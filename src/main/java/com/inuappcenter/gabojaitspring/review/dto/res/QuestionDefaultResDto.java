package com.inuappcenter.gabojaitspring.review.dto.res;

import com.inuappcenter.gabojaitspring.review.domain.Question;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "Question 기본 응답")
public class QuestionDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "질문 식별자")
    private String questionId;

    @ApiModelProperty(position = 2, required = true, value = "질문 내용")
    private String context;

    @ApiModelProperty(position = 3, required = true, value = "생성일")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 4, required = true, value = "수정일")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(position = 5, required = true, value = "스키마 버전")
    private String schemaVersion;

    public QuestionDefaultResDto(Question question) {
        this.questionId = question.getId().toString();
        this.context = question.getContext();
        this.createdDate = question.getCreatedDate();
        this.modifiedDate = question.getModifiedDate();
        this.schemaVersion = question.getSchemaVersion();
    }

}
