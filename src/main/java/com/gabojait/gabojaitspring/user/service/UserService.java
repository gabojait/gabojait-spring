package com.gabojait.gabojaitspring.user.service;

import com.gabojait.gabojaitspring.common.util.EmailProvider;
import com.gabojait.gabojaitspring.common.util.FileProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.favorite.repository.FavoriteUserRepository;
import com.gabojait.gabojaitspring.fcm.domain.Fcm;
import com.gabojait.gabojaitspring.fcm.repository.FcmRepository;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.ProfileOrder;
import com.gabojait.gabojaitspring.profile.dto.ProfileSeekPageDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileOfferAndFavoriteResDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.UserRole;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.dto.req.UserFindPasswordReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserLoginReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserRegisterReqDto;
import com.gabojait.gabojaitspring.user.dto.req.UserRenewTokenReqDto;
import com.gabojait.gabojaitspring.user.repository.ContactRepository;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import com.gabojait.gabojaitspring.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
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
@Transactional
@RequiredArgsConstructor
public class UserService {

    @Value(value = "${s3.bucket.profile-img}")
    private String bucketName;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ContactRepository contactRepository;
    private final FcmRepository fcmRepository;
    private final OfferRepository offerRepository;
    private final FavoriteUserRepository favoriteUserRepository;
    private final GeneralProvider generalProvider;
    private final EmailProvider emailProvider;
    private final FileProvider fileProvider;

    /**
     * 아이디 검증 |
     * 409(UNAVAILABLE_USERNAME / EXISTING_USERNAME)
     */
    public void validateUsername(String username) {
        if (username.toLowerCase().contains("admin") || username.toLowerCase().contains("gabojait"))
            throw new CustomException(UNAVAILABLE_USERNAME);

        validateDuplicateUsername(username);
    }

    /**
     * 닉네임 검증 |
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void validateNickname(String nickname) {
        if (nickname.toLowerCase().contains("관리자") || nickname.toLowerCase().contains("가보자잇"))
            throw new CustomException(UNAVAILABLE_NICKNAME);

        validateDuplicateNickname(nickname);
    }

    /**
     * 회원 가입 |
     * 400(PASSWORD_MATCH_INVALID)
     * 404(CONTACT_NOT_FOUND)
     * 409(UNAVAILABLE_USERNAME / EXISTING_USERNAME / UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     * 500(SERVER_ERROR)
     */
    public User register(UserRegisterReqDto request) {
        validateUsername(request.getUsername());
        validateNickname(request.getNickname());
        validateMatchingPassword(request.getPassword(), request.getPasswordReEntered());
        Contact contact = findOneVerifiedUnregisteredContact(request.getEmail());
        String password = generalProvider.encodePassword(request.getPassword());

        User user = request.toEntity(password, contact);
        saveUser(user);

        UserRole userRole = createUserRole(user);
        saveUserRole(userRole);

        if (request.getFcmToken() != null)
            createFcm(request.getFcmToken(), user);

        return user;
    }

    /**
     * 회원 로그인 |
     * 401(LOGIN_UNAUTHENTICATED)
     * 500(SERVER_ERROR)
     */
    public User login(UserLoginReqDto request) {
        User user = findOneUser(request.getUsername());

        boolean isVerified = generalProvider.verifyPassword(user, request.getPassword());
        if (!isVerified)
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        if (request.getFcmToken() != null)
            createFcm(request.getFcmToken(), user);
        updateLastRequestAt(user);

        return user;
    }

    /**
     * 회원 로그아웃 |
     * 500(SERVER_ERROR)
     */
    public void logout(User user, String fcmToken) {
        Optional<Fcm> fcm = findOneFcm(fcmToken, user);

        fcm.ifPresent(this::hardDeleteFcm);
    }

    /**
     * 마지막 요청일 업데이트 |
     * 500(SERVER_ERROR)
     */
    public void updateLastRequestAt(User user) {
        user.updateLastRequestAt();
    }

