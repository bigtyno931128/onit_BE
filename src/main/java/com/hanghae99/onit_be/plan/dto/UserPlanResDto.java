package com.hanghae99.onit_be.plan.dto;

import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.plan.dto.PlanResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserPlanResDto {
    private String ninkName;
    private List<PlanResDto> planList;

    public UserPlanResDto(List<PlanResDto> planResDtoList, User user) {
        this.ninkName = user.getNickname();
        this.planList = planResDtoList;
    }
}
