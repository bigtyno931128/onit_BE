package com.hanghae99.onit_be.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Setter
@Getter
@Data
public class SignupReqDto {

    @NotBlank(message = "아이디를 입력해 주세요!")
    @Pattern(regexp = "^[a-z0-9-_]{3,10}$", message = "아이디는 영어와 숫자만 입력이 가능합니다!")
    private String username;

    @NotBlank(message = "비밀번호를 입력해 주세요!")
    @Size(min = 4, max = 12, message = "비밀번호는 4자 이상 12자 이하로 입력해주세요!")
    private String password;

    @NotBlank(message = "닉네임을 입력해 주세요!")
    @Size(min = 3, max = 12, message = "닉네임은 3자 이상 12자 이하로 입력해주세요!")
    private String nickname;
}