package com.gabojait.gabojaitspring.user.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.user.domain.Admin;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@ApiModel(value = "관리자 기본 응답")
public class AdminDefaultResDto extends AdminAbstractResDto {

    @ApiModelProperty(position = 5, required = true, value = "실명")
    private String legalName;

    @ApiModelProperty(position = 6, required = true, value = "생년월일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    public AdminDefaultResDto(Admin admin) {
        super(admin);

        this.legalName = admin.getLegalName();
        this.birthdate = admin.getBirthdate();
    }
}