    /**
     * 아이디 이메일로 전송 |
     * 404(CONTACT_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR / SERVER_ERROR)
     */
    public void sendUsernameToEmail(String email) {
        Contact contact = findOneRegisteredContact(email);
        sendUsernameEmail(contact.getUser());
    }

    /**
     * 비밀번호 이메일로 전송 |
     * 400(USERNAME_EMAIL_MATCH_INVALID)
     * 404(CONTACT_NOT_FOUND)
     * 500(EMAIL_SEND_ERROR)
     */
    public void sendPasswordToEmail(UserFindPasswordReqDto request) {
        Contact contact = findOneRegisteredContact(request.getEmail());
        User user = contact.getUser();

        if (!user.getUsername().equals(request.getUsername()))
            throw new CustomException(USERNAME_EMAIL_MATCH_INVALID);

        String tempPassword = generalProvider.generateRandomCode(8);
        updatePassword(user, tempPassword, tempPassword, true);

        emailProvider.sendEmail(
                user.getContact().getEmail(),
                "[가보자IT] 비밀번호 찾기",
                user.getUsername() + "님 안녕하세요!🙇🏻<br>임시 비밀번호를 제공해 드립니다.<br>접속 후 비밀번호를 변경 해주세요.",
                tempPassword
        );
    }

    /**
     * 비밀번호 검증 |
     * 401(PASSWORD_UNAUTHENTICATED)
     */
    public void verifyPassword(User user, String password) {
        boolean isVerified = generalProvider.verifyPassword(user, password);

        if (!isVerified)
            throw new CustomException(PASSWORD_UNAUTHENTICATED);
    }

    /**
     * 닉네임 업데이트 |
     * 409(UNAVAILABLE_NICKNAME / EXISTING_NICKNAME)
     */
    public void updateNickname(User user, String nickname) {
        validateNickname(nickname);

        user.updateNickname(nickname);
    }

    /**
     * 비밀번호 업데이트 |
     * 400(PASSWORD_MATCH_INVALID)
     */
    public void updatePassword(User user, String password, String passwordReEntered, boolean isTemporaryPassword) {
        if (!isTemporaryPassword)
            validateMatchingPassword(password, passwordReEntered);

        String encodedPassword = generalProvider.encodePassword(password);
        user.updatePassword(encodedPassword, isTemporaryPassword);
    }

    /**
     * 알림 여부 업데이트
     */
    public void updateIsNotified(User user, boolean isNotified) {
        user.updateIsNotified(isNotified);
    }

    /**
     * 프로필 이미지 업로드 |
     * 400(FILE_FIELD_REQUIRED)
     * 415(IMAGE_TYPE_UNSUPPORTED)
     * 500(SERVER_ERROR)
     */
    public void uploadProfileImage(User user, MultipartFile multipartFile) {
        String url = fileProvider.upload(bucketName,
                user.getId().toString(),
                UUID.randomUUID().toString(),
                multipartFile,
                true);

        user.updateImageUrl(url);
    }

    /**
     * 프로필 이미지 삭제
     */
    public void deleteProfileImage(User user) {
        user.updateImageUrl(null);
    }

    /**
     * 팀 찾기 여부 업데이트
     */
    public void updateIsSeekingTeam(User user, boolean isSeekingTeam) {
        user.updateIsSeekingTeam(isSeekingTeam);
    }

    /**
     * 자기소개 업데이트
     */
    public void updateProfileDescription(User user, String profileDescription) {
        user.updateProfileDescription(profileDescription);
    }

    /**
     * 토큰 업데이트 |
     * 500(SERVER_ERROR)
     */
    public void updateToken(User user, UserRenewTokenReqDto request) {
        updateLastRequestAt(user);

        if (request.getFcmToken() != null)
            createFcm(request.getFcmToken(), user);
    }

