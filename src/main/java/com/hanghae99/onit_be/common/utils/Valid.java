package com.hanghae99.onit_be.common.utils;

import com.hanghae99.onit_be.user.dto.SignupReqDto;
import com.hanghae99.onit_be.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Valid {

    private final UserRepository userRepository;

    @Autowired
    public Valid(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public static boolean validWriter(boolean plan, String s) {
        if(plan){
            throw new IllegalArgumentException(s);
        }
        return false;
    }
    
    //회원가입 유효성 검사
    public static void validUser(SignupReqDto requestDto) throws IllegalArgumentException {

        if(!requestDto.getUsername().matches("^[a-z0-9-_]{3,10}$")){
            throw new IllegalArgumentException("아이디는 영어와 숫자로 3~9자리로 입력하셔야 합니다!");
        }
        if(!requestDto.getPassword().matches("^[a-z0-9-_]{4,10}$")){
            throw new IllegalArgumentException("비빌번호는 영어와 숫자로 4~12 자리로 입력하셔야 합니다!");
        }
    }
}
