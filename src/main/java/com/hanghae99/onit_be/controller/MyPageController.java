package com.hanghae99.onit_be.controller;

import com.hanghae99.onit_be.dto.response.ProfileResDto;
import com.hanghae99.onit_be.dto.response.ResultDto;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import com.hanghae99.onit_be.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService mypageService;

    @PutMapping("/member/profile")
    public ResponseEntity<ResultDto<ProfileResDto>> updateImg (@RequestParam("profileImg") MultipartFile multipartFile,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        mypageService.updateProfile(multipartFile, userDetails);
        return ResponseEntity.ok().body(new ResultDto<>("프로필 이미지 수정 성공!"));
    }
}