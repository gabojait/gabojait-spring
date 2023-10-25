package com.gabojait.gabojaitspring.api.service.notification;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.notification.response.NotificationDefaultResponse;
import com.gabojait.gabojaitspring.domain.notification.Notification;
import com.gabojait.gabojaitspring.domain.notification.NotificationType;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.repository.notification.FcmRepository;
import com.gabojait.gabojaitspring.repository.notification.NotificationRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final FirebaseApp firebaseApp;
    private final FcmRepository fcmRepository;
    private final NotificationRepository notificationRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 알림 페이징 조회
     * @param username 회원 아이디
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 알림 기본 응답들
     */
    public PageData<List<NotificationDefaultResponse>> findPageNotifications(String username,
                                                                             long pageFrom,
                                                                             int pageSize) {
        Page<Notification> notifications = notificationRepository.findPage(username, pageFrom, pageSize);

        List<NotificationDefaultResponse> responses = notifications.stream()
                .map(NotificationDefaultResponse::new)
                .collect(Collectors.toList());

        return new PageData<>(responses, notifications.getTotalElements());
    }

    /**
     * 알림 읽기
     * @param username       회원 아이디
     * @param notificationId 알림 식별자
     */
    @Transactional
    public void readNotification(String username, long notificationId) {
        notificationRepository.findUnread(username, notificationId)
                .ifPresent(Notification::read);
    }

    /**
     * 새 팀원 합류 알림 전송 |
     * 합류 팀원 & 팀 전체
     * @param offer 제안
     */
    public void sendTeamMemberJoin(Offer offer) {
        User user = offer.getUser();
        Team team = offer.getTeam();

        List<String> userFcms = fcmRepository.findAllUser(user.getId());
        List<String> teamFcms = fcmRepository.findAllTeamExceptUser(team.getId(), user.getId());

        String userTitle = team.getProjectName() + "팀 합류";
        String userBody = offer.getPosition().getText() + "로서 역량을 펼쳐보세요!";
        String teamTitle = "새로운 " + offer.getPosition().getText() + " 합류";
        String teamBody = user.getNickname() + "님이 " + offer.getPosition().getText() + "로 팀에 합류하였어요.";
        NotificationType notificationType = NotificationType.NEW_TEAM_MEMBER;

        createAndSaveNotification(user, notificationType, userTitle, userBody);
        if (!userFcms.isEmpty()) {
            Map<String, String> data = createFcmData(userTitle, userBody, notificationType);

            MulticastMessage message = createMulticast(userFcms, data);
            sendMulticast(message);
        }

        teamMemberRepository.findAllExceptUserFetchUser(team.getId(), user.getId())
                .forEach(teamMember ->
                        createAndSaveNotification(teamMember.getUser(), notificationType, teamTitle, teamBody)
                );
        if (!teamFcms.isEmpty()) {
            Map<String, String> data = createFcmData(teamTitle, teamBody, notificationType);

            MulticastMessage message = createMulticast(teamFcms, data);
            sendMulticast(message);
        }
    }

    /**
     * 팀원 추방 알림 전송 |
     * 추방된 팀원 & 팀 전체
     * @param user 추방된 회원
     * @param team 팀
     */
    public void sendTeamMemberFired(User user, Team team) {
        List<String> userFcms = fcmRepository.findAllUser(user.getId());
        List<String> teamFcms = fcmRepository.findAllTeamExceptUser(team.getId(), user.getId());

        String userTitle = team.getProjectName() + "팀에서 추방";
        String userBody = team.getProjectName() + "팀에서 추방 되었어요. 아쉽지만 새로운 팀을 찾아보세요.";
        String teamTitle = user.getNickname() + "님 팀에서 추방";
        String teamBody = user.getNickname() + "님이 팀장에 의해 추방되었어요.";
        NotificationType notificationType = NotificationType.FIRED_TEAM_MEMBER;

        createAndSaveNotification(user, notificationType, userTitle, userBody);
        if (!userFcms.isEmpty()) {
            Map<String, String> data = createFcmData(userTitle, userBody, notificationType);

            MulticastMessage message = createMulticast(userFcms, data);
            sendMulticast(message);
        }

        teamMemberRepository.findAllExceptUserFetchUser(team.getId(), user.getId())
                .forEach(teamMember ->
                        createAndSaveNotification(teamMember.getUser(), notificationType, teamTitle, teamBody)
                );
        if (!teamFcms.isEmpty()) {
            Map<String, String> data = createFcmData(teamTitle, teamBody, notificationType);

            MulticastMessage message = createMulticast(teamFcms, data);
            sendMulticast(message);
        }
    }

    /**
     * 팀원 탈퇴 알림 전송 |
     * 팀 전체
     * @param user 회원
     * @param team 팀
     */
    public void sendTeamMemberQuit(User user, Team team) {
        List<String> fcms = fcmRepository.findAllTeamExceptUser(team.getId(), user.getId());

        String title = user.getNickname() + "님 팀 탈퇴";
        String body = user.getNickname() + "님이 팀에서 탈퇴하였습니다.";
        NotificationType notificationType = NotificationType.QUIT_TEAM_MEMBER;
        teamMemberRepository.findAllExceptUserFetchUser(team.getId(), user.getId())
                .forEach(teamMember ->
                        createAndSaveNotification(teamMember.getUser(), notificationType, title, body)
                );
        if (!fcms.isEmpty()) {
            Map<String, String> data = createFcmData(title, body, notificationType);

            MulticastMessage message = createMulticast(fcms, data);
            sendMulticast(message);
        }

    }

    /**
     * 팀 해산 알림 전송 |
     * 팀 전체
     * @param team 팀
     */
    public void sendTeamIncomplete(Team team) {
        List<String> fcms = fcmRepository.findAllTeam(team.getId());
        String title = team.getProjectName() + "팀 해산";
        String boy = "아쉽지만 팀장에 의해 " + team.getProjectName() + "팀이 해산 되었어요.";
        NotificationType notificationType = NotificationType.TEAM_INCOMPLETE;
        teamMemberRepository.findAllFetchUser(team.getId())
                .forEach(teamMember ->
                        createAndSaveNotification(teamMember.getUser(), notificationType, title, boy)
                );
        if (!fcms.isEmpty()) {
            Map<String, String> data = createFcmData(title, boy, notificationType);

            MulticastMessage message = createMulticast(fcms, data);
            sendMulticast(message);
        }
    }

    /**
     * 팀 완료 알림 전송 |
     * 팀 전체
     * @param team 팀
     */
    public void sendTeamComplete(Team team) {
        List<String> fcms = fcmRepository.findAllTeam(team.getId());
        String title = team.getProjectName() + " 프로젝트 완료";
        String body = "수고하셨어요! 프로젝트를 완료했어요. 팀원 리뷰를 작성해보세요!";
        NotificationType notificationType = NotificationType.TEAM_COMPLETE;
        teamMemberRepository.findAllFetchUser(team.getId())
                .forEach(teamMember ->
                        createAndSaveNotification(teamMember.getUser(), notificationType, title, body)
                );
        if (!fcms.isEmpty()) {
            Map<String, String> data = createFcmData(title, body, notificationType);

            MulticastMessage message = createMulticast(fcms, data);
            sendMulticast(message);
        }
    }

    /**
     * 팀 프로필 수정 알림 전송 |
     * 팀 전체
     * @param team 팀
     */
    public void sendTeamProfileUpdated(Team team) {
        List<String> fcms = fcmRepository.findAllTeam(team.getId());
        String title = team.getProjectName() + "팀 프로필 수정";
        String body = team.getProjectName() + "팀 프로필이 팀장에 의해 수정되었어요.";
        NotificationType notificationType = NotificationType.TEAM_PROFILE_UPDATED;

        teamMemberRepository.findAllFetchUser(team.getId())
                .forEach(teamMember ->
                        createAndSaveNotification(teamMember.getUser(), notificationType, title, body)
                );
        if (!fcms.isEmpty()) {
            Map<String, String> data = createFcmData(title, body, notificationType);

            MulticastMessage message = createMulticast(fcms, data);
            sendMulticast(message);
        }
    }

    /**
     * 팀이 회원에게 제안 알림 전송 |
     * 제안 받은 회원
     * @param offer 제안
     */
    public void sendOfferByTeam(Offer offer) {
        List<String> fcms = fcmRepository.findAllUser(offer.getUser().getId());
        String title = offer.getPosition().toString() + " 스카웃 제의";
        String body = offer.getTeam().getProjectName() + "팀에서 " + offer.getPosition().getText() + " 스카웃 제의가 왔어요!";
        NotificationType notificationType = NotificationType.TEAM_OFFER;
        createAndSaveNotification(offer.getUser(), notificationType, title, body);
        if (!fcms.isEmpty()) {
            Map<String, String> data = createFcmData(title, body, notificationType);

            MulticastMessage message = createMulticast(fcms, data);
            sendMulticast(message);
        }
    }

    /**
     * 회원이 팀에게 제안 알림 전송 |
     * 팀장
     * @param offer 제안
     */
    public void sendOfferByUser(Offer offer) {
        Optional<TeamMember> teamLeader = teamMemberRepository.findLeaderFetchUser(offer.getTeam().getId());
        String title = offer.getPosition().getText() + " 지원";
        String body = offer.getPosition().getText() + " " + offer.getUser().getNickname() + "님이 지원을 했습니다.";
        NotificationType notificationType = NotificationType.USER_OFFER;
        if (teamLeader.isPresent()) {
            createAndSaveNotification(teamLeader.get().getUser(), notificationType, title, body);

            List<String> fcms = fcmRepository.findAllUser(teamLeader.get().getUser().getId());
            if (!fcms.isEmpty()) {
                Map<String, String> data = createFcmData(title, body, notificationType);

                MulticastMessage message = createMulticast(fcms, data);
                sendMulticast(message);
            }
        }
    }


    private Map<String, String> createFcmData(String title, String body, NotificationType notificationType) {
        return Map.of(
                "title", title,
                "body", body,
                "type", notificationType.name(),
                "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    /**
     * 멀티캐스트 메세지 생성
     * @param fcmTokens FCM 토큰들
     * @param data      데이터
     * @return 멀티캐스트 메세지
     */
    private MulticastMessage createMulticast(List<String> fcmTokens, Map<String, String> data) {
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
     * 멀티캐스트 메세지 전송
     * @param message 메세지
     */
    @Async
    public void sendMulticast(MulticastMessage message) {
        FirebaseMessaging.getInstance(firebaseApp)
                .sendMulticastAsync(message);
    }

    /**
     * 알림 생성 및 저장
     * @param user 회원
     * @param notificationType 알림 종류
     * @param title 제목
     * @param body 내용
     */
    @Transactional
    public void createAndSaveNotification(User user, NotificationType notificationType, String title, String body) {
        Notification notification = Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .title(title)
                .body(body)
                .build();

        notificationRepository.save(notification);
    }
}
