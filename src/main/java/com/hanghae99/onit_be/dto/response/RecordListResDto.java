package com.hanghae99.onit_be.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecordListResDto {
    private List<RecordResDto> recordResList;
    private int totalPage;
    private int currentPage;

    public RecordListResDto(Page<RecordResDto> planHistory) {
        this.recordResList = planHistory.getContent();
        this.totalPage = planHistory.getTotalPages();
        this.currentPage = planHistory.getNumber();
    }
}