    /**
     * 회원 식별자로 회원으로 타 회원 프로필 단건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public ProfileOfferAndFavoriteResDto findOneOtherProfile(Long otherUserId, User user) {
        if (user.getId().equals(otherUserId))
            return new ProfileOfferAndFavoriteResDto(user, List.of(), null);

        User otherUser = findOneUser(otherUserId);

        otherUser.incrementVisitedCnt();

        List<Offer> offers = new ArrayList<>();
        Boolean isFavorite = null;

        if (user.isLeader()) {
            offers = findAllOffersToUser(user, otherUser);
            isFavorite = isFavoriteUser(otherUser, user);
        }

        return new ProfileOfferAndFavoriteResDto(otherUser, offers, isFavorite);
    }

    /**
     * 포지션과 프로필 정렬 기준으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    public ProfileSeekPageDto findManyUsersByPositionWithProfileOrder(String position,
                                                                      String profileOrder,
                                                                      Integer pageFrom,
                                                                      Integer pageSize,
                                                                      User user) {
        Position p = Position.fromString(position);
        ProfileOrder po = ProfileOrder.fromString(profileOrder);
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);

        Page<User> users;

        if (p.equals(Position.NONE)) {
            switch (po.name().toLowerCase()) {
                case "rating":
                    users = findManyUsersByRating(pageable);
                    break;
                case "popularity":
                    users = findManyUsersByPopularity(pageable);
                    break;
                default:
                    users = findManyUsersByActive(pageable);
                    break;
            }
        } else {
            switch (po.name().toLowerCase()) {
                case "rating":
                    users = findManyUsersPositionByRating(p, pageable);
                    break;
                case "popularity":
                    users = findManyUsersPositionByPopularity(p, pageable);
                    break;
                default:
                    users = findManyUsersPositionByActive(p, pageable);
                    break;
            }
        }

        List<ProfileSeekResDto> profileSeekResDtos = new ArrayList<>();

        if (user.isLeader()) {
            for (User u : users) {
                List<Offer> offers = findAllOffersToUser(user, u);
                profileSeekResDtos.add(new ProfileSeekResDto(u, offers));
            }
        } else {
            for (User u : users)
                profileSeekResDtos.add(new ProfileSeekResDto(u, List.of()));
        }

        return new ProfileSeekPageDto(profileSeekResDtos, users.getTotalPages());
    }

    /**
     * 회원 탈퇴 |
     * 500(SERVER_ERROR)
     */
    public void deleteAccount(User user) {
        for(Fcm fcm : user.getFcms())
            hardDeleteFcm(fcm);

        user.getContact().deleteAccount();
        user.deleteAccount();
    }

