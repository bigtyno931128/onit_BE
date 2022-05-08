package com.hanghae99.onit_be.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecordResDto {

    private Long planId;
    private String planName;
    private String planDate;
    private String address;
    private String penalty;

    public RecordResDto(Long planId, String planName, String planDateCv, String address, String penalty) {
        this.planId = planId;
        this.planName = planName;
        this.planDate = planDateCv;
        this.address = address;
        this.penalty = penalty;
    }
}
