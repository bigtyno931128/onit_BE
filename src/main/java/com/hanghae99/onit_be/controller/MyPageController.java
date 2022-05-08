package com.hanghae99.onit_be.controller;

import com.hanghae99.onit_be.dto.response.*;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import com.hanghae99.onit_be.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService mypageService;

    // 프로필 이미지 수정
    @PutMapping("/member/profile")
    public ResponseEntity<ResultDto<ProfileResDto>> updateImg (@RequestParam("profileImg") MultipartFile multipartFile,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        mypageService.updateProfile(multipartFile, userDetails);
        return ResponseEntity.ok().body(new ResultDto<>("프로필 이미지 수정 성공!"));
    }

    // 지난 일정 조회
    @GetMapping("/member/history/{pageno}")
    public ResponseEntity<ResultDto<RecordListResDto>> getPlanHistory(@PathVariable int pageno,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        RecordListResDto recordListResDto = new RecordListResDto(mypageService.getPlanHistory(userDetails.getUser(), pageno - 1));
        return ResponseEntity.ok().body(new ResultDto<>("지난 일정 조회 성공!", recordListResDto));
    }


    // 링크 공유를 통한 약속 저장
    @PostMapping("/invitation/{randomUrl}")
    public ResponseEntity<ResultDto> savePlanInvitation(@PathVariable("randomUrl") String url,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        mypageService.savePlanInvitation(url, userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto<>("내 일정에 저장 성공!"));
    }
}