package com.yimian.system.service;

public interface EmailCodeService {

    void sendRegisterCode(String email);

    void sendResetPasswordCode(String email);

    void verifyRegisterCode(String email, String code);

    void verifyResetPasswordCode(String email, String code);
}
