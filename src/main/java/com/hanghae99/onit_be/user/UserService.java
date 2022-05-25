package com.hanghae99.onit_be.user;

import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.entity.UserRoleEnum;
import com.hanghae99.onit_be.plan.PlanRepository;

import com.hanghae99.onit_be.user.dto.IdCheckResDto;
import com.hanghae99.onit_be.user.dto.LoginReqDto;
import com.hanghae99.onit_be.user.dto.SignupReqDto;
import com.hanghae99.onit_be.common.utils.Valid;
import com.hanghae99.onit_be.user.dto.UserInfoResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,PlanRepository planRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.planRepository = planRepository;
    }

    //회원가입 수정 .
    public void registerUser(SignupReqDto requestDto) {

        Valid.validUser(requestDto);

        if (userRepository.existsByUsername(requestDto.getUsername())){
            throw new IllegalArgumentException("이미 사용중인 아이디 입니다!");
        }
        if (userRepository.existsByNickname(requestDto.getNickname())){
            throw new IllegalArgumentException("이미 사용중인 닉네임 입니다!");
        }

        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();

        //사용자 ROLE 을 생성 하는 부분 추가 .
        UserRoleEnum role = UserRoleEnum.USER;
        //사용자 profileImg 기본 이미지 부여
        User user = new User(username, password,nickname,role,"https://onit-bucket.s3.ap-northeast-2.amazonaws.com/profile_default.png");
        //return userRepository.save(user);
        userRepository.save(user);
    }

    // 아이디 중복검사
    public IdCheckResDto vaildId(LoginReqDto requestDto) {
        String username = requestDto.getUsername();
        IdCheckResDto idCheckDto = new IdCheckResDto();
        idCheckDto.setResult(userRepository.existsByUsername(username));
        return idCheckDto;
    }

    // 클라이언트로 부터 devicetoken 을 받을시에 user 테이블에 devicetoken 저장, token이 존재하면 알림여부 true , 없다면 false
    @Transactional
    public void updateDeviceToken(String token, Long id) {
        User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if (token != null) {
            user.setNoticeAllowedTrue();
        }
        else {
            user.setNoticeAllowedFalse();
        }
        user.updateToken(token);
    }

    // 회원 정보
    public UserInfoResDto getUserInfo(User user) {
        User userInfo = userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new);
        String profile = "https://onit-bucket.s3.ap-northeast-2.amazonaws.com/" + userInfo.getProfileImg();
        return new UserInfoResDto(user, profile);
    }
}