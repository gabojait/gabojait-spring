package com.gabojait.gabojaitspring.favorite.service;

import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.favorite.repository.FavoriteUserRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteUserService {

    private final FavoriteUserRepository favoriteUserRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final GeneralProvider generalProvider;

    /**
     * 찜한 회원 업데이트 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void update(long userId, Long otherUserId, boolean isAdd) {
        User leader = findOneUser(userId);
        TeamMember teamMember = findOneCurrentTeamMember(leader);
        validateIsLeader(teamMember);

        Team team = teamMember.getTeam();
        User otherUser = findOneUser(otherUserId);

        if (isAdd)
            create(team, otherUser);
        else
            delete(team, otherUser);
    }

    /**
     * 찜한 회원 페이징 다건 조회 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Page<FavoriteUser> findManyFavoriteUsers(long leaderId, Integer pageFrom, Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);
        User leader = findOneUser(leaderId);

        TeamMember teamMember = findOneCurrentTeamMember(leader);
        validateIsLeader(teamMember);

        try {
            return favoriteUserRepository.findAllByTeamAndIsDeletedIsFalse(teamMember.getTeam(), pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
    /**
     * 찜한 팀 생성
     */
    private void create(Team team, User user) {
        Optional<FavoriteUser> foundFavoriteUser = findOneFavoriteUser(team, user);

        if (foundFavoriteUser.isEmpty()) {
            FavoriteUser favoriteUser = FavoriteUser.builder()
                    .user(user)
                    .team(team)
                    .build();

            saveFavoriteUser(favoriteUser);
        }
    }

    /**
     * 찜한 회원 삭제
     */
    private void delete(Team team, User user) {
        Optional<FavoriteUser> favoriteUser = findOneFavoriteUser(team, user);

        favoriteUser.ifPresent(FavoriteUser::delete);
    }

    /**
     * 찜한 회원 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveFavoriteUser(FavoriteUser favoriteUser) {
        try {
            favoriteUserRepository.save(favoriteUser);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원과 팀으로 찜한 회원 단건 조회
     */
    private Optional<FavoriteUser> findOneFavoriteUser(Team team, User user) {
        return favoriteUserRepository.findByTeamAndUserAndIsDeletedIsFalse(team, user);
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneUser(Long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 현재 팀 멤버 단건 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     */
    private TeamMember findOneCurrentTeamMember(User user) {
        return teamMemberRepository.findByUserAndIsDeletedIsFalse(user)
                .orElseThrow(() -> {
                    throw new CustomException(CURRENT_TEAM_NOT_FOUND);
                });
    }

    /**
     * 리더 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateIsLeader(TeamMember teamMember) {
        if (!teamMember.getIsLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
    }
}
