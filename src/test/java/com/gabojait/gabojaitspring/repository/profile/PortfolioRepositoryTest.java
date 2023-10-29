package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Media;
import com.gabojait.gabojaitspring.domain.profile.Portfolio;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PortfolioRepositoryTest {

    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("포트폴리오 전체 조회를 한다.")
    void findAll() {
        // given
        User user = createSavedDefaultUser();

        Portfolio portfolio1 = createPortfolio("깃허브1", "github.com/gabojait1", Media.LINK, user);
        Portfolio portfolio2 = createPortfolio("깃허브2", "github.com/gabojait2", Media.FILE, user);
        Portfolio portfolio3 = createPortfolio("깃허브3", "github.com/gabojait3", Media.LINK, user);
        portfolioRepository.saveAll(List.of(portfolio1, portfolio2, portfolio3));

        // when
        List<Portfolio> portfolios = portfolioRepository.findAll(user.getId());

        // then
        assertThat(portfolios)
                .extracting("portfolioName", "portfolioUrl", "media")
                .containsExactly(
                        tuple(portfolio3.getPortfolioName(), portfolio3.getPortfolioUrl(), portfolio3.getMedia()),
                        tuple(portfolio2.getPortfolioName(), portfolio2.getPortfolioUrl(), portfolio2.getMedia()),
                        tuple(portfolio1.getPortfolioName(), portfolio1.getPortfolioUrl(), portfolio1.getMedia())
                );
    }

    private Portfolio createPortfolio(String portfolioName, String portfolioUrl, Media media, User user) {
        return Portfolio.builder()
                .portfolioName(portfolioName)
                .portfolioUrl(portfolioUrl)
                .media(media)
                .user(user)
                .build();
    }

    private User createSavedDefaultUser() {
        Contact contact = Contact.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();
        contact.verified();
        contactRepository.save(contact);

        User user = User.builder()
                .username("tester")
                .password("password1!")
                .nickname("테스터")
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();
        return userRepository.save(user);
    }
}