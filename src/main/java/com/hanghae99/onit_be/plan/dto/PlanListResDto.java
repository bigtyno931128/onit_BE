package com.hanghae99.onit_be.plan.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PlanListResDto {


    private List<PlanResDto> planList;
    private int totalPage;
    private int currentPage;

    public PlanListResDto(Page<PlanResDto> page) {
        this.planList = page.getContent();
        this.totalPage = page.getTotalPages();
        this.currentPage = page.getNumber();
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class PlanListsResDto {

        private List<PlanResDto.MyPlanDto> planLists;
        private int totalPage;
        private int currentPage;

        public PlanListsResDto(Page<PlanResDto.MyPlanDto> myPlanPage) {
            this.planLists = myPlanPage.getContent();
            this.totalPage = myPlanPage.getTotalPages();;
            this.currentPage = myPlanPage.getNumber();
        }

    }
}
