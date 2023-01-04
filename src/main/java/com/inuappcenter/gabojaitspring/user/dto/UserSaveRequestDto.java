package com.inuappcenter.gabojaitspring.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.Gender;
import com.inuappcenter.gabojaitspring.user.domain.Role;
import com.inuappcenter.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@GroupSequence({UserSaveRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class,
        ValidationSequence.Email.class})
@ApiModel(value = "User 생성 요청")
public class UserSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "아이디", example = "username")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = "아이디는 영문과 숫자의 조합만 가능합니다.",
            groups = ValidationSequence.Pattern.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, dataType = "String", value = "비밀번호", example = "password")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = "비밀번호는 영문과 숫자의 조합만 가능합니다.",
            groups = ValidationSequence.Pattern.class)
    private String password;

    @ApiModelProperty(position = 3, required = true, dataType = "String", value = "비밀번호 재입력", example = "password")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    private String passwordReEntered;

    @ApiModelProperty(position = 3, required = true, dataType = "String", value = "실명", example = "실명")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 2, max = 5, message = "실명은 2~5자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String legalName;

    @ApiModelProperty(position = 4, required = true, dataType = "String", value = "닉네임", example = "닉네임")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String nickname;

    @ApiModelProperty(position = 5,
            required = true,
            dataType = "Character",
            allowableValues = "M, F",
            value = "성별: M, F",
            example = "M")
    @NotNull(message = "모든 성별 선택은 필수입니다.", groups = ValidationSequence.NotNull.class)
    private Character gender;

    @ApiModelProperty(position = 6, required = true, dataType = "LocalDate", value = "생년월일", example = "2000-01-01")
    @NotNull(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @ApiModelProperty(position = 7, required = true, dataType = "String", value = "이메일", example = "email@domain.com")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    @Email(message = "올바른 이메일 형식이 아닙니다.", groups = ValidationSequence.Email.class)
    private String email;

    public User toEntity(String password, Gender gender, Contact contact) {
        return User.builder()
                .username(this.username)
                .password(password)
                .legalName(this.legalName)
                .nickname(this.nickname)
                .gender(gender)
                .birthdate(this.birthdate)
                .role(Role.USER)
                .contact(contact)
                .build();
    }
}
