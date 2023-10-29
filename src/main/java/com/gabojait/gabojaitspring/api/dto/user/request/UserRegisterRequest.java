package com.gabojait.gabojaitspring.api.dto.user.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.domain.notification.Fcm;
import com.gabojait.gabojaitspring.domain.user.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({UserRegisterRequest.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "회원 가입 요청")
public class UserRegisterRequest {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "tester")
    @NotBlank(message = "아이디는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[a-z0-9])[a-z0-9]+$", message = "아이디는 소문자 영어와 숫자의 조합으로 입력해 주세요.",
            groups = ValidationSequence.Format.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)(?=.*[#$@!%&*?])[A-z\\d#$@!%&*?]+$",
            message = "비밀번호는 영어, 숫자, 특수문자(#$@!%&*?)의 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String password;

    @ApiModelProperty(position = 3, required = true, value = "비밀번호 재입력", example = "password1!")
    @NotBlank(message = "비밀번호 재입력은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String passwordReEntered;

    @ApiModelProperty(position = 4, required = true, value = "닉네임", example = "김테스트")
    @NotBlank(message = "닉네임은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String nickname;

    @ApiModelProperty(position = 5, required = true, value = "성별", example = "M",
            allowableValues = "M, F, N")
    @NotBlank(message = "성별은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(M|F|N)", message = "성별은 'M', 'F', 또는 'N' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String gender;

    @ApiModelProperty(position = 6, required = true, notes = "string", value = "생년월일", example = "2000-01-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @ApiModelProperty(position = 7, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Email(message = "올바른 이메일 형식을 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String email;

    @ApiModelProperty(position = 8, required = true, value = "인증코드")
    @NotBlank(message = "인증코드는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String verificationCode;

    @ApiModelProperty(position = 9, value = "FCM 토큰")
    private String fcmToken;

    public User toEntity(String password, Contact contact, LocalDateTime lastRequestAt) {
        return User.builder()
                .username(this.username)
                .password(password)
                .nickname(this.nickname)
                .gender(Gender.valueOf(this.gender))
                .birthdate(this.birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }

    public Fcm toFcmEntity(User user) {
        return Fcm.builder()
                .user(user)
                .fcmToken(this.fcmToken)
                .build();
    }

    @Builder
    private UserRegisterRequest(String username,
                               String password,
                               String passwordReEntered,
                               String nickname,
                               String gender,
                               LocalDate birthdate,
                               String email,
                               String verificationCode,
                               String fcmToken) {
        this.username = username;
        this.password = password;
        this.passwordReEntered = passwordReEntered;
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.email = email;
        this.verificationCode = verificationCode;
        this.fcmToken = fcmToken;
    }
}
