package com.gabojait.gabojaitspring.api.service.favorite;

import com.gabojait.gabojaitspring.api.dto.favorite.request.FavoriteUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteTeamPageResponse;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteUserPageResponse;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.repository.favorite.FavoriteRepository;
import com.gabojait.gabojaitspring.repository.profile.SkillRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.TEAM_NOT_FOUND;
import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final SkillRepository skillRepository;

    /**
     * 찜한 회원 업데이트 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param favoriteUserId 찜할 회원 식별자
     * @param request 찜 기본 요청
     */
    @Transactional
    public void updateFavoriteUser(long userId, long favoriteUserId, FavoriteUpdateRequest request) {
        User user = findUser(userId);
        User favoriteUser = findUser(favoriteUserId);

        Optional<Favorite> foundFavorite = favoriteRepository.findUser(user.getId(), favoriteUser.getId());

        if (request.getIsAddFavorite()) {
            if (foundFavorite.isPresent()) return;

            Favorite favorite = request.toFavoriteUserEntity(user, favoriteUser);
            favoriteRepository.save(favorite);
        } else {
            if (foundFavorite.isEmpty()) return;

            favoriteRepository.delete(foundFavorite.get());
        }
    }

    /**
     * 찜한 팀 업데이트 |
     * 404(USER_NOT_FOUND / TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @param request 찜 기본 요청
     */
    @Transactional
    public void updateFavoriteTeam(long userId, long teamId, FavoriteUpdateRequest request) {
        User user = findUser(userId);
        Team favoriteTeam = findTeam(teamId);

        Optional<Favorite> foundFavorite = favoriteRepository.findTeam(user.getId(), favoriteTeam.getId());

        if (request.getIsAddFavorite()) {
            if (foundFavorite.isPresent()) return;

            Favorite favorite = request.toFavoriteTeamEntity(user, favoriteTeam);
            favoriteRepository.save(favorite);
        } else {
            if (foundFavorite.isEmpty()) return;

            favoriteRepository.delete(foundFavorite.get());
        }
    }

    /**
     * 찜한 회원 페이징 조회 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 아이디
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 찜한 회원 페이지 응답들
     */
    public PageData<List<FavoriteUserPageResponse>> findPageFavoriteUser(long userId, long pageFrom, int pageSize) {
        User user = findUser(userId);

        PageData<List<Favorite>> favorites = favoriteRepository.findPageUser(user.getId(), pageFrom, pageSize);
        List<Skill> skills = skillRepository.findAllInFetchUser(favorites.getData().stream()
                .map(f -> f.getFavoriteUser().getId())
                .collect(Collectors.toList()));
        Map<Long, List<Skill>> sMap = skills.stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));

        List<FavoriteUserPageResponse> responses = favorites.getData().stream()
                .map(f -> new FavoriteUserPageResponse(f,
                        sMap.getOrDefault(f.getFavoriteUser().getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return new PageData<>(responses, favorites.getTotal());
    }

    /**
     * 찜한 팀 페이징 조회 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 찜한 팀 페이지 응답들
     */
    public PageData<List<FavoriteTeamPageResponse>> findPageFavoriteTeam(long userId, long pageFrom, int pageSize) {
        User user = findUser(userId);

        PageData<List<Favorite>> favorites = favoriteRepository.findPageTeam(user.getId(), pageFrom, pageSize);

        List<FavoriteTeamPageResponse> responses = favorites.getData().stream()
                .map(FavoriteTeamPageResponse::new)
                .collect(Collectors.toList());

        return new PageData<>(responses, favorites.getTotal());
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
}
