package com.hanghae99.onit_be.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.onit_be.common.ResultDto;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import com.hanghae99.onit_be.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;

    @Autowired
    public UserController(UserService userService, KakaoUserService kakaoUserService) {
        this.userService = userService;
        this.kakaoUserService = kakaoUserService;
    }

    // 회원 가입 요청 처리
    @PostMapping("/user/signup")
    public ResponseEntity<Object> registerUser(@RequestBody SignupReqDto requestDto) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok().body(new ResultDto<>("회원가입 성공!"));
    }

    //아이디 중복 검사
    @PostMapping("/api/idCheck")
    public IdCheckResDto vaildId(@RequestBody LoginReqDto requestDto) {
        return userService.vaildId(requestDto);
    }


    //회원 정보
//    @GetMapping("/api/user/info")
//    public UserInfoResDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
//        return userService.getUserInfo(userDetails.getUser());
//    }


    // 카카오 로그인
    @GetMapping("/users/kakao/callback")
    public KakaoUserInfoResDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println(code);
        return kakaoUserService.kakaoLogin(code, response);
    }

    //device token 저장
    @PostMapping("/member/devices")
    public ResponseEntity<Object> updateDeviceToken(@RequestBody @Valid DeviceTokenReqDto requestDto,
                                                    BindingResult bindingResult,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.updateDeviceToken(requestDto.getToken(), userDetails.getUser().getId());
        return ResponseEntity.ok().body(new ResultDto<>("저장 완료"));
    }

}