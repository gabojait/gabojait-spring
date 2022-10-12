package com.inuappcenter.gabojaitspring.user.service;

import com.inuappcenter.gabojaitspring.email.service.EmailService;
import com.inuappcenter.gabojaitspring.exception.http.ConflictException;
import com.inuappcenter.gabojaitspring.exception.http.InternalServerErrorException;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ContactService contactService;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;



    /**
     * 중복 유저이름 존재 여부 확인 |
     * 중복 유저이름 존재를 파악하고, 이미 사용중인 유저이름이면 409(Conflict)를 던진다. 만약 조회 중 에러가 발생하면 500(Internal Server
     * Error)를 던진다.
     */
    public void isExistingUsername(String username) {
        log.info("IN PROGRESS | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + username);
        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    throw new ConflictException("이미 사용중인 아이디입니다");
                });
        log.info("COMPLETE | 중복 유저이름 존재 여부 확인 At " + LocalDateTime.now() + " | " + username);
    }

    /**
     * User 저장 |
     * User의 Contact를 조회한다. 조회되는 Contact의 이메일이 인증이 안됐을 경우 409(Conflict)를 던진다. 이메일 인증을 했을 경우 회원가입을
     * 진행하고 비밀번호는 인코드한다. 만약 User 정보 저장 중 에러가 발생하면 500(Internal Server Error)를 던진다.
     */
    public UserDefaultResponseDto save(UserSaveRequestDto request) {
        log.info("IN PROGRESS | User 저장 At " + LocalDateTime.now() + " | " + request.toString());
        Contact foundContact = contactService.findOneContact(request.getEmail());
        if (!foundContact.getIsVerified()) {
            throw new ConflictException("이메일 인증을 해주세요");
        }
        contactService.register(foundContact);
        try {
            User roleAssignedUser = assignAsUser(request.toEntity(foundContact));
            roleAssignedUser.setPassword(passwordEncoder.encode(roleAssignedUser.getPassword()));
            User insertedUser = userRepository.insert(roleAssignedUser);
            log.info("COMPLETE | User 저장 At " + LocalDateTime.now() + " | " + insertedUser);
            return new UserDefaultResponseDto(insertedUser);
        } catch (Exception e) {
            throw new InternalServerErrorException("유저 저장 중 에러 발생", e);
        }
    }

    /**
     * 사용자 역할 부여 |
     * User에게 사용자 역할을 부여한다.
     */
    public User assignAsUser(User user) {
        log.info("IN PROGRESS | 사용자 역할 부여 At " + LocalDateTime.now() + " | " + user.toString());
        user.addRole("USER");
        log.info("COMPLETE | 사용자 역할 부여 At " + LocalDateTime.now() + " | " + user);
        return user;
    }

    /**
     * 사용자 조회 |
     * User를 조회 한다. 조회가 되지 않은 User일 경우 401(Unauthorized)를 던진다.
     */
    public UserDefaultResponseDto findOneUser(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UnauthorizedException("유저 정보가 존재하지 않습니다");
        } else {
            return new UserDefaultResponseDto(user.get());
        }
    }

    /**
     * User 이메일로 조회
     * User를 이메일로 조회하여 해당 이메일에 아이디 정보를 보낸다. 조회가 되지 않은 User일 경우 401(Unauthorized)를 던진다.
     */
    public void findUserByEmail(String email) {
        log.info("IN PROGRESS | User 이메일로 조회 At " + LocalDateTime.now() + " | " + email);
        Contact contact = contactService.findOneContact(email);
        userRepository.findByContact(contact.getEmail())
                .ifPresentOrElse((user) -> {
                    emailService.sendEmail(
                            email,
                            "가보자it 아이디 찾기",
                            forgotUsernameEmailContent(user.getUsername(), user.getLegalName())
                    );
                    log.info("COMPLETE | User 이메일로 조회 At " + LocalDateTime.now() + " | " + user);
                }, () -> {
                    throw new UnauthorizedException("유저 정보가 존재하지 않습니다");
                });
    }

    /**
     * User 전체 삭제 |
     * 배포 단계에서 삭제
     */
    public void deleteAll() {
        try {
            log.info("IN PROGRESS | User 전체 삭제 At " + LocalDateTime.now());
            userRepository.deleteAll();
        } catch (Exception e) {
            throw new InternalServerErrorException("User 전체 삭제 중 에러", e);
        }
        log.info("COMPLETE | User 전체 삭제 At " + LocalDateTime.now());
    }

    /**
     * 아이디 찾기 이메일 내용
     */
    private String forgotUsernameEmailContent(String username, String legalName) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <style type=\"text/css\">\n" +
                "        @media screen {\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: normal;\n" +
                "                font-weight: 400;\n" +
                "                src: local('Lato Regular'), local('Lato-Regular'), url(https://fonts.gstatic.com/s/lato/v11/qIIYRU-oROkIk8vfvxw6QvesZW2xOQ-xsNqO47m55DA.woff) format('woff');\n" +
                "            }\n" +
                "\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: normal;\n" +
                "                font-weight: 700;\n" +
                "                src: local('Lato Bold'), local('Lato-Bold'), url(https://fonts.gstatic.com/s/lato/v11/qdgUG4U09HnJwhYI-uK18wLUuEpTyoUstqEm5AMlJo4.woff) format('woff');\n" +
                "            }\n" +
                "\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: italic;\n" +
                "                font-weight: 400;\n" +
                "                src: local('Lato Italic'), local('Lato-Italic'), url(https://fonts.gstatic.com/s/lato/v11/RYyZNoeFgb0l7W3Vu1aSWOvvDin1pK8aKteLpeZ5c0A.woff) format('woff');\n" +
                "            }\n" +
                "\n" +
                "            @font-face {\n" +
                "                font-family: 'Lato';\n" +
                "                font-style: italic;\n" +
                "                font-weight: 700;\n" +
                "                src: local('Lato Bold Italic'), local('Lato-BoldItalic'), url(https://fonts.gstatic.com/s/lato/v11/HkF_qI1x_noxlxhrhMQYELO3LdcAZYWl9Si6vvxL-qU.woff) format('woff');\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        /* CLIENT-SPECIFIC STYLES */\n" +
                "        body,\n" +
                "        table,\n" +
                "        td,\n" +
                "        a {\n" +
                "            -webkit-text-size-adjust: 100%;\n" +
                "            -ms-text-size-adjust: 100%;\n" +
                "        }\n" +
                "\n" +
                "        table,\n" +
                "        td {\n" +
                "            mso-table-lspace: 0pt;\n" +
                "            mso-table-rspace: 0pt;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            -ms-interpolation-mode: bicubic;\n" +
                "        }\n" +
                "\n" +
                "        /* RESET STYLES */\n" +
                "        img {\n" +
                "            border: 0;\n" +
                "            height: auto;\n" +
                "            line-height: 100%;\n" +
                "            outline: none;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            border-collapse: collapse !important;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            height: 100% !important;\n" +
                "            margin: 0 !important;\n" +
                "            padding: 0 !important;\n" +
                "            width: 100% !important;\n" +
                "        }\n" +
                "\n" +
                "        /* iOS BLUE LINKS */\n" +
                "        a[x-apple-data-detectors] {\n" +
                "            color: inherit !important;\n" +
                "            text-decoration: none !important;\n" +
                "            font-size: inherit !important;\n" +
                "            font-family: inherit !important;\n" +
                "            font-weight: inherit !important;\n" +
                "            line-height: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        /* MOBILE STYLES */\n" +
                "        @media screen and (max-width:600px) {\n" +
                "            h1 {\n" +
                "                font-size: 32px !important;\n" +
                "                line-height: 32px !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        /* ANDROID CENTER FIX */\n" +
                "        div[style*=\"margin: 16px 0;\"] {\n" +
                "            margin: 0 !important;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"background-color: #f4f4f4; margin: 0 !important; padding: 0 !important;\">\n" +
                "    <!-- HIDDEN PREHEADER TEXT -->\n" +
                "    <div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: 'Lato', Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\"> 가보자잇 인증번호입니다.\n" +
                "    </div>\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "        <!-- LOGO -->\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#1CDF71\" align=\"center\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 40px 10px;\"> </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#1CDF71\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 4px; line-height: 48px;\">\n" +
                "                            <img src=\" https://gabojait-bucket.s3.ap-northeast-2.amazonaws.com/admin/gabojait-logo.png\" width=\"222\" height=\"70\" style=\"display: block; border: 0px;\" />\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#1CDF71\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px 40px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                            <p style=\"margin: 0;\">" + legalName + "님 안녕하세요!🙇🏻<br>해당 이메일에 가입된 아이디 정보입니다.</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"left\">\n" +
                "                            <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                                <tr>\n" +
                "                                    <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding: 20px 30px 60px 30px;\">\n" +
                "                                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                                            <tr>\n" +
                "                                                <td align=\"center\" style=\"border-radius: 3px;\" bgcolor=\"#444444\"><p style=\"font-size: 20px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; color: #ffffff; text-decoration: none; padding: 15px 25px; border-radius: 2px; border: 1px solid #444444; display: inline-block;\">" + username + "</p></td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr> <!-- COPY -->\n" +
                "                    <tr>\n" +
                "                        <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 0px 30px 40px 30px; border-radius: 0px 0px 4px 4px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                            <p style=\"margin: 0;\">팀 가보자it 드림</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td bgcolor=\"#1CDF71\" align=\"center\" style=\"padding: 30px 10px 0px 10px;\">\n" +
                "            </td>\n" +
                "        </tr>" +
                "    </table>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }
}
