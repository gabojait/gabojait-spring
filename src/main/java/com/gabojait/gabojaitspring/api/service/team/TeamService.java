package com.gabojait.gabojaitspring.api.service.team;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamCreateRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.team.response.*;
import com.gabojait.gabojaitspring.api.service.notification.NotificationService;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.repository.favorite.FavoriteRepository;
import com.gabojait.gabojaitspring.repository.offer.OfferRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final FavoriteRepository favoriteRepository;
    private final NotificationService notificationService;

    /**
     * 팀 생성 |
     * 404(USER_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_LEADER_POSITION_UNAVAILABLE)
     * @param userId 회원 식별자
     * @param request 팀 생성 요청
     * @return 팀 생성 응답
     */
    @Transactional
    public TeamCreateResponse createTeam(long userId, TeamCreateRequest request) {
        User user = findUser(userId);

        validateHasNoCurrentTeam(user.getId());
        validateLeaderPosition(request, user);

        Team team = request.toTeamEntity();
        teamRepository.save(team);

        TeamMember teamMember = request.toTeamMemberEntity(user, team);
        teamMemberRepository.save(teamMember);

        return new TeamCreateResponse(team, teamMember);
    }

    /**
     * 팀 수정 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 409(DESIGNER_CNT_UPDATE_UNAVAILABLE / BACKEND_CNT_UPDATE_UNAVAILABLE / FRONTEND_CNT_UPDATE_UNAVAILABLE /
     * MANAGER_CNT_UPDATE_UNAVAILABLE)
     * @param userId 회원 식별자
     * @param request 팀 수정 요청
     * @return 팀 수정 응답
     */
    @Transactional
    public TeamUpdateResponse updateTeam(long userId, TeamUpdateRequest request) {
        TeamMember teamMember = findCurrentTeamMemberFetchTeam(userId);
        validateLeader(teamMember);
        teamMember.updatePosition(Position.valueOf(request.getLeaderPosition()));

        Team team = teamMember.getTeam();
        team.update(request.getProjectName(), request.getProjectDescription(), request.getExpectation(),
                request.getDesignerMaxCnt(), request.getBackendMaxCnt(), request.getFrontendMaxCnt(),
                request.getManagerMaxCnt());

        notificationService.sendTeamProfileUpdated(team);

        List<TeamMember> teamMembers = teamMemberRepository.findAllCurrentFetchUser(team.getId());

        return new TeamUpdateResponse(team, teamMembers);
    }

    /**
     * 현재 팀 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @return 팀 현재 본인 조회 응답
     */
    public TeamMyCurrentResponse findCurrentTeam(long userId) {
        TeamMember teamMember = findCurrentTeamMemberFetchTeam(userId);

        List<TeamMember> teamMembers = teamMemberRepository.findAllCurrentFetchUser(teamMember.getTeam().getId());

        return new TeamMyCurrentResponse(teamMember.getTeam(), teamMembers);
    }

    /**
     * 다른 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @return 팀 단건 조회 응답
     */
    @Transactional
    public TeamFindResponse findOtherTeam(long userId, long teamId) {
        Team team = findTeam(teamId);
        List<TeamMember> teamMembers = teamMemberRepository.findAllCurrentFetchUser(team.getId());
        boolean isFavorite = favoriteRepository.existsTeam(userId, team.getId());

        List<Offer> offers = new ArrayList<>();
        boolean isTeamMember = teamMemberRepository.exists(userId, team.getId());
        if (!isTeamMember) {
            team.visit();

            offers = offerRepository.findAllByTeamId(userId, team.getId());
        }

        return new TeamFindResponse(team, teamMembers, offers, isFavorite);
    }

    /**
     * 팀 페이징 조회 |
     * @param position 포지션
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 팀 기본 응답들
     */
    public PageData<List<TeamPageResponse>> findPageTeam(Position position, long pageFrom, int pageSize) {
        PageData<List<Team>> teams = teamRepository.findPage(position, pageFrom, pageSize);

        List<TeamPageResponse> responses = teams.getData()
                .stream()
                .map(TeamPageResponse::new)
                .collect(Collectors.toList());

        return new PageData<>(responses, teams.getTotal());
    }

    /**
     * 팀원 모집 여부 업데이트 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @param isRecruiting 팀원 모집 여부
     */
    @Transactional
    public void updateIsRecruiting(long userId, boolean isRecruiting) {
        TeamMember teamMember = findCurrentTeamMemberFetchTeam(userId);
        validateLeader(teamMember);

        teamMember.getTeam().updateIsRecruiting(isRecruiting);
    }

    /**
     * 프로젝트 종료 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @param projectUrl 프로젝트 URL
     * @param completedAt 완료일
     */
    @Transactional
    public void endProject(long userId, String projectUrl, LocalDateTime completedAt) {
        TeamMember teamMember = findCurrentTeamMemberFetchTeam(userId);
        validateLeader(teamMember);

        List<TeamMember> teamMembers = teamMemberRepository.findAllCurrentFetchUser(teamMember.getTeam().getId());

        if (projectUrl.isBlank()) {
            teamMembers.forEach(TeamMember::incomplete);

            notificationService.sendTeamIncomplete(teamMember.getTeam());
        } else {
            teamMembers.forEach(tm -> tm.complete(projectUrl, completedAt));

            notificationService.sendTeamComplete(teamMember.getTeam());
        }
    }

    /**
     * 팀원 추방 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 409(TEAM_LEADER_UNAVAILABLE)
     * @param teamLeaderUserId 팀장 회원 아이디
     * @param teamMemberUserId 팀원 회원 식별자
     */
    @Transactional
    public void fire(long teamLeaderUserId, long teamMemberUserId) {
        TeamMember teamLeader = findCurrentTeamMemberFetchTeam(teamLeaderUserId);
        validateLeader(teamLeader);

        TeamMember teamMember = findCurrentTeamMember(teamMemberUserId, teamLeader.getTeam().getId());
        teamMember.fire();

        notificationService.sendTeamMemberFired(teamMember.getUser(), teamLeader.getTeam());
    }

    /**
     * 팀 나가기 |
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     * 409(TEAM_LEADER_UNAVAILABLE)
     * @param userId 회원 식별자
     */
    @Transactional
    public void leave(long userId) {
        User user = findUser(userId);

        TeamMember teamMember = findCurrentTeamMemberFetchTeam(user.getId());

        teamMember.quit();

        notificationService.sendTeamMemberQuit(user, teamMember.getTeam());
    }

    /**
     * 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @return 회원
     */
    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     * @param teamId 팀 식별자
     * @return 팀
     */
    private Team findTeam(long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
                });
    }

    /**
     * 현재 팀 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @return 팀원
     */
    private TeamMember findCurrentTeamMemberFetchTeam(long userId) {
        return teamMemberRepository.findCurrentFetchTeam(userId)
                .orElseThrow(() -> {
                    throw new CustomException(CURRENT_TEAM_NOT_FOUND);
                });
    }

    /**
     * 현재 팀 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @return 팀원
     */
    private TeamMember findCurrentTeamMember(long userId, long teamId) {
        return teamMemberRepository.find(userId, teamId, TeamMemberStatus.PROGRESS)
                .orElseThrow(() -> {
                    throw new CustomException(CURRENT_TEAM_NOT_FOUND);
                });
    }

    /**
     * 현재 팀 미존재 검증 |
     * 409(EXISTING_CURRENT_TEAM)
     * @param userId 회원 식별자
     */
    private void validateHasNoCurrentTeam(long userId) {
        boolean hasCurrentTeam = teamMemberRepository.existsCurrent(userId);

        if (hasCurrentTeam)
            throw new CustomException(EXISTING_CURRENT_TEAM);
    }

    /**
     * 현재 팀장 여부 검증 |
     * 403(REQUEST_FORBIDDEN)
     * @param teamMember 팀원
     */
    private void validateLeader(TeamMember teamMember) {
        if (!teamMember.getIsLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
    }

    /**
     * 현재 팀장 포지션으로 팀원수 검증 |
     * 409(TEAM_LEADER_POSITION_UNAVAILABLE)
     * @param request 생성 요청
     * @param user 회원
     */
    private void validateLeaderPosition(TeamCreateRequest request, User user) {
        switch (Position.valueOf(request.getLeaderPosition())) {
            case DESIGNER:
                if (request.getDesignerMaxCnt() <= 0)
                    throw new CustomException(TEAM_LEADER_POSITION_UNAVAILABLE);
                break;
            case BACKEND:
                if (request.getBackendMaxCnt() <= 0)
                    throw new CustomException(TEAM_LEADER_POSITION_UNAVAILABLE);
                break;
            case FRONTEND:
                if (request.getFrontendMaxCnt() <= 0)
                    throw new CustomException(TEAM_LEADER_POSITION_UNAVAILABLE);
                break;
            case MANAGER:
                if (request.getManagerMaxCnt() <= 0)
                    throw new CustomException(TEAM_LEADER_POSITION_UNAVAILABLE);
                break;
        }
    }
}
