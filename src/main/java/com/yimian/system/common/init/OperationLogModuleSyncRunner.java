package com.yimian.system.common.init;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.cache.LogModuleCache;
import com.yimian.system.entity.LogModule;
import com.yimian.system.mapper.LogModuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 启动时扫描所有 @OperationLog 注解，把 module 编码自动 upsert 到 sys_log_module
 * 编码已存在 → 跳过（不覆盖用户改过的中文名/排序）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogModuleSyncRunner implements ApplicationRunner {

    private final ApplicationContext applicationContext;
    private final LogModuleMapper logModuleMapper;
    private final LogModuleCache logModuleCache;

    @Override
    public void run(ApplicationArguments args) {
        Set<String> moduleCodes = new HashSet<>();

        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(RestController.class);
        for (Object bean : controllers.values()) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            // AOP 代理类上的桥接方法不保留原方法注解，必须扫描目标 Controller 类。
            for (Method method : clazz.getDeclaredMethods()) {
                OperationLog annotation = method.getAnnotation(OperationLog.class);
                if (annotation != null) {
                    moduleCodes.add(annotation.module());
                }
            }
        }

        int added = 0;
        for (String code : moduleCodes) {
            LogModule exist = logModuleMapper.selectByCode(code);
            if (exist == null) {
                LogModule m = new LogModule();
                m.setCode(code);
                m.setName(code); // 默认 name = code，之后到字典页改成中文
                m.setEnabled(1);
                m.setSort(99);
                logModuleMapper.insert(m);
                added++;
                log.info("自动创建日志模块字典: code={} name={}", code, code);
            }
        }

        if (added > 0) {
            log.info("操作日志模块同步完成，新增 {} 条，总计 {} 条", added, moduleCodes.size());
        } else {
            log.info("操作日志模块同步完成，{} 条模块均已存在，无需新增", moduleCodes.size());
        }

        logModuleCache.refresh();
    }
}
