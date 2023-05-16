package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.common.util.EmailProvider;
import com.gabojait.gabojaitspring.common.util.FileProvider;
import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.ProfileOrder;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.dto.req.UserLoginReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserSaveReqDto;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    @Value(value = "${s3.bucket.profile-img}")
    private String bucketName;

    private final UserRepository userRepository;
    private final EmailProvider emailProvider;
    private final FileProvider fileProvider;
    private final UtilityProvider utilityProvider;

    /**
     * 아이디 검증 | main |
     * 409(UNAVAILABLE_USERNAME / EXISTING_USERNAME)
     */
    public void validateUsername(String username) {
        if (username.toLowerCase().contains("admin") || username.toLowerCase().contains("gabojait"))
            throw new CustomException(null, UNAVAILABLE_USERNAME);

        isExistingUsername(username);
    }

    /**
     * 닉네임 검증 | main |
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void validateNickname(String nickname) {
        if (nickname.toLowerCase().contains("관리자") || nickname.toLowerCase().contains("가보자잇"))
            throw new CustomException(null, UNAVAILABLE_NICKNAME);

        isExistingNickname(nickname);
    }

    /**
     * 가입 | main |
     * 500(SERVER_ERROR)
     */
    public User register(UserSaveReqDto request, Contact contact) {
        String password = utilityProvider.encodePassword(request.getPassword());

        return save(request.toEntity(password, contact));
    }

    /**
     * 로그인 | main |
     * 401(LOGIN_FAIL)
     */
    public User login(UserLoginReqDto request) {
        User user = findOneUsername(request.getUsername());

        boolean isVerified = utilityProvider.verifyPassword(user, request.getPassword());
        if (!isVerified)
            throw new CustomException(null, LOGIN_FAIL);

        return user;
    }

    /**
     * 타인 단건 조회 | main |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public User findOther(User user, String otherUserId) {
        if (user.getId().toString().equals(otherUserId)) {
            return user;
        }

        return findOneOther(otherUserId);
    }

    /**
     * 아이디 이메일로 전송 | main |
     * 404(USER_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR)
     */
    public void sendUsernameEmail(Contact contact) {
        User user = findOneEmail(contact);

        emailProvider.sendEmail(
                user.getContact().getEmail(),
                "[가보자잇] 아이디 찾기",
                user.getLegalName() + "님 안녕하세요!🙇🏻<br>해당 이메일로 가입된 아이디 정보입니다.",
                user.getUsername()
        );
    }

    /**
     * 임시 비밀번호 이메일로 전송 | main |
     * 400(USERNAME_EMAIL_MATCH_INVALID)
     * 404(USER_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR)
     */
    public void sendPasswordEmail(Contact contact, String username) {
        User user = findOneEmail(contact);

        if (!user.getUsername().equals(username))
            throw new CustomException(null, USERNAME_EMAIL_MATCH_INVALID);

        String tempPassword = utilityProvider.generateRandomCode(8);
        updatePassword(user, tempPassword, tempPassword, true);

        emailProvider.sendEmail(
                user.getContact().getEmail(),
                "[가보자잇] 비밀번호 찾기",
                user.getLegalName() +
                        "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
                tempPassword
        );
    }

    /**
     * 비밀번호 업데이트 | main&sub |
     * 400(PASSWORD_MATCH_INVALID)
     * 500(SERVER_ERROR)
     */
    public void updatePassword(User user, String password, String passwordReEntered, boolean isTemporaryPassword) {
        if (!isTemporaryPassword)
            isMatchingPassword(password, passwordReEntered);

        user.updatePassword(utilityProvider.encodePassword(password), isTemporaryPassword);

        save(user);
    }

    /**
     * 비밀번호 검증 | main |
     * 400(PASSWORD_INVALID)
     */
    public void validatePassword(User user, String password) {
        boolean isVerified = utilityProvider.verifyPassword(user, password);
        if (!isVerified)
            throw new CustomException(null, PASSWORD_INVALID);
    }

    /**
     * 프로필 이미지 업데이트 | main |
     * 400(FILE_FIELD_REQUIRED)
     * 415(IMAGE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public void uploadProfileImage(User user, MultipartFile multipartFile) {
        String url = fileProvider.upload(bucketName,
                user.getUsername() + "-" + user.getId().toString(),
                UUID.randomUUID().toString(),
                multipartFile,
                true);

        user.updateImageUrl(url);

        save(user);
    }

    /**
     * 프로필 이미지 삭제 | main |
     * 500(SERVER_ERROR)
     */
    public void deleteProfileImage(User user) {
        user.updateImageUrl(null);

        save(user);
    }

    /**
     * 공개 여부 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updateIsPublic(User user, Boolean isPublic) {
        user.updateIsPublic(isPublic);

        save(user);
    }

    /**
     * 자기소개 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updateProfileDescription(User user, String profileDescription) {
        user.updateProfileDescription(profileDescription);

        save(user);
    }

    /**
     * 아이디로 테스트 회원 단건 조회 | main |
     * 404(USER_NOT_FOUND)
     */
    public User findOneTestUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedIsFalse(username)
                .orElseThrow(() -> {
                    throw new CustomException(null, USER_NOT_FOUND);
                });
    }

    /**
     * 포지션과 기술들 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updatePositionAndSkills(User user, String position, List<Skill> createdSkills, List<Skill> deletedSkills) {
        updatePosition(user, position);
        addSkills(user, createdSkills);
        removeSkills(user, deletedSkills);

        save(user);
    }

    /**
     * 학력들과 경력들 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updateEducationsAndWorks(User user,
                                         List<Education> createdEducations,
                                         List<Education> deletedEducations,
                                         List<Work> createdWorks,
                                         List<Work> deletedWorks) {
        addEducations(user, createdEducations);
        removeEducations(user, deletedEducations);
        addWorks(user, createdWorks);
        removeWorks(user, deletedWorks);

        save(user);
    }

    /**
     * 포트폴리오들 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updatePortfolios(User user, List<Portfolio> createdPortfolios, List<Portfolio> deletedPortfolios) {
        addPortfolios(user, createdPortfolios);
        removePortfolios(user, deletedPortfolios);

        save(user);
    }

    /**
     * 포지션과 프로필 정렬 기준으로 회원 페이징 다건 조회 | main |
     * 500(SERVER_ERROR)
     */
    public Page<User> findPagePositionProfileOrder(String position,
                                                   String profileOrder,
                                                   Integer pageFrom,
                                                   Integer pageSize) {
        Position p = Position.fromString(position);
        ProfileOrder po = ProfileOrder.fromString(profileOrder);
        Pageable pageable = utilityProvider.validatePaging(pageFrom, pageSize, 20);

        Page<User> users;

        if (p.equals(Position.NONE)) {
            switch (po.name()) {
                case "RATING":
                    users = findPageByRating(pageable);
                    break;
                case "POPULARITY":
                    users = findPageByPopularity(pageable);
                    break;
                default:
                    users = findPageByActive(pageable);
                    break;
            }
        } else {
            switch (po.name()) {
                case "RATING":
                    users = findPagePositionByRating(p, pageable);
                    break;
                case "POPULARITY":
                    users = findPagePositionByPopularity(p, pageable);
                    break;
                default:
                    users = findPagePositionByActive(p, pageable);
                    break;
            }
        }

        return users;
    }

    /**
     * 팀 들어가기 | main |
     * 500(SERVER_ERROR)
     */
    public void joinTeam(User user, Team team) {
        updateCurrentTeam(user, team.getId());
    }

    /**
     * 팀 찜 여부 확인 | main |
     */
    public Boolean isFavoriteTeam(User user, ObjectId teamId) {
        if (user.getCurrentTeamId() != null)
            return null;

        return user.getFavoriteTeamIds().contains(teamId);
    }

    /**
     * 현재 팀 종료 | main |
     * 500(SERVER_ERROR)
     */
    public void exitCurrentTeam(List<User> users, boolean isComplete) {
        for (User user : users) {
            user.quitTeam(isComplete);
            save(user);
        }
    }

    /**
     * 식별자로 회원 전체 조회 | main |
     * 500(SERVER_ERROR)
     */
    public List<User> findAllId(List<ObjectId> userIds) {
        List<User> users = new ArrayList<>();
        try {
            for(ObjectId userId : userIds) {
                Optional<User> user = userRepository.findByIdAndIsDeletedIsFalse(userId);
                user.ifPresent(users::add);
            }
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }

        return users;
    }

    /**
     * 팀 찜 업데이트 | main |
     * TODO
     */
    public void updateFavoriteTeam(User user, Team team, boolean isAddFavorite) {
        user.updateFavoriteTeamId(team.getId(), isAddFavorite);

        save(user);
    }

    /**
     * 팀 생성 전 검증 | sub |
     * 409(EXISTING_CURRENT_TEAM / NON_EXISTING_POSITION)
     */
    public void validatePreCreateTeam(User user) {
        hasNoCurrentTeam(user);
        hasPosition(user);
    }

    /**
     * 가입 전 검증 | sub |
     * 409(EXISTING_USERNAME / UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void validatePreRegister(UserSaveReqDto request) {
        isExistingUsername(request.getUsername());
        isMatchingPassword(request.getPassword(), request.getPasswordReEntered());
        validateNickname(request.getNickname());
    }

    /**
     * 마지막 요청일 업데이트 | sub |
     * 500(SERVER_ERROR)
     */
    public void updateLastRequestDate(User user) {
        try {
            user.updateLastRequestDate();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }

        save(user);
    }

    /**
     * 현재 팀 여부 검증 | sub |
     * 409(NON_EXISTING_CURRENT_TEAM)
     */
    public void hasCurrentTeam(User user) {
        if (user.getCurrentTeamId() == null)
            throw new CustomException(null, NON_EXISTING_CURRENT_TEAM);
    }

    /**
     * 아이디로 회원 단건 조회 |
     * 401(LOGIN_FAIL)
     */
    private User findOneUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedIsFalse(username)
                .orElseThrow(() -> {
                    throw new CustomException(null, LOGIN_FAIL);
                });
    }

    /**
     * 연락처로 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneEmail(Contact contact) {
        return userRepository.findByContactAndIsDeletedIsFalse(contact)
                .orElseThrow(() -> {
                    throw new CustomException(null, USER_NOT_FOUND);
                });
    }

    /**
     * 식별자로 회원 단건 조회 | sub |
     * 404(USER_NOT_FOUND)
     */
    public User findOne(String userId) {
        ObjectId id = utilityProvider.toObjectId(userId);

        return userRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> {
                    throw new CustomException(null, USER_NOT_FOUND);
                });
    }

    /**
     * 식별자로 타회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    private User findOneOther(String userId) {
        User user = findOne(userId);

        user.incrementVisitedCnt();
        save(user);

        return user;
    }

    /**
     * 중복 아이디 여부 확인 |
     * 409(EXISTING_USERNAME)
     */
    private void isExistingUsername(String username) {
        userRepository.findByUsernameAndIsDeletedIsFalse(username)
                .ifPresent(u -> {
                    throw new CustomException(null, EXISTING_USERNAME);
                });
    }

    /**
     * 중복 닉네임 여부 확인 |
     * 409(EXISTING_NICKNAME)
     */
    private void isExistingNickname(String nickname) {
        userRepository.findByNicknameAndIsDeletedIsFalse(nickname)
                .ifPresent(u -> {
                    throw new CustomException(null, EXISTING_NICKNAME);
                });
    }

    /**
     * 비밀번호와 비밀번호 재입력 검증 |
     * 400(PASSWORD_MATCH_INVALID)
     */
    private void isMatchingPassword(String password, String passwordReEnter) {
        if (!password.equals(passwordReEnter))
            throw new CustomException(null, PASSWORD_MATCH_INVALID);
    }

    /**
     * 특정 포지션을 활동순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findPagePositionByActive(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsPublicIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(
                    position.getType(),
                    pageable
            );
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 활동순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findPageByActive(Pageable pageable) {
        try {
            return userRepository.findAllByIsPublicIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 인기순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findPagePositionByPopularity(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsPublicIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                    position.getType(),
                    pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 인기순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findPageByPopularity(Pageable pageable) {
        try {
            return userRepository.findAllByIsPublicIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 평점순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findPagePositionByRating(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsPublicIsTrueAndIsDeletedIsFalseOrderByRatingDesc(
                    position.getType(),
                    pageable
            );
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 평점순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findPageByRating(Pageable pageable) {
        try {
            return userRepository.findAllByIsPublicIsTrueAndIsDeletedIsFalseOrderByRatingDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 저장 |
     * 500(SERVER_ERROR)
     */
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 소프트 삭제 |
     * 500(SERVER_ERROR)
     */
    public void softDelete(User user) {
        user.delete();

        save(user);
    }

    /**
     * 회원 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDelete(User user) {
        try {
            userRepository.delete(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 현재 팀 무존재 여부 검증 |
     * 409(EXISTING_CURRENT_TEAM)
     */
    private void hasNoCurrentTeam(User user) {
        if (user.getCurrentTeamId() != null)
            throw new CustomException(null, EXISTING_CURRENT_TEAM);
    }

    /**
     * 현재 팀 업데이트 |
     * 500(SERVER_ERROR)
     */
    private void updateCurrentTeam(User user, ObjectId teamId) {
        user.joinTeam(teamId);

        save(user);
    }

    /**
     * 포지션 존재 여부 검증 |
     * 409(NON_EXISTING_POSITION)
     */
    private void hasPosition(User user) {
        if (user.getPosition().equals(Position.NONE.getType()) || user.getPosition() == null)
            throw new CustomException(null, NON_EXISTING_POSITION);
    }

    /**
     * 포지션 업데이트
     */
    private void updatePosition(User user, String position) {
        if (!position.isBlank())
            user.updatePosition(Position.fromString(position));
    }

    /**
     * 학력 추가
     */
    private void addEducations(User user, List<Education> educations) {
        for (Education education : educations)
            user.addEducation(education);
    }

    /**
     * 학력 제거
     */
    private void removeEducations(User user, List<Education> educations) {
        for (Education education : educations)
            user.removeEducation(education);
    }

    /**
     * 포트폴리오 추가
     */
    private void addPortfolios(User user, List<Portfolio> portfolios) {
        for (Portfolio portfolio : portfolios)
            user.addPortfolio(portfolio);
    }

    /**
     * 포트폴리오 제거
     */
    private void removePortfolios(User user, List<Portfolio> portfolios) {
        for (Portfolio portfolio : portfolios)
            user.removePortfolio(portfolio);
    }

    /**
     * 기술 추가
     */
    private void addSkills(User user, List<Skill> skills) {
        for (Skill skill : skills)
            user.addSkill(skill);
    }

    /**
     * 기술 제거
     */
    private void removeSkills(User user, List<Skill> skills) {
        for (Skill skill : skills)
            user.removeSkill(skill);
    }

    /**
     * 경력 추가
     */
    private void addWorks(User user, List<Work> works) {
        for (Work work : works)
            user.addWork(work);
    }

    /**
     * 경력 제거
     */
    private void removeWorks(User user, List<Work> works) {
        for (Work work : works)
            user.removeWork(work);
    }
}
