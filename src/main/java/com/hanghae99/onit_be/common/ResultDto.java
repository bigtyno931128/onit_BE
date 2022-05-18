package com.hanghae99.onit_be.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 필드 노출 X
public class ResultDto<T> {
    private final boolean result = true;
    private String message;
    private T data;

    public ResultDto(String message) {
        this.message = message;
    }

    public ResultDto(String message, T dto) {
        this.message = message;
        this.data = dto;
    }
}
