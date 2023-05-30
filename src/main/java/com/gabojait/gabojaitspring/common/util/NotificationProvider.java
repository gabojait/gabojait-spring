package com.gabojait.gabojaitspring.common.util;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationProvider {

    private final FirebaseApp firebaseApp;

    /**
     * 팀 합류 알림 전송 | main |
     * 새 팀원 & 팀 전체 |
     * 500(SERVER_ERROR)
     */
    public void teamJoinedNotification(User user, Team team, Offer offer, boolean isAccepted) {
        if (!isAccepted)
            return;

        if (!user.getFcmTokens().isEmpty() || !user.getIsNotified()) {
            MulticastMessage multicastMessage = createMulticastMessages(user.getFcmTokens(),
                    team.getProjectName() + "팀 합류",
                    Position.toKorean(offer.getPosition()) + "로서 역량을 펼쳐보세요!");

            sendMulticast(multicastMessage);
        }

        if (team.getAllMemberFcmTokens().isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(team.getAllMemberFcmTokens(),
                "새로운 " + Position.toKorean(offer.getPosition()) + " 팀원 합류",
                user.getUsername() + "님이 " + Position.toKorean(offer.getPosition()) + "로 팀에 합류하였어요.");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀 추방 알림 전송 | main |
     * 추방된 팀원 & 팀 전체 |
     * 500(SERVER_ERROR)
     */
    public void teamFiredNotification(User user, Team team) {
        if (!user.getFcmTokens().isEmpty() || !user.getIsNotified()) {
            MulticastMessage multicastMessage = createMulticastMessages(user.getFcmTokens(),
                    team.getProjectName() + "팀에서 추방",
                    team.getProjectName() + " 팀에서 추방 되었어요. 아쉽지만 새로운 팀을 찾아보세요!");

            sendMulticast(multicastMessage);
        }

        if (team.getAllMemberFcmTokens().isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(team.getAllMemberFcmTokens(),
                user.getUsername() + "님 팀에서 추방",
                user.getUsername() + "님이 팀장에 의해 추방되었어요.");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀 해산 알림 전송 | main |
     * 팀 전체 |
     * 500(SERVER_ERROR)
     */
    public void teamIncompleteQuitNotification(Team team) {
        if (team.getAllMemberFcmTokens().isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(team.getAllMemberFcmTokens(),
                team.getProjectName() + " 팀 해산",
                "아쉽지만 팀장에 의해 " + team.getProjectName() + " 팀이 해산되었어요.");

        sendMulticast(multicastMessage);
    }

    /**
     * 프로젝트 종료 알림 전송 | main |
     * 팀 전체 |
     * 500(SERVER_ERROR)
     */
    public void teamCompleteQuitNotification(Team team) {
        if (team.getAllMemberFcmTokens().isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(team.getAllMemberFcmTokens(),
                team.getProjectName() + " 프로젝트 종료",
                "수고하셨어요! 프로젝트를 완료하였습니다. 팀원 리뷰를 작성해보세요!");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀 프로필 수정 알림 전송 | main |
     * 팀 전체 |
     * 500(SERVER_ERROR)
     */
    public void teamProfileModifiedNotification(Team team) {
        if (team.getAllMemberFcmTokens().isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(team.getAllMemberFcmTokens(),
                team.getProjectName() + " 팀 프로필 수정",
                team.getProjectName()+ " 팀 프로필이 팀장에 의해 수정되었습니다.");

        sendMulticast(multicastMessage);
    }

    /**
     * 회원 스카웃 알림 전송 | main |
     * 스카웃 요청 받은 회원 |
     * 500(SERVER_ERROR)
     */
    public void offerByTeamNotification(User user, Offer offer, Team team) {
        if (user.getFcmTokens().isEmpty() || !user.getIsNotified())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(user.getFcmTokens(),
                Position.toKorean(offer.getPosition()) + " 스카웃 제의",
                team.getProjectName() + " 팀에서 " + Position.toKorean(offer.getPosition()) + " 스카웃 제의가 왔어요!");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀 지원 알림 전송 | main |
     * 팀장 |
     * 500(SERVER_ERROR)
     */
    public void offerByUserNotification(User leader, Offer offer, User user) {
        if (leader.getFcmTokens().isEmpty() || !leader.getIsNotified())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(leader.getFcmTokens(),
                Position.toKorean(offer.getPosition()) + " 지원",
                Position.toKorean(offer.getPosition()) + user.getUsername() + "님이 지원을 했습니다!");

        sendMulticast(multicastMessage);
    }

    /**
     * 한 회원에게 알림 전송 | main |
     * 500(SERVER_ERROR)
     */
    public void singleNotification(User user, String title, String message) {
        if (user.getFcmTokens().isEmpty() || !user.getIsNotified())
            return;

        MulticastMessage multicastMessage = createMulticastMessages(user.getFcmTokens(), title, message);

        sendMulticast(multicastMessage);
    }

    /**
     * 멀티캐스트 메세지 생성
     */
    private MulticastMessage createMulticastMessages(Set<String> tokens, String title, String body) {
        return MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("time", LocalDateTime.now().toString())
                .build();
    }

    /**
     * 멀티캐스트 메세지 보내기 |
     * 500(SERVER_ERROR)
     */
    @Async
    protected void sendMulticast(MulticastMessage multicastMessage) {
        FirebaseMessaging.getInstance(firebaseApp).sendMulticastAsync(multicastMessage);
    }
}
