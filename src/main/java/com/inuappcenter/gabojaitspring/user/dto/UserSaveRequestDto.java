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
        ValidationSequence.Pattern.class,
        ValidationSequence.Email.class})
@ApiModel(value = "User 생성 요청")
public class UserSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "username",
            allowableValues = "Restriction: [NotBlank > Size > Pattern]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)[A-z\\d]+$", message = "아이디는 영문과 숫자의 조합의 형식만 가능합니다.",
            groups = ValidationSequence.Pattern.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password",
            allowableValues = "Restriction: [NotBlank > Size > Pattern]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)(?=.*[#$@!%&*?])[A-z\\d#$@!%&*?]+$",
            message = "비밀번호는 영문, 숫자, 특수문자(#$@!%&*?)의 조합의 형식만 가능합니다.", groups = ValidationSequence.Pattern.class)
    private String password;

    @ApiModelProperty(position = 3, required = true, value = "비밀번호 재입력", example = "password",
            allowableValues = "Restriction: [NotBlank]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String passwordReEntered;

    @ApiModelProperty(position = 3, required = true, value = "실명", example = "실명",
            allowableValues = "Restriction: [NotBlank > Size > Pattern]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 2, max = 5, message = "실명은 2~5자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "실명은 한글만 가능합니다.", groups = ValidationSequence.Pattern.class)
    private String legalName;

    @ApiModelProperty(position = 4, required = true, value = "닉네임", example = "닉네임",
            allowableValues = "Restriction: [NotNull > Size > Pattern]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글만 가능합니다.", groups = ValidationSequence.Pattern.class)
    private String nickname;

    @ApiModelProperty(position = 5, required = true, value = "성별: M, F", example = "M",
            allowableValues = "Input: [M | F], Restriction: [NotNull > Pattern]")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @Pattern(regexp = "^[MF]+$]", message = "성별은 M, F 중 하나입니다.", groups = ValidationSequence.Pattern.class)
    private Character gender;

    @ApiModelProperty(position = 6, required = true, notes = "string", value = "생년월일", example = "2000-01-01",
            allowableValues = "Format: [yyyy-MM-dd], Restriction: [NotNull > Pattern]")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "올바른 날짜 형식이 아닙니다.",
            groups = ValidationSequence.Pattern.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @ApiModelProperty(position = 7, required = true, value = "이메일", example = "email@domain.com",
            allowableValues = "Format: [XXXX@XXXXX.XXX], Restriction: [NotBlank > Email]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
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
