package com.hanghae99.onit_be.plan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class PlanListResDto  {

    private List<PlanResDto> planList;
    private int totalPage;
    private int currentPage;

    public PlanListResDto(Page<PlanResDto> page) {
        this.planList = page.getContent();
        this.totalPage = page.getTotalPages();
        this.currentPage = page.getNumber();
    }
}