    /**
     * FCM 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    private void hardDeleteFcm(Fcm fcm) {
        try {
            fcmRepository.delete(fcm);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveUser(User user) {
        try {
            userRepository.save(user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * FCM 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveFcm(Fcm fcm) {
        try {
            fcmRepository.save(fcm);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 권한 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveUserRole(UserRole userRole) {
        try {
            userRoleRepository.save(userRole);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 아이디로 회원 단건 조회 |
     * 401(LOGIN_UNAUTHENTICATED)
     * 500(SERVER_ERROR)
     */
    private User findOneUser(String username) {
        Optional<User> user = userRepository.findByUsernameAndIsDeletedIsFalse(username);

        if (user.isEmpty())
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        Optional<UserRole> userRole = findOneUserRole(user.get());

        if (userRole.isEmpty())
            throw new CustomException(LOGIN_UNAUTHENTICATED);

        return user.get();
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
     * 회원과 권한으로 회원 권한 단건 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<UserRole> findOneUserRole(User user) {
        try {
            return userRoleRepository.findByUserAndRole(user, Role.USER.name());
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 평점순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersByRating(Pageable pageable) {
        try {
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 인기순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersByPopularity(Pageable pageable) {
        try {
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 활동순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersByActive(Pageable pageable) {
        try {
            return userRepository.findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestAtDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 평점순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersPositionByRating(Position position, Pageable pageable) {
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
     * 특정 포지션을 인기순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersPositionByPopularity(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                    position.getType(),
                    pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 활동순으로 회원 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<User> findManyUsersPositionByActive(Position position, Pageable pageable) {
        try {
            return userRepository.findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestAtDesc(
                    position.getType(),
                    pageable
            );
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 인증되고 가입되지 않은 연락처 단건 조회 |
     * 404(CONTACT_NOT_FOUND)
     */
    private Contact findOneVerifiedUnregisteredContact(String email) {
        Optional<Contact> contact = contactRepository.findByEmailAndIsVerifiedIsTrueAndIsDeletedIsFalse(email);

        if (contact.isEmpty())
            throw new CustomException(CONTACT_NOT_FOUND);
        else if (contact.get().getUser() != null)
            throw new CustomException(CONTACT_NOT_FOUND);

        return contact.get();
    }

    /**
     * 이메일로 가입된 연락처 단건 조회 | main |
     * 404(CONTACT_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    private Contact findOneRegisteredContact(String email) {
        try {
            Optional<Contact> contact = contactRepository.findByEmailAndIsVerifiedIsTrueAndIsDeletedIsFalse(email);

            if (contact.isEmpty() || contact.get().getUser() == null)
                throw new CustomException(CONTACT_NOT_FOUND);

            return contact.get();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * FCM 토큰과 회원으로 FCM 단건 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<Fcm> findOneFcm(String fcmToken, User user) {
        try {
            return fcmRepository.findByFcmTokenAndUserAndIsDeletedIsFalse(fcmToken, user);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 리더와 회원으로 리더가 특정 회원에게 보낸 전체 제안 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Offer> findAllOffersToUser(User leader, User user) {
        Team team = leader.getTeamMembers().get(leader.getTeamMembers().size() - 1).getTeam();

        try {
            return offerRepository.findAllByUserAndTeamAndIsAcceptedIsNullAndIsDeletedIsFalse(user, team);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 아이디 이메일로 전송 |
     * 500(EMAIL_SEND_ERROR)
     */
    private void sendUsernameEmail(User user) {
        emailProvider.sendEmail(
                user.getContact().getEmail(),
                "[가보자IT] 아이디 찾기",
                "해당 이메일로 가입된 아이디 정보입니다.",
                user.getUsername()
        );
    }

    /**
     * FCM 생성 |
     * 500(SERVER_ERROR)
     */
    private void createFcm(String fcmToken, User user) {
        if (fcmToken.isBlank())
            return;

        Optional<Fcm> fcm = findOneFcm(fcmToken, user);

        if (fcm.isPresent())
            return;

        Fcm newFcm = Fcm.builder()
                .fcmToken(fcmToken)
                .user(user)
                .build();

        saveFcm(newFcm);
    }

    /**
     * 회원 권한 생성
     */
    private UserRole createUserRole(User user) {
        return UserRole.builder()
                .user(user)
                .role(Role.USER)
                .build();
    }

    /**
     * 찜한 회원 여부 확인 |
     * 500(SERVER_ERROR)
     */
    private boolean isFavoriteUser(User user, User leader) {
        Team team = leader.getTeamMembers().get(leader.getTeamMembers().size() - 1).getTeam();

        try {
            Optional<FavoriteUser> favoriteUser =
                    favoriteUserRepository.findByTeamAndUserAndIsDeletedIsFalse(team, user);

            return favoriteUser.isPresent();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 중복 아이디 여부 검증 |
     * 409(EXISTING_USERNAME)
     * 500(SERVER_ERROR)
     */
    private void validateDuplicateUsername(String username) {
        Optional<User> user = userRepository.findByUsernameAndIsDeletedIsFalse(username);

        if (user.isPresent()) {
            Optional<UserRole> userRole = findOneUserRole(user.get());

            if (userRole.isPresent())
                throw new CustomException(EXISTING_USERNAME);
        }
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
    private void validateMatchingPassword(String password, String passwordReEntered) {
        if (!password.equals(passwordReEntered))
            throw new CustomException(PASSWORD_MATCH_INVALID);
    }
}
