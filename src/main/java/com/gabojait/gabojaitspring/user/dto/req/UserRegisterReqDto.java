package com.gabojait.gabojaitspring.user.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@GroupSequence({UserRegisterReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "회원 가입 요청")
public class UserRegisterReqDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "username")
    @NotBlank(message = "아이디는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[a-z0-9])[a-z0-9]+$", message = "아이디는 소문자 영어와 숫자의 조합으로 입력해 주세요.",
            groups = ValidationSequence.Format.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)(?=.*[#$@!%&*?])[A-z\\d#$@!%&*?]+$",
            message = "비밀번호는 영어, 숫자, 특수문자(#$@!%&*?)의 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String password;

    @ApiModelProperty(position = 3, required = true, value = "비밀번호 재입력", example = "password1!")
    @NotBlank(message = "비밀번호 재입력은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private String passwordReEntered;

    @ApiModelProperty(position = 3, required = true, value = "실명", example = "김가보자잇")
    @Size(max = 5, message = "실명은 0~5자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "실명은 한글 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String legalName;

    @ApiModelProperty(position = 4, required = true, value = "닉네임", example = "김가보자잇")
    @NotBlank(message = "닉네임은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String nickname;

    @ApiModelProperty(position = 5, required = true, value = "성별", example = "male",
            allowableValues = "male, female, none")
    @NotBlank(message = "성별은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(male|female|none)", message = "성별은 'male', 'female', 또는 'none' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String gender;

    @ApiModelProperty(position = 6, required = true, notes = "string", value = "생년월일", example = "2000-01-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @ApiModelProperty(position = 7, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Email(message = "올바른 이메일 형식을 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String email;

    @ApiModelProperty(position = 8, value = "FCM 토큰")
    private String fcmToken;

    public User toEntity(String password, Contact contact) {
        return User.userBuilder()
                .username(this.username)
                .password(password)
                .legalName(this.legalName)
                .nickname(this.nickname)
                .gender(Gender.fromString(this.gender))
                .birthdate(this.birthdate)
                .contact(contact)
                .fcmToken(fcmToken)
                .build();
    }
}
