package com.hanghae99.onit_be.mypage;

import com.hanghae99.onit_be.aop.LogExecutionTime;
import com.hanghae99.onit_be.mypage.dto.ProfileResDto;
import com.hanghae99.onit_be.mypage.dto.RecordListResDto;
import com.hanghae99.onit_be.plan.dto.PlanDetailResDto;
import com.hanghae99.onit_be.common.ResultDto;
import com.hanghae99.onit_be.plan.dto.PlanListResDto;
import com.hanghae99.onit_be.security.UserDetailsImpl;
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

    // 지난 일정 조회
    @GetMapping("/member/history/{pageno}")
    public ResponseEntity<ResultDto<RecordListResDto>> getPlanHistory(@PathVariable int pageno,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        RecordListResDto recordListResDto = new RecordListResDto(mypageService.getPlanHistory(userDetails.getUser(), pageno - 1));
        return ResponseEntity.ok().body(new ResultDto<>("지난 일정 조회 성공!", recordListResDto));
    }

    // 내가 참여한 일정 삭제 .
    @LogExecutionTime
    @DeleteMapping("/invitation/{randomUrl}")
    public ResponseEntity<ResultDto> deletePlan (@PathVariable("randomUrl") String url, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        mypageService.deleteInvitationPlan(url, userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto("내가 참여한 일정 삭제 성공!"));
    }

    // 내가 참여한 일정 리스트
    @LogExecutionTime
    @GetMapping("/invitation/plans/{pageno}")
    public PlanListResDto.PlanListsResDto getInvitationPlansList (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             @PathVariable int pageno) {
        return mypageService.getInvitationPlansList(userDetails.getUser(),pageno-1);
    }
}