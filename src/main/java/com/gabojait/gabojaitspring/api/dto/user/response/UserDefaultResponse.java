package com.gabojait.gabojaitspring.api.dto.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "회원 기본 응답")
public class UserDefaultResponse {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private Long userId;

    @ApiModelProperty(position = 2, required = true, value = "아이디")
    private String username;

    @ApiModelProperty(position = 3, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 4, required = true, value = "성별", allowableValues = "M, F, N")
    private Gender gender;

    @ApiModelProperty(position = 5, required = true, value = "생년월일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @ApiModelProperty(position = 6, required = true, value = "알림 여부")
    private Boolean isNotified;

    @ApiModelProperty(position = 7, required = true, value = "연락처")
    private ContactDefaultResponse contact;

    @ApiModelProperty(position = 8, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 9, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public UserDefaultResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.birthdate = user.getBirthdate();
        this.isNotified = user.getIsNotified();
        this.contact = new ContactDefaultResponse(user.getContact());
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    @Builder
    private UserDefaultResponse(long id,
                                String username,
                                String nickname,
                                Gender gender,
                                LocalDate birthdate,
                                boolean isNotified,
                                LocalDateTime now,
                                String email) {
        this.userId = id;
        this.username = username;
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.isNotified = isNotified;
        this.contact = ContactDefaultResponse.builder()
                .id(id)
                .email(email)
                .now(now)
                .build();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
