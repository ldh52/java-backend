package com.lion.demo.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "아이디 또는 비밀번호가 잘못되었습니다.";

        // 예외 메시지 확인
        if (exception.getMessage().contains("사용자를 찾을 수 없습니다")) {
            errorMessage = "존재하지 않는 아이디입니다.";
        }

        // 실패 메시지 전달
        request.getSession().setAttribute("error", errorMessage);

        // 리다이렉션 사용
        response.sendRedirect("/user/login");
    }
}
