package com.yimian.system.common.aspect;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.entity.OperationLogRecord;
import com.yimian.system.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 操作日志 AOP 切面
 * 拦截 @OperationLog 注解的方法，自动记录操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final ParameterNameDiscoverer NAME_DISCOVERER =
            new DefaultParameterNameDiscoverer();

    private static final Pattern SPEL_PATTERN = Pattern.compile("#\\{([^}]+)\\}");

    /** 敏感字段关键词（不区分大小写，去符号） */
    private static final Set<String> SENSITIVE_KEYWORDS = Set.of(
            "password", "passwd", "pwd", "secret", "token",
            "oldpassword", "newpassword", "confirmpassword",
            "accesstoken", "refreshtoken", "authorization"
    );

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog opLog) throws Throwable {
        long start = System.currentTimeMillis();

        // ---- 解析请求信息 ----
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;

        String requestUri = request != null ? request.getRequestURI() : "";
        String httpMethod = request != null ? request.getMethod() : "";
        String ip = request != null ? JakartaServletUtil.getClientIP(request) : "";
        String userAgent = request != null ? request.getHeader("User-Agent") : "";

        // ---- 解析方法签名 ----
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String classMethod = method.getDeclaringClass().getName() + "." + method.getName();

        // ---- 构建日志 ----
        OperationLogRecord record = new OperationLogRecord();
        record.setModule(opLog.module());
        record.setOperation(opLog.operation());
        record.setMethod(httpMethod);
        record.setRequestUri(requestUri);
        record.setClassMethod(classMethod);
        record.setIp(ip);
        record.setUserAgent(userAgent);

        // 解析 SpEL 描述
        String description = resolveSpEL(opLog.description(), joinPoint, method);
        record.setDescription(description.isEmpty() ? null : description);

        // 操作人
        fillUserInfo(record);

        // 请求参数（脱敏）
        if (opLog.logParams()) {
            record.setRequestParams(serializeArgs(joinPoint.getArgs(), method));
        }

        // ---- 执行业务方法 ----
        Object result = null;
        try {
            result = joinPoint.proceed();
            record.setStatus(1);
        } catch (Throwable e) {
            record.setStatus(0);
            record.setErrorMsg(truncate(e.getMessage(), 2000));
            throw e;
        } finally {
            record.setDuration(System.currentTimeMillis() - start);
        }

        // ---- 填充返回值信息 ----
        fillResultInfo(record, result, opLog);

        // ---- 异步写入 ----
        try {
            operationLogService.save(record);
        } catch (Exception e) {
            log.error("操作日志写入失败: {}", e.getMessage());
        }

        return result;
    }

    // ==================== 操作人 ====================

    private void fillUserInfo(OperationLogRecord record) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return;
        if (auth.getPrincipal() instanceof UserDetails ud) {
            record.setUsername(ud.getUsername());
        }
        if (auth.getPrincipal() instanceof com.yimian.system.security.JwtUserDetails jud) {
            record.setUserId(jud.getUser().getId());
        }
    }

    // ==================== 返回值 ====================

    private void fillResultInfo(OperationLogRecord record, Object result, OperationLog opLog) {
        if (result == null) return;
        // 从 Result 中提取业务码和消息
        try {
            Method getCode = result.getClass().getMethod("getCode");
            Method getMessage = result.getClass().getMethod("getMessage");
            Object code = getCode.invoke(result);
            Object msg = getMessage.invoke(result);
            if (code instanceof Integer ci) record.setResultCode(ci);
            if (msg instanceof String ms) record.setResultMsg(ms);
        } catch (Exception ignored) {
            // 返回值不是 Result 类型，跳过
        }
    }

    // ==================== 参数序列化 & 脱敏 ====================

    private String serializeArgs(Object[] args, Method method) {
        if (args == null || args.length == 0) return null;

        String[] paramNames = resolveParamNames(method);
        Map<String, Object> paramMap = new LinkedHashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) continue;
            if (args[i] instanceof HttpServletRequest
                    || args[i] instanceof HttpServletResponse
                    || args[i] instanceof MultipartFile) {
                continue;
            }
            String name = i < paramNames.length ? paramNames[i] : "arg" + i;
            paramMap.put(name, desensitize(args[i]));
        }

        if (paramMap.isEmpty()) return null;

        try {
            return truncate(OBJECT_MAPPER.writeValueAsString(paramMap), 5000);
        } catch (JsonProcessingException e) {
            log.warn("请求参数序列化失败: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Object desensitize(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey() != null ? entry.getKey().toString() : "";
                result.put(key, isSensitive(key) ? "***" : desensitize(entry.getValue()));
            }
            return result;
        }

        if (!isSimpleType(obj.getClass())) {
            try {
                String json = OBJECT_MAPPER.writeValueAsString(obj);
                Map<String, Object> map = OBJECT_MAPPER.readValue(json, Map.class);
                return desensitize(map);
            } catch (Exception e) {
                return obj.toString();
            }
        }

        return obj;
    }

    private boolean isSensitive(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) return false;
        String lower = fieldName.toLowerCase().replaceAll("[-_]", "");
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (lower.contains(keyword)) return true;
        }
        return false;
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.getName().startsWith("java.")
                || clazz.isEnum();
    }

    // ==================== 参数名 ====================

    private String[] resolveParamNames(Method method) {
        String[] names = NAME_DISCOVERER.getParameterNames(method);
        if (names != null && names.length > 0) return names;
        return Arrays.stream(method.getParameters())
                .map(p -> p.isNamePresent() ? p.getName() : "arg")
                .toArray(String[]::new);
    }

    // ==================== SpEL ====================

    private String resolveSpEL(String template, ProceedingJoinPoint joinPoint, Method method) {
        if (template == null || template.isEmpty() || !template.contains("#{")) {
            return template != null ? template : "";
        }

        String[] paramNames = resolveParamNames(method);
        Object[] args = joinPoint.getArgs();

        Map<String, Object> context = new LinkedHashMap<>();
        for (int i = 0; i < paramNames.length && i < args.length; i++) {
            context.put(paramNames[i], args[i]);
        }

        Matcher matcher = SPEL_PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String expr = matcher.group(1).trim(); // 如 #dto.username
            Object value = evalExpr(expr, context);
            matcher.appendReplacement(sb, value != null ? Matcher.quoteReplacement(value.toString()) : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Object evalExpr(String expression, Map<String, Object> context) {
        String trimmed = expression.startsWith("#") ? expression.substring(1) : expression;
        String[] parts = trimmed.split("\\.", 2);
        Object current = context.get(parts[0]);
        if (current == null || parts.length < 2) return current;
        return getProperty(current, parts[1]);
    }

    private Object getProperty(Object obj, String path) {
        if (obj == null) return null;
        for (String prop : path.split("\\.")) {
            if (obj == null) return null;
            try {
                String getter = "get" + Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
                obj = obj.getClass().getMethod(getter).invoke(obj);
            } catch (Exception e) {
                return null;
            }
        }
        return obj;
    }

    // ==================== 工具方法 ====================

    private String truncate(String str, int maxLen) {
        if (str == null) return null;
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen) + "...(truncated)";
    }
}
