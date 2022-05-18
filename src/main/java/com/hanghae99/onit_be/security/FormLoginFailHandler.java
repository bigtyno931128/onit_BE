package com.hanghae99.onit_be.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FormLoginFailHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMsg = "제발";

        if(exception instanceof UsernameNotFoundException) {
            errorMsg = "존재하지 않는 아이디 입니다.";

        }else if(exception instanceof BadCredentialsException) {
            errorMsg = "비밀번호가 맞지 않습니다.";
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> data = new HashMap<>();
        System.out.println(errorMsg);
        data.put(
                "exception",
                errorMsg);
        System.out.println(exception.toString());
        String str = new String(objectMapper.writeValueAsString(data).getBytes("UTF-8"), "ISO-8859-1");
        response.getOutputStream()
                .println(str);

    }
}


