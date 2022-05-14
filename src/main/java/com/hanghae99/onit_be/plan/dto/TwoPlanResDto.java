package com.hanghae99.onit_be.plan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class TwoPlanResDto {
    private PlanListResDto.PlanListsResDto myPlanList;
    private PlanListResDto.PlanListsResDto invitedPlanList;


    public TwoPlanResDto(PlanListResDto.PlanListsResDto myPlanListsResDto, PlanListResDto.PlanListsResDto invitedPlanListsResDto) {
        this.myPlanList = myPlanListsResDto;
        this.invitedPlanList = invitedPlanListsResDto;
    }

}
