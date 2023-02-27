package com.inuappcenter.gabojaitspring.test.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.repository.EducationRepository;
import com.inuappcenter.gabojaitspring.profile.repository.PortfolioRepository;
import com.inuappcenter.gabojaitspring.profile.repository.SkillRepository;
import com.inuappcenter.gabojaitspring.profile.repository.WorkRepository;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.repository.OfferRepository;
import com.inuappcenter.gabojaitspring.team.repository.TeamRepository;
import com.inuappcenter.gabojaitspring.team.service.TeamService;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.domain.type.Gender;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.user.repository.ContactRepository;
import com.inuappcenter.gabojaitspring.user.repository.UserRepository;
import com.inuappcenter.gabojaitspring.user.service.ContactService;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestService {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillRepository skillRepository;
    private final WorkRepository workRepository;
    private final TeamRepository teamRepository;
    private final OfferRepository offerRepository;
    private final UserService userService;
    private final ContactService contactService;
    private final TeamService teamService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 데이터베이스 초기화 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void resetDatabase() {

        try {
            userRepository.deleteAll();
            contactRepository.deleteAll();
            educationRepository.deleteAll();
            portfolioRepository.deleteAll();
            skillRepository.deleteAll();
            workRepository.deleteAll();
            teamRepository.deleteAll();
            offerRepository.deleteAll();

            injectTestAccounts();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }
    }

    @Transactional
    public void injectTestAccounts() {

        for (int i = 1; i <= 3; i++) {
            Contact contact = Contact.builder()
                    .email("test" + i + "@gabojait.com")
                    .verificationCode("000000")
                    .build();

            contact.verified();
            contact.registered();

            contactService.save(contact);

            User user = User.builder()
                    .username("test" + i)
                    .legalName("테스트")
                    .password(passwordEncoder.encode("password"))
                    .gender(Gender.MALE)
                    .birthdate(LocalDate.of(2000, 1, i))
                    .contact(contact)
                    .nickname("테스트" + i)
                    .roles(new ArrayList<>(List.of(Role.USER, Role.ADMIN)))
                    .build();

            if (user.getUsername().equals("test1"))
                user.updatePosition(Position.PM);

            userService.save(user);

            if (user.getUsername().equals("test1")) {
                user.updatePosition(Position.PM);

                Team team = Team.builder()
                        .projectName("Gabojait")
                        .projectDescription("가보자잇 설명입니다.")
                        .leaderUserId(user.getId())
                        .designerTotalRecruitCnt(Short.valueOf("2"))
                        .backendTotalRecruitCnt(Short.valueOf("2"))
                        .frontendTotalRecruitCnt(Short.valueOf("2"))
                        .projectManagerTotalRecruitCnt(Short.valueOf("2"))
                        .expectation("열정적인 태도를 원해요.")
                        .openChatUrl("https://open.kakao.com/o/test")
                        .build();

                teamService.save(team);
                teamService.join(team, user, Position.PM);
            }
        }
    }
}
