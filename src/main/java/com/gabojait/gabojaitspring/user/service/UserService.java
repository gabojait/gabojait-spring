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
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.dto.req.UserLoginReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserRegisterReqDto;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
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
            throw new CustomException(UNAVAILABLE_USERNAME);

        validateDuplicateUsername(username);
    }

    /**
     * 닉네임 검증 | main |
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void validateNickname(String nickname) {
        if (nickname.toLowerCase().contains("관리자") || nickname.toLowerCase().contains("가보자잇"))
            throw new CustomException(UNAVAILABLE_NICKNAME);

        validateDuplicateNickname(nickname);
    }

    /**
     * 회원 가입 | main |
     * 500(SERVER_ERROR)
     */
    public User register(UserRegisterReqDto request, Contact contact) {
        String password = utilityProvider.encodePassword(request.getPassword());

        User user =  save(request.toEntity(password, contact));

        updateFcmToken(user, request.getFcmToken(), true);

        return user;
    }

    /**
     * 회원 로그인 | main |
     * 401(LOGIN_FAIL)
     * 500(SERVER_ERROR)
     */
    public User login(UserLoginReqDto request) {
        User user = findOneByUsername(request.getUsername());

        boolean isVerified = utilityProvider.verifyPassword(user, request.getPassword());
        if (!isVerified)
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        updateFcmToken(user, request.getFcmToken(), true);

        return user;
    }

    /**
     * 식별자로 타인 단건 조회 | main |
     * 400(ID_CONVERT_INVALID)
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public User findOneOtherById(User user, String otherUserId) {
        if (user.getId().toString().equals(otherUserId))
            return user;

        User otherUser = findOneById(otherUserId);
        otherUser.incrementVisitedCnt();
        save(user);

        return otherUser;
    }

    /**
     * 아이디 이메일로 전송 | main |
     * 404(USER_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR)
     */
    public void sendUsernameEmail(Contact contact) {
        User user = findOneByContact(contact);

        emailProvider.sendEmail(
                user.getContact().getEmail(),
                "[가보자IT] 아이디 찾기",
                user.getUsername() + "님 안녕하세요!🙇🏻<br>해당 이메일로 가입된 아이디 정보입니다.",
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
        User user = findOneByContact(contact);

        if (!user.getUsername().equals(username))
            throw new CustomException(USERNAME_EMAIL_MATCH_INVALID);

        String tempPassword = utilityProvider.generateRandomCode(8);
        updatePassword(user, tempPassword, tempPassword, true);

        emailProvider.sendEmail(
                user.getContact().getEmail(),
                "[가보자IT] 비밀번호 찾기",
                user.getUsername() + "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
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
            validateMatchingPassword(password, passwordReEntered);

        user.updatePassword(utilityProvider.encodePassword(password), isTemporaryPassword);

        save(user);
    }

    /**
     * 닉네임 업데이트 | main |
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     * 500(SERVER_ERROR)
     */
    public void updateNickname(User user, String nickname) {
        validateNickname(nickname);

        user.updateNickname(nickname);

        save(user);
    }

    /**
     * 비밀번호 검증 | main |
     * 400(PASSWORD_INVALID)
     */
    public void validatePassword(User user, String password) {
        boolean isVerified = utilityProvider.verifyPassword(user, password);
        if (!isVerified)
            throw new CustomException(PASSWORD_UNAUTHENTICATED);
    }

    /**
     * 프로필 이미지 업데이트 | main |
     * 400(FILE_FIELD_REQUIRED)
     * 415(IMAGE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public void uploadProfileImage(User user, MultipartFile multipartFile) {
        if (multipartFile == null)
            throw new CustomException(FILE_FIELD_REQUIRED);

        String url = fileProvider.upload(bucketName,
                user.getId().toString(),
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
     * 팀 찾기 여부 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updateIsSeekingTeam(User user, Boolean isPublic) {
        user.updateIsSeekingTeam(isPublic);

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
    public User findOneTestByUsername(String username) {
        return userRepository.findByUsernameAndRolesInAndIsDeletedIsFalse(username, Role.USER.name())
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 포지션과 기술들 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updatePositionAndSkills(User user,
                                        String position,
                                        List<Skill> createdSkills,
                                        List<Skill> updatedSkills,
                                        List<Skill> deletedSkills) {
        // update
        user.updatePosition(Position.fromString(position));
        user.removeAllSkills(updatedSkills);
        user.addAllSkills(updatedSkills);
        // create
        user.addAllSkills(createdSkills);
        // delete
        user.removeAllSkills(deletedSkills);

        save(user);
    }

    /**
     * 학력들과 경력들 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updateEducationsAndWorks(User user,
                                         List<Education> createdEducations,
                                         List<Education> updatedEducations,
                                         List<Education> deletedEducations,
                                         List<Work> createdWorks,
                                         List<Work> updatedWorks,
                                         List<Work> deletedWorks) {
        // update
        user.removeAllEducations(updatedEducations);
        user.addAllEducations(updatedEducations);
        user.removeAllWorks(updatedWorks);
        user.addAllWorks(updatedWorks);
        // create
        user.addAllEducations(createdEducations);
        user.addAllWorks(createdWorks);
        // delete
        user.removeAllEducations(deletedEducations);
        user.removeAllWorks(deletedWorks);

        save(user);
    }

    /**
     * 포트폴리오들 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updatePortfolios(User user,
                                 List<Portfolio> createdPortfolios,
                                 List<Portfolio> updatedPortfolios,
                                 List<Portfolio> deletedPortfolios) {
        // update
        user.removeAllPortfolios(updatedPortfolios);
        user.addAllPortfolios(updatedPortfolios);
        // create
        user.addAllPortfolios(createdPortfolios);
        // delete
        user.removeAllPortfolios(deletedPortfolios);

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
            switch (po.name().toLowerCase()) {
                case "rating":
                    users = findPageByRating(pageable);
                    break;
                case "popularity":
                    users = findPageByPopularity(pageable);
                    break;
                default:
                    users = findPageByActive(pageable);
                    break;
            }
        } else {
            switch (po.name().toLowerCase()) {
                case "rating":
                    users = findPagePositionByRating(p, pageable);
                    break;
                case "popularity":
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
    public void joinTeam(User user, Team team, boolean isLeader) {
        user.joinTeam(team.getId(), isLeader);
        save(user);
    }

    /**
     * 현재 팀 종료 | main |
     * 500(SERVER_ERROR)
     */
    public void exitCurrentTeam(List<User> users, ObjectId teamId, boolean isComplete) {
        for (User user : users) {
            user.quitTeam(teamId, isComplete);
            save(user);
        }
    }

    /**
     * 식별자로 회원 전체 조회 | main |
     * 500(SERVER_ERROR)
     */
    public List<User> findAllById(List<ObjectId> userIds) {
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
     * 500(SERVER_ERROR)
     */
    public void updateFavoriteTeam(User user, Team team, boolean isAddFavorite) {
        user.updateFavoriteTeamId(team.getId(), isAddFavorite);
        save(user);
    }

    /**
     * 회원 또는 팀 제안 | main |
     * 400(ID_CONVERT_INVALID)
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void offer(User user, boolean isOfferedByUser) {
        user.offer(isOfferedByUser);

        save(user);
    }

    /**
     * 제안 결정 | main |
     * 500(SERVER_ERROR)
     */
    public void offerDecided(User user, ObjectId teamId, boolean isAccepted) {
        if (!isAccepted)
            return;

        user.joinTeam(teamId, false);
        save(user);
    }

    /**
     * 평점 업데이트 | main |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void updateRating(List<Review> reviews) {
        for (Review review : reviews) {
            User reviewee = findOneById(review.getRevieweeId().toString());

            reviewee.updateRating(review);

            save(reviewee);
        }
    }

    /**
     * Fcm 토큰 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updateFcmToken(User user, String fcmToken, boolean isAdd) {
        if (isAdd)
            user.addFcmToken(fcmToken);
        else
            user.removeFcmToken(fcmToken);

        save(user);
    }

    /**
     * 알림 여부 업데이트 | main |
     * 500(SERVER_ERROR)
     */
    public void updateIsNotified(User user, boolean isNotified) {
        user.updateIsNotified(isNotified);

        save(user);
    }

    /**
     * 팀 생성 전 검증 | sub |
     * 409(EXISTING_CURRENT_TEAM / NON_EXISTING_POSITION)
     */
    public void validatePreCreateTeam(User user) {
        validateHasNoCurrentTeam(user);
        validatePositionSelected(user);
    }

    /**
     * 가입 전 검증 | sub |
     * 400(PASSWORD_MATCH_INVALID)
     * 409(EXISTING_USERNAME / UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void validatePreRegister(UserRegisterReqDto request) {
        validateDuplicateUsername(request.getUsername());
        validateMatchingPassword(request.getPassword(), request.getPasswordReEntered());
        validateNickname(request.getNickname());
    }

    /**
     * 마지막 요청일 업데이트 | sub |
     * 500(SERVER_ERROR)
     */
    public void updateLastRequestDate(User user) {
        user.updateLastRequestDate();
        save(user);
    }

    /**
     * 현재 팀 존재 여부 검증 | sub |
     * 409(NON_EXISTING_CURRENT_TEAM)
     */
    public void validateHasCurrentTeam(User user) {
        if (!user.hasCurrentTeam())
            throw new CustomException(NON_EXISTING_CURRENT_TEAM);
    }

    /**
     * 현재 팀 미존재 여부 검증 | sub |
     * 409(EXISTING_CURRENT_TEAM)
     */
    public void validateHasNoCurrentTeam(User user) {
        if (user.hasCurrentTeam())
            throw new CustomException(EXISTING_CURRENT_TEAM);
    }

    /**
     * 아이디로 회원 단건 조회 |
     * 401(LOGIN_UNAUTHENTICATED)
     */
    private User findOneByUsername(String username) {
        return userRepository.findByUsernameAndRolesInAndIsDeletedIsFalse(username, Role.USER.name())
                .orElseThrow(() -> {
                    throw new CustomException(LOGIN_UNAUTHENTICATED);
                });
    }

    /**
     * 연락처로 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneByContact(Contact contact) {
        return userRepository.findByContactAndIsDeletedIsFalse(contact)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 식별자로 회원 단건 조회 | sub |
     * 400(ID_CONVERT_INVALID)
     * 404(USER_NOT_FOUND)
     */
    public User findOneById(String userId) {
        ObjectId id = utilityProvider.toObjectId(userId);

        return userRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 특정 포지션을 활동순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findPagePositionByActive(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(
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
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(pageable);
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
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
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
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
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
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(
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
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(pageable);
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
     * 중복 아이디 여부 검증 |
     * 409(EXISTING_USERNAME)
     */
    private void validateDuplicateUsername(String username) {
        userRepository.findByUsernameAndRolesInAndIsDeletedIsFalse(username, Role.USER.name())
                .ifPresent(u -> {
                    throw new CustomException(EXISTING_USERNAME);
                });
    }

    /**
     * 중복 닉네임 여부 검증 |
     * 409(EXISTING_NICKNAME)
     */
    private void validateDuplicateNickname(String nickname) {
        userRepository.findByNicknameAndIsDeletedIsFalse(nickname)
                .ifPresent(u -> {
                    throw new CustomException(EXISTING_NICKNAME);
                });
    }

    /**
     * 비밀번호와 비밀번호 재입력 검증 |
     * 400(PASSWORD_MATCH_INVALID)
     */
    public void validateMatchingPassword(String password, String passwordReEnter) {
        if (!password.equals(passwordReEnter))
            throw new CustomException(PASSWORD_MATCH_INVALID);
    }


    /**
     * 포지션 선택 여부 검증 |
     * 409(NON_EXISTING_POSITION)
     */
    private void validatePositionSelected(User user) {
        if (!user.hasPosition())
            throw new CustomException(NON_EXISTING_POSITION);
    }
}
