package com.hanghae99.onit_be.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
public class PlanListResDto implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private List<PlanResDto> planList;
    private int totalPage;
    private int currentPage;

    public PlanListResDto(Page<PlanResDto> page) {
        this.planList = page.getContent();
        this.totalPage = page.getTotalPages();
        this.currentPage = page.getNumber();
    }
}
