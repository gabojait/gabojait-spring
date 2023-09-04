package com.gabojait.gabojaitspring.fcm.service;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.fcm.domain.Fcm;
import com.gabojait.gabojaitspring.fcm.domain.type.AlertType;
import com.gabojait.gabojaitspring.fcm.repository.FcmRepository;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TEAM_LEADER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseApp firebaseApp;
    private final FcmRepository fcmRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 팀원 합류 알림 전송 |
     * 합류한 팀원 & 팀 전체
     */
    public void sendTeamMemberJoin(Offer offer) {
        User user = offer.getUser();
        Team team = offer.getTeam();

        Set<String> userFcmTokens = findAllFcmTokenByUser(user);

        if (!userFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(team.getProjectName() + "팀 합류",
                    Position.toKorean(offer.getPosition()) + "로서 역량을 펼쳐보세요!", AlertType.TEAM_PROFILE);

            MulticastMessage multicastMessage = createMulticastMessage(userFcmTokens, data);

            sendMulticast(multicastMessage);
        }

        Set<String> teamFcmTokens = findAllTeamMemberFcmTokenExceptOneByTeam(team, user);

        if (!teamFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData("새로운 " + Position.toKorean(offer.getPosition()) + " 팀원 합류",
                    user.getUsername() + "님이 " + Position.toKorean(offer.getPosition()) + "로 팀에 합류하였어요.",
                    AlertType.TEAM_PROFILE);

            MulticastMessage multicastMessage = createMulticastMessage(teamFcmTokens, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 팀원 추방 알림 전송 |
     * 추방된 팀원 & 팀 전체
     */
    public void sendTeamMemberFired(User user, Team team) {
        Set<String> userFcmTokens = findAllFcmTokenByUser(user);

        if (!userFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(team.getProjectName() + "팀에서 추방",
                    team.getProjectName() + "팀에서 추방 되었어요. 아쉽지만 새로운 팀을 찾아보세요.", AlertType.NONE);

            MulticastMessage multicastMessage = createMulticastMessage(userFcmTokens, data);

            sendMulticast(multicastMessage);
        }

        Set<String> teamFcmTokens = findAllTeamMemberFcmTokenExceptOneByTeam(team, user);

        if (!teamFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(user.getUsername() + "님 팀에서 추방",
                    user.getUsername() + "님이 팀장에 의해 추방되었어요.", AlertType.TEAM_PROFILE);

            MulticastMessage multicastMessage = createMulticastMessage(teamFcmTokens, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 팀원 탈퇴 알림 전송 |
     * 팀 전체
     */
    public void sendTeamMemberQuit(User user, Team team) {
        Set<String> allFcmTokens = findAllTeamMemberFcmTokenExceptOneByTeam(team, user);

        if (!allFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(user.getUsername() + "님 팀 탈퇴",
                    user.getUsername() + "님이 팀에서 탈퇴하였습니다.",
                    AlertType.TEAM_PROFILE);

            MulticastMessage multicastMessage = createMulticastMessage(allFcmTokens, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 팀 해산 알림 전송 |
     * 팀 전체
     */
    public void sendTeamIncomplete(Team team) {
        Set<String> allFcmTokens = findAllFcmTokenByTeam(team);

        if (!allFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(team.getProjectName() + "팀 해산",
                    "아쉽지만 팀장에 의해 " + team.getProjectName() + "팀이 해산 되었어요.", AlertType.NONE);

            MulticastMessage multicastMessage = createMulticastMessage(allFcmTokens, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 팀 완료 알림 전송 |
     * 팀 전체
     */
    public void sendTeamComplete(Team team) {
        Set<String> allFcmTokens = findAllFcmTokenByTeam(team);

        if (!allFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(team.getProjectName() + " 프로젝트로 완료",
                    "수고하셧어요! 프로젝트를 완료했어요. 팀원 리뷰를 작성해보세요!", AlertType.REVIEW);

            MulticastMessage multicastMessage = createMulticastMessage(allFcmTokens, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 팀 프로필 수정 알림 전송 |
     * 팀 전체
     */
    public void sendTeamProfileUpdated(Team team) {
        Set<String> allFcmTokens = findAllFcmTokenByTeam(team);

        if (!allFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(team.getProjectName() + "팀 프로필 수정",
                    team.getProjectName() + "팀 프로필이 팀장에 의해 수정되었습니다.", AlertType.TEAM_PROFILE);

            MulticastMessage multicastMessage = createMulticastMessage(allFcmTokens, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 팀이 회원에게 제안 알림 전송 |
     * 제안 받은 회원
     */
    public void sendOfferByTeam(Offer offer) {
        Team team = offer.getTeam();
        User user = offer.getUser();

        Set<String> userFcmTokens = findAllFcmTokenByUser(user);

        if (!userFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(Position.toKorean(offer.getPosition()) + " 스카웃 제의",
                    team.getProjectName() + "팀에서 " + Position.toKorean(offer.getPosition()) +
                            " 스카웃 제의가 왔어요!", AlertType.TEAM_OFFER);

            MulticastMessage multicastMessage = createMulticastMessage(userFcmTokens, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 회원이 팀에 제안 알림 전송 |
     * 404(TEAM_LEADER_NOT_FOUND)
     */
    public void sendOfferByUser(Offer offer) {
        TeamMember leader = findLeaderTeamMemberByTeam(offer.getTeam());
        Set<String> userFcmTokens = findAllFcmTokenByUser(leader.getUser());

        if (!userFcmTokens.isEmpty()) {
            Map<String, String> data = setFcmData(
                    Position.toKorean(offer.getPosition()) + " 지원",
                    Position.toKorean(offer.getPosition()) + " " + offer.getUser().getUsername() +
                            "님이 지원을 했습니다!", AlertType.USER_OFFER);

            MulticastMessage multicastMessage = createMulticastMessage(userFcmTokens, data);

            sendMulticast(multicastMessage);
        }

    }

    /**
     * 알림 단건 전송
     */
    public void sendTest(User user, Map<String, String> data) {
        Set<String> userFcms = findAllFcmTokenByUser(user);

        if (!userFcms.isEmpty()) {
            MulticastMessage multicastMessage = createMulticastMessage(userFcms, data);

            sendMulticast(multicastMessage);
        }
    }

    /**
     * 멀티캐스트 메세지 생성
     */
    private MulticastMessage createMulticastMessage(Set<String> fcmTokens, Map<String, String> data) {
        Map<String, String> headers = new HashMap<>();
        headers.put("apns-push-type", "background");
        headers.put("apns-priority", "5");
        headers.put("topic", "com.gabojait");

        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setContentAvailable(true)
                                .build())
                        .putAllHeaders(headers)
                        .build())
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
     * FCM 데이터 생성
     */
    private Map<String, String> setFcmData(String title, String body, AlertType alertType) {
        Map<String, String> data = new HashMap<>();

        data.put("title", title);
        data.put("body", body);
        data.put("type", alertType.name());
        data.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return data;
    }

    /**
     * 회원으로 전체 FCM 토큰 조회
     */
    private Set<String> findAllFcmTokenByUser(User user) {
        Set<String> fcmSet = new HashSet<>();
        if (user.getIsNotified()) {
            List<Fcm> fcms = fcmRepository.findAllByUserAndIsDeletedIsFalse(user);
            fcms.forEach(fcm -> fcmSet.add(fcm.getFcmToken()));
        }

        return fcmSet;
    }

    /**
     * 팀으로 전체 FCM 토큰 조회
     */
    private Set<String> findAllFcmTokenByTeam(Team team) {
        Set<String> fcmSet = new HashSet<>();
        List<TeamMember> teamMembers =  teamMemberRepository.findAllByTeamAndIsQuitIsFalseAndIsDeletedIsFalse(team);

        for (TeamMember teamMember : teamMembers)
            if (teamMember.getUser().getIsNotified())
                fcmSet.addAll(findAllFcmTokenByUser(teamMember.getUser()));

        return fcmSet;
    }

    /**
     * 팀과 회원으로 한명 제외한 전체 FCM 토큰 조회
     */
    private Set<String> findAllTeamMemberFcmTokenExceptOneByTeam(Team team, User user) {
        Set<String> fcmSet = new HashSet<>();
        List<TeamMember> teamMembers =  teamMemberRepository.findAllByTeamAndIsQuitIsFalseAndIsDeletedIsFalse(team);

        for (TeamMember teamMember : teamMembers)
            if (teamMember.getUser().equals(user) && teamMember.getUser().getIsNotified())
                fcmSet.addAll(findAllFcmTokenByUser(teamMember.getUser()));

        return fcmSet;
    }

    /**
     * 팀으로 리더 단건 조회 |
     * 404(TEAM_LEADER_NOT_FOUND)
     */
    private TeamMember findLeaderTeamMemberByTeam(Team team) {
        return teamMemberRepository.findByTeamAndIsLeaderIsTrueAndIsQuitIsFalseAndIsDeletedIsFalse(team)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_LEADER_NOT_FOUND);
                });
    }
}
