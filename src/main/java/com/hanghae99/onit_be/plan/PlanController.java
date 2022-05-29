package com.hanghae99.onit_be.plan;

import com.hanghae99.onit_be.aop.LogExecutionTime;
import com.hanghae99.onit_be.common.ResultDto;
import com.hanghae99.onit_be.plan.dto.*;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PlanController {

    private final PlanService planService;

    //일정 만들기
    @LogExecutionTime
    @PostMapping("/member/plan")
    public ResponseEntity<ResultDto> createPlan (@RequestBody PlanReqDto planReqDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        planService.createPlan(planReqDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto<>("일정 등록 성공!"));
    }

    // 일정 목록 조회 (전체 / 내가 만든 일정/초대된 일정)
    @LogExecutionTime
    @GetMapping("/member/plans/{pageno}")
    public TwoPlanResDto getPlansList (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @PathVariable int pageno) {
        return planService.getPlansList(userDetails.getUser(),pageno-1);
    }

    // 일정 상세 조회 (내가 만든)
    @LogExecutionTime
    @GetMapping("/member/plan/{randomUrl}")
    public ResponseEntity<ResultDto<PlanDetailResDto>> getPlan (@PathVariable("randomUrl") String url,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println("id=?" + userDetails.getUser().getId());
        PlanDetailResDto planDetailResDto = planService.getPlan(url,userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto<>("일정 상세 조회 성공!", planDetailResDto));
    }

    // 일정 수정
    @LogExecutionTime
    @PutMapping("/member/plan/{randomUrl}")
    public ResponseEntity<ResultDto> editPlan (@PathVariable("randomUrl") String url, @RequestBody PlanReqDto planReqDto,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        planService.editPlan(url, planReqDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto("일정 수정 성공!"));
    }

    // 일정 삭제
    @LogExecutionTime
    @DeleteMapping("/member/plan/{randomUrl}")
    public ResponseEntity<ResultDto> deletePlan (@PathVariable("randomUrl") String url, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        planService.deletePlan(url, userDetails.getUser());
        return ResponseEntity.ok().body(new ResultDto("일정 삭제 성공!"));
    }


    // 일정 목록 조회 (내가 만든 일정)
    @LogExecutionTime
    @GetMapping("/member/myplans/{pageno}")
    public PlanListResDto.PlanListsResDto getMyPlansList (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable int pageno) {
        return planService.getMyPlansList(userDetails.getUser(),pageno-1);
    }

    // 전체 일정 목록 조회 (내가 만든 일정 + 내가 참여한 일정)
    @LogExecutionTime
    @GetMapping("/member/totalplans/{pageno}")
    public PlanListResDto.PlanListsResDto getTotalPlansList (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable int pageno) {
        return planService.getTotalPlansList(userDetails.getUser(),pageno-1);
    }
}

