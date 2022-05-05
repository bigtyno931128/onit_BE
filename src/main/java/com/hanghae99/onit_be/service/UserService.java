package com.hanghae99.onit_be.service;

import com.hanghae99.onit_be.dto.response.IdCheckResDto;
import com.hanghae99.onit_be.dto.request.LoginReqDto;
import com.hanghae99.onit_be.dto.request.SignupReqDto;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.entity.UserRoleEnum;
import com.hanghae99.onit_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //회원가입 수정 .
    @Transactional
    public User registerUser(SignupReqDto requestDto) {

        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();

        // 중복 아이디 확인   == 프론트 분들과 얘기 필요 유효성검증 어떤 부분에서 할 것인지 /
        if (userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("이미 사용중인 아이디 입니다!");
        }

//        if (userRepository.existsByUserNickname(userNickname)){
//            throw new IllegalArgumentException("이미 사용중인 닉네임 입니다!");
//
//        }

        if(!username.matches("^[a-z0-9-_]{3,10}$")){
            throw new IllegalArgumentException("아이디는 영어와 숫자로 3~9자리로 입력하셔야 합니다!");
        }
        if(!requestDto.getPassword().matches("^[a-z0-9-_]{4,10}$")){
            throw new IllegalArgumentException("비빌번호는 영어와 숫자로 4~12 자리로 입력하셔야 합니다!");
        }

        //사용자 ROLE 을 생성 하는 부분 추가 .
        UserRoleEnum role = UserRoleEnum.USER;
        //사용자 profileImg 기본 이미지 부여
        User user = new User(username, password,nickname,role,"https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profileImg_default.png");
        return userRepository.save(user);
    }

    //아이디 중복검사
    public IdCheckResDto vaildId(LoginReqDto requestDto) {
        String username = requestDto.getUsername();
        IdCheckResDto idCheckDto = new IdCheckResDto();
        idCheckDto.setResult(!userRepository.existsByUsername(username));
        return idCheckDto;
    }


//    public UserInfoDto getUserInfo(UserDetailsImpl userDetails) {
//        return UserInfoDto;
//    }
}