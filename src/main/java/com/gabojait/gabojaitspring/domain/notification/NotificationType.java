package com.gabojait.gabojaitspring.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    NONE("없음"),
    TEAM_PROFILE_UPDATED("팀 프로필 수정"),
    USER_OFFER("회원 제안"),
    TEAM_OFFER("팀 제안"),
    NEW_TEAM_MEMBER("새 팀원"),
    FIRED_TEAM_MEMBER("팀원 추방"),
    QUIT_TEAM_MEMBER("팀원 포기"),
    TEAM_INCOMPLETE("프로젝트 미완료"),
    TEAM_COMPLETE("프로젝트 완료");

    private final String text;
}
