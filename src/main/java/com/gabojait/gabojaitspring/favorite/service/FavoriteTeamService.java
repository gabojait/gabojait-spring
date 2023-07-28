package com.gabojait.gabojaitspring.favorite.service;

import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.favorite.repository.FavoriteTeamRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
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
public class FavoriteTeamService {

    private final FavoriteTeamRepository favoriteTeamRepository;
    private final TeamRepository teamRepository;
    private final GeneralProvider generalProvider;
    private final UserRepository userRepository;

    /**
     * 찜한 팀 업데이트 |
     * 404(USER_NOT_FOUND / TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void update(long userId, Long teamId, boolean isAdd) {
        Team team = findOneTeam(teamId);
        User user= findOneUser(userId);

        if (isAdd)
            create(user, team);
        else
            delete(user, team);
    }

    /**
     * 찜한 팀 생성
     */
    private void create(User user, Team team) {
        Optional<FavoriteTeam> foundFavoriteTeam = findOneFavoriteTeam(user, team);

        if (foundFavoriteTeam.isEmpty()) {
            FavoriteTeam favoriteTeam = FavoriteTeam.builder()
                    .user(user)
                    .team(team)
                    .build();

            saveFavoriteTeam(favoriteTeam);
        }
    }

    /**
     * 찜한 팀 삭제
     */
    private void delete(User user, Team team) {
        Optional<FavoriteTeam> favoriteTeam = findOneFavoriteTeam(user, team);

        favoriteTeam.ifPresent(FavoriteTeam::delete);
    }

    /**
     * 찜한 팀 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveFavoriteTeam(FavoriteTeam favoriteTeam) {
        try {
            favoriteTeamRepository.save(favoriteTeam);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원과 팀으로 찜한 팀 단건 조회
     */
    private Optional<FavoriteTeam> findOneFavoriteTeam(User user, Team team) {
        return favoriteTeamRepository.findByUserAndTeamAndIsDeletedIsFalse(user, team);
    }

    /**
     * 찜한 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    public Page<FavoriteTeam> findManyFavoriteTeams(long userId, Integer pageFrom, Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);
        User user = findOneUser(userId);

        try {
            return favoriteTeamRepository.findAllByUserAndIsDeletedIsFalse(user, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자로 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     */
    private Team findOneTeam(Long teamId) {
        return teamRepository.findByIdAndIsDeletedIsFalse(teamId)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
                });
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
}
