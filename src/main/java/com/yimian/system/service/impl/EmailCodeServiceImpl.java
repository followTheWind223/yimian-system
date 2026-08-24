package com.yimian.system.service.impl;

import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.entity.User;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.EmailCodeService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCodeServiceImpl implements EmailCodeService {

    private static final String PURPOSE_REGISTER = "register";
    private static final String PURPOSE_RESET_PASSWORD = "reset-password";
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration DEFAULT_COOLDOWN = Duration.ofSeconds(60);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectProvider<JavaMailSender> javaMailSenderProvider;
    private final UserMapper userMapper;

    @Value("${email-code.mail-enabled:false}")
    private boolean mailEnabled;

    @Value("${email-code.from:}")
    private String from;

    @Value("${email-code.cooldown-seconds:60}")
    private long cooldownSeconds;

    @Override
    public void sendRegisterCode(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (userMapper.selectByEmail(normalizedEmail) != null) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        sendCode(normalizedEmail, PURPOSE_REGISTER, "易面注册验证码");
    }

    @Override
    public void sendResetPasswordCode(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userMapper.selectByEmail(normalizedEmail);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        sendCode(normalizedEmail, PURPOSE_RESET_PASSWORD, "易面重置密码验证码");
    }

    @Override
    public void verifyRegisterCode(String email, String code) {
        verifyCode(normalizeEmail(email), code, PURPOSE_REGISTER);
    }

    @Override
    public void verifyResetPasswordCode(String email, String code) {
        verifyCode(normalizeEmail(email), code, PURPOSE_RESET_PASSWORD);
    }

    private void sendCode(String email, String purpose, String subject) {
        String cooldownKey = cooldownKey(purpose, email);
        Duration cooldown = cooldownSeconds > 0 ? Duration.ofSeconds(cooldownSeconds) : DEFAULT_COOLDOWN;
        Boolean allowed = stringRedisTemplate.opsForValue().setIfAbsent(cooldownKey, "1", cooldown);
        if (Boolean.FALSE.equals(allowed)) {
            throw new BusinessException(ResultCode.EMAIL_CODE_SEND_TOO_FREQUENT);
        }

        String code = generateCode();
        stringRedisTemplate.opsForValue().set(codeKey(purpose, email), code, CODE_TTL);

        if (mailEnabled) {
            JavaMailSender sender = javaMailSenderProvider.getIfAvailable();
            if (sender == null) {
                throw new BusinessException(ResultCode.ERROR, "邮件服务未配置");
            }
            sendMail(sender, email, purpose, subject, code);
        } else {
            log.info("Email code generated. purpose={}, email={}, code={}", purpose, email, code);
        }
    }

    private void sendMail(JavaMailSender sender, String email, String purpose, String subject, String code) {
        MimeMessage message = sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            if (from != null && !from.isBlank()) {
                helper.setFrom(new InternetAddress(from, "易面", StandardCharsets.UTF_8.name()));
            }
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(buildPlainText(code), buildHtmlText(purpose, code));
            sender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.warn("Email code send failed. purpose={}, email={}, reason={}", purpose, email, e.getMessage());
            throw new BusinessException(ResultCode.ERROR, "邮件发送失败，请稍后重试");
        }
    }

    private String buildPlainText(String code) {
        return "您的验证码是：" + code + "。验证码 5 分钟内有效，请勿泄露给他人。";
    }

    private String buildHtmlText(String purpose, String code) {
        String title = PURPOSE_RESET_PASSWORD.equals(purpose) ? "重置密码验证码" : "注册验证码";
        String action = PURPOSE_RESET_PASSWORD.equals(purpose) ? "重置账号密码" : "完成账号注册";
        return """
                <!doctype html>
                <html lang="zh-CN">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>%s</title>
                </head>
                <body style="margin:0;padding:0;background:#eef5f8;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI','Microsoft YaHei',Arial,sans-serif;color:#183241;">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#eef5f8;padding:32px 12px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:560px;background:#ffffff;border-radius:14px;overflow:hidden;border:1px solid #d8e7ee;box-shadow:0 10px 28px rgba(16,54,72,0.08);">
                          <tr>
                            <td style="padding:28px 32px;background:#0f6f94;background:linear-gradient(135deg,#0f789c,#14354f);">
                              <div style="font-size:14px;line-height:20px;color:#b9ddeb;">YiMian</div>
                              <div style="font-size:26px;line-height:34px;font-weight:700;color:#ffffff;margin-top:6px;">易面%s</div>
                              <div style="font-size:14px;line-height:22px;color:#d6eef7;margin-top:8px;">智能面试知识社区</div>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px;">
                              <div style="font-size:18px;line-height:26px;font-weight:700;color:#183241;">请使用以下验证码%s</div>
                              <div style="margin:24px 0 18px;padding:22px 20px;border-radius:12px;background:#f4fbfe;border:1px solid #cce8f3;text-align:center;">
                                <div style="font-size:13px;line-height:20px;color:#587384;">验证码</div>
                                <div style="font-size:40px;line-height:48px;font-weight:800;letter-spacing:8px;color:#0d8ec2;margin-top:6px;font-family:'Segoe UI',Arial,sans-serif;">%s</div>
                              </div>
                              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:18px;">
                                <tr>
                                  <td style="padding:14px 16px;border-radius:10px;background:#fff8e8;color:#6f5420;font-size:14px;line-height:22px;">
                                    验证码 5 分钟内有效。为保障账号安全，请不要转发或泄露给他人。
                                  </td>
                                </tr>
                              </table>
                              <div style="font-size:13px;line-height:22px;color:#7b909c;margin-top:24px;">
                                如果这不是你本人操作，可以忽略本邮件。
                              </div>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:18px 32px;background:#f7fafc;border-top:1px solid #e4eef3;color:#7b909c;font-size:12px;line-height:20px;">
                              本邮件由系统自动发送，请勿直接回复。
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(title, title, action, code);
    }

    private void verifyCode(String email, String code, String purpose) {
        String key = codeKey(purpose, email);
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached == null || code == null || !cached.equals(code.trim())) {
            throw new BusinessException(ResultCode.EMAIL_CODE_INVALID);
        }
        stringRedisTemplate.delete(key);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String codeKey(String purpose, String email) {
        return "auth:email-code:" + purpose + ":" + email;
    }

    private String cooldownKey(String purpose, String email) {
        return "auth:email-code:cooldown:" + purpose + ":" + email;
    }

    private String generateCode() {
        int value = RANDOM.nextInt(1_000_000);
        return String.format("%06d", value);
    }
}
