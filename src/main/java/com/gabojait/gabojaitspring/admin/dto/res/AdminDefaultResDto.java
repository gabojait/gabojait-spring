package com.gabojait.gabojaitspring.admin.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@ApiModel(value = "관리자 기본 응답")
public class AdminDefaultResDto extends AdminAbstractResDto {

    @ApiModelProperty(position = 6, required = true, value = "실명")
    private String legalName;

    @ApiModelProperty(position = 7, required = true, value = "성별")
    private String gender;

    @ApiModelProperty(position = 8, required = true, value = "생년월일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    public AdminDefaultResDto(User user) {
        super(user);

        this.legalName = user.getLegalName();
        this.gender = Gender.fromChar(user.getGender()).name();
        this.birthdate = user.getBirthdate();
    }
}