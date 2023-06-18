package com.gabojait.gabojaitspring.common.util;

import com.gabojait.gabojaitspring.fcm.domain.Fcm;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.user.domain.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FcmProvider {

    private final FirebaseApp firebaseApp;

    /**
     * 팀원 합류 알림 전송 |
     * 합류한 팀원 & 팀 전체
     */
    public void sendTeamMemberJoin(Offer offer) {
        User user = offer.getUser();
        Team team = offer.getTeam();

        if (!user.getFcms().isEmpty() && user.getIsNotified()) {
            Set<String> fcmTokens = new HashSet<>();
            for (Fcm fcm : user.getFcms())
                fcmTokens.add(fcm.getFcmToken());

            MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                    team.getProjectName() + "팀 합류",
                    Position.toKorean(offer.getPosition()) + "로서 역량을 펼쳐보세요!");

            sendMulticast(multicastMessage);
        }

        Set<String> fcmTokens = getAllFcmTokenExceptOne(team, user);

        if (fcmTokens.isEmpty())
            return;
        MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                "새로운 " + Position.toKorean(offer.getPosition()) + " 팀원 합류",
                user.getUsername() + "님이 " + Position.toKorean(offer.getPosition()) + "로 팀에 합류하였어요.");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀원 추방 알림 전송 |
     * 추방된 팀원 & 팀 전체
     */
    public void sendTeamMemberFired(User user, Team team) {
        if (!user.getFcms().isEmpty() && user.getIsNotified()) {
            Set<String> fcmTokens = new HashSet<>();
            for (Fcm fcm : user.getFcms())
                fcmTokens.add(fcm.getFcmToken());

            MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                    team.getProjectName() + "팀에서 추방",
                    team.getProjectName() + "팀에서 추방 되었어요. 아쉽지만 새로운 팀을 찾아보세요.");

            sendMulticast(multicastMessage);
        }

        Set<String> fcmTokens = getAllFcmTokenExceptOne(team, user);

        if (fcmTokens.isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                user.getUsername() + "님 팀에서 추방",
                user.getUsername() + "님이 팀장에 의해 추방되었어요.");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀원 탈퇴 알림 전송 |
     * 팀 전체
     */
    public void sendTeamMemberQuit(User user, Team team) {
        Set<String> fcmTokens = getAllFcmToken(team);

        if (fcmTokens.isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                user.getUsername() + "님 팀 탈퇴",
                user.getUsername() + "님이 팀에서 탈퇴하였습니다.");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀 해산 알림 전송 |
     * 팀 전체
     */
    public void sendTeamIncomplete(Team team) {
        Set<String> fcmTokens = getAllFcmToken(team);

        if (fcmTokens.isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                team.getProjectName() + "팀 해산",
                "아쉽지만 팀장에 의해 " + team.getProjectName() + "팀이 해산 되었어요.");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀 완료 알림 전송 |
     * 팀 전체
     */
    public void sendTeamComplete(Team team) {
        Set<String> fcmTokens = getAllFcmToken(team);

        if (fcmTokens.isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                team.getProjectName() + " 프로젝트로 완료",
                "수고하셧어요! 프로젝트를 완료했어요. 팀원 리뷰를 작성해보세요!");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀 프로필 수정 알림 전송 |
     * 팀 전체
     */
    public void sendTeamProfileUpdated(Team team) {
        Set<String> fcmTokens = getAllFcmToken(team);

        if (fcmTokens.isEmpty())
            return;

        MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                team.getProjectName() + "팀 프로필 수정",
                team.getProjectName() + "팀 프로필이 팀장에 의해 수정되었습니다.");

        sendMulticast(multicastMessage);
    }

    /**
     * 팀이 회원에게 제안 알림 전송 |
     * 제안 받은 회원
     */
    public void sendOfferByTeam(Offer offer) {
        User user = offer.getUser();
        Team team = offer.getTeam();

        if (!user.getFcms().isEmpty() && user.getIsNotified()) {
            Set<String> fcmTokens = new HashSet<>();
            for (Fcm fcm : user.getFcms())
                fcmTokens.add(fcm.getFcmToken());

            MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                    Position.toKorean(offer.getPosition()) + " 스카웃 제의",
                    team.getProjectName() + "팀에서 " + Position.toKorean(offer.getPosition()) + " 스카웃 제의가 왔어요!");

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 회원이 팀에 제안 알림 전송 |
     * 팀 리더
     */
    public void sendOfferByUser(Offer offer) {
        Team team = offer.getTeam();
        User leader = null;
        for(TeamMember teamMember : team.getTeamMembers())
            if (teamMember.getTeamMemberStatus().equals(TeamMemberStatus.LEADER.getType())) {
                leader = teamMember.getUser();
                break;
            }

        if (leader != null) {
            if (!leader.getIsNotified())
                return;

            Set<String> fcmTokens = new HashSet<>();
            for(Fcm fcm : leader.getFcms())
                fcmTokens.add(fcm.getFcmToken());

            MulticastMessage multicastMessage = createMulticastMessage(fcmTokens,
                    Position.toKorean(offer.getPosition()) + " 지원",
                    Position.toKorean(offer.getPosition()) + offer.getUser().getUsername() + "님이 지원을 했습니다!");

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 알림 단건 전송
     */
    public void sendOne(User user, String title, String message) {
        Set<String> fcmTokens = new HashSet<>();
        for(Fcm fcm : user.getFcms())
            fcmTokens.add(fcm.getFcmToken());

        if (!fcmTokens.isEmpty() && user.getIsNotified()) {
            MulticastMessage multicastMessage = createMulticastMessage(fcmTokens, title, message);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 멀티캐스트 메세지 생성
     */
    private MulticastMessage createMulticastMessage(Set<String> fcmTokens, String title, String body) {
        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("time", LocalDateTime.now().toString())
                .build();
    }

    /**
     * 멀티캐스트 메세지 보내기
     */
    @Async
    protected void sendMulticast(MulticastMessage multicastMessage) {
        FirebaseMessaging.getInstance(firebaseApp).sendMulticastAsync(multicastMessage);
    }

    /**
     * 팀원 전체 FCM 토큰 전체 반환
     */
    private Set<String> getAllFcmToken(Team team) {
        Set<String> fcmTokens = new HashSet<>();

        for(TeamMember teamMember : team.getTeamMembers()) {
            User user = teamMember.getUser();

            if (user.getIsNotified())
                for(Fcm fcm : user.getFcms())
                    fcmTokens.add(fcm.getFcmToken());
        }

        return fcmTokens;
    }

    /**
     * 한명 제외한 팀원 전체 FCM 토큰 전체 반환
     */
    private Set<String> getAllFcmTokenExceptOne(Team team, User user) {
        Set<String> fcmTokens = new HashSet<>();

        for(TeamMember teamMember : team.getTeamMembers()) {
            User u = teamMember.getUser();
            if (user.getId().equals(u.getId()))
                continue;

            if (u.getIsNotified())
                for(Fcm fcm : u.getFcms())
                    fcmTokens.add(fcm.getFcmToken());
        }

        return fcmTokens;
    }
}
