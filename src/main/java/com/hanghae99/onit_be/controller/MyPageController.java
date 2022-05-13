package com.hanghae99.onit_be.controller;

import com.hanghae99.onit_be.dto.response.PlanDetailResDto;
import com.hanghae99.onit_be.dto.response.ProfileResDto;
import com.hanghae99.onit_be.dto.response.ResultDto;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import com.hanghae99.onit_be.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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


    // 링크 공유를 통한 약속 저장
    @PostMapping("/invitation/{randomUrl}")
    public ResponseEntity<ResultDto> savePlanInvitation(@PathVariable("randomUrl") String url,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        mypageService.savePlanInvitation(url, userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto<>("내 일정에 저장 성공!"));
    }


    //내가 참여한 plan detail.
    @GetMapping("/invitation/{randomUrl}")
    public ResponseEntity<ResultDto<PlanDetailResDto>> getPlanInvitation(@PathVariable("randomUrl") String url,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PlanDetailResDto planDetailResDto = mypageService.getPlanInvitation(url, userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto<>("내가 참여한 일정 불러오기 성공!", planDetailResDto));
    }
}