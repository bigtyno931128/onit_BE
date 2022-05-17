package com.hanghae99.onit_be.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.onit_be.user.UserRepository;
import com.hanghae99.onit_be.security.jwt.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


// 3번 . ( 로그인 성공 하면 )
public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;

    @Autowired
    public FormLoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {

        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());

        // 4번 호출.
        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails);


        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);
        response.setStatus(HttpServletResponse.SC_OK);


        Map<String,Object> data = new HashMap<>();
        data.put(
                "id",
                userDetails.getUser().getId());
        data.put(
                "nickname",
                userDetails.getNickName());
        data.put(
                "profileImg",
                userDetails.getProfileImg());
        String str = new String(objectMapper.writeValueAsString(data).getBytes("UTF-8"), "ISO-8859-1");
        response.getOutputStream()
                .println(str);
    }

}
