package com.gabojait.gabojaitspring.admin.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@GroupSequence({AdminRegisterReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "관리자 가입 요청")
public class AdminRegisterReqDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "_admin")
    @NotBlank(message = "아이디는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[a-z0-9])[a-z0-9]+_admin$",
            message = "아이디는 소문자 영어와 숫자의 조합 그리고 '_admin'으로 끝나게 입력해 주세요.",
            groups = ValidationSequence.Format.class)
    private String adminName;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)(?=.*[#$@!%&*?])[A-z\\d#$@!%&*?]+$",
            message = "비밀번호는 영어, 숫자, 특수문자(#$@!%&*?)의 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String password;

    @ApiModelProperty(position = 3, required = true, value = "비밀번호 재입력", example = "password1!")
    @NotBlank(message = "비밀번호 재입력은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String passwordReEntered;

    @ApiModelProperty(position = 3, required = true, value = "실명", example = "김가보자잇")
    @NotBlank(message = "실명은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 5, message = "실명은 1~5자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "실명은 한글 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String legalName;

    @ApiModelProperty(position = 5, required = true, value = "성별", example = "M", allowableValues = "M, F")
    @NotBlank(message = "성별은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(M|F)", message = "성별은 'M', 'F' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String gender;

    @ApiModelProperty(position = 6, required = true, notes = "string", value = "생년월일", example = "2000-01-01")
    @NotNull(message = "생년월일은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    public User toEntity(String password) {
        return User.adminBuilder()
                .username(this.adminName)
                .password(password)
                .legalName(this.legalName)
                .gender(Gender.valueOf(this.gender))
                .birthdate(this.birthdate)
                .build();
    }
}