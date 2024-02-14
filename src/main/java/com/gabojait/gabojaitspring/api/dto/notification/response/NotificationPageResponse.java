package com.gabojait.gabojaitspring.api.dto.notification.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.notification.Notification;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "알림 페이지 응답")
public class NotificationPageResponse {

    @ApiModelProperty(position = 1, required = true, value = "알림 식별자")
    private Long notificationId;

    @ApiModelProperty(position = 2, required = true, value = "알림 제목")
    private String title;

    @ApiModelProperty(position = 3, required = true, value = "알림 내용")
    private String body;

    @ApiModelProperty(position = 4, required = true, value = "읽음 여부")
    private Boolean isRead;

    @ApiModelProperty(position = 5, required = true, value = "딥링크")
    private DeepLinkResponse deepLink;

    @ApiModelProperty(position = 6, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 7, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public NotificationPageResponse(Notification notification) {
        this.notificationId = notification.getId();
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.isRead = notification.getIsRead();
        this.deepLink = new DeepLinkResponse(notification.getDeepLinkType());
        this.createdAt = notification.getCreatedAt();
        this.updatedAt = notification.getUpdatedAt();
    }
}
