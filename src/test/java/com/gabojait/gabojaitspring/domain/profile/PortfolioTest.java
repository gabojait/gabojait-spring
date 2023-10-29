package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioTest {

    @Test
    @DisplayName("포트폴리오를 생성한다.")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String portfolioName = "깃허브";
        String portfolioUrl = "github.com/gabojait";
        Media media = Media.LINK;

        // when
        Portfolio portfolio = createPortfolio(portfolioName, portfolioUrl, media, user);

        // then
        assertThat(portfolio)
                .extracting("portfolioName", "portfolioUrl", "media")
                .containsExactly(portfolioName, portfolioUrl, media);
    }

    @Test
    @DisplayName("포트폴리오를 업데이트한다.")
    void update() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio = createPortfolio("깃허브", "github.com/gabojait.com", Media.LINK, user);

        String portfolioName = "노션";
        String portfolioUrl = "nogamsung.notion.io";
        Media media = Media.LINK;

        // when
        portfolio.update(portfolioName, portfolioUrl, media);

        // then
        assertThat(portfolio)
                .extracting("portfolioName", "portfolioUrl", "media")
                .containsExactly(portfolioName, portfolioUrl, media);
    }

    private Portfolio createPortfolio(String portfolioName, String portfolioUrl, Media media, User user) {
        return Portfolio.builder()
                .portfolioName(portfolioName)
                .portfolioUrl(portfolioUrl)
                .media(media)
                .user(user)
                .build();
    }

    private User createDefaultUser(String email,
                                   String verificationCode,
                                   String username,
                                   String password,
                                   String nickname,
                                   Gender gender,
                                   LocalDate birthdate,
                                   LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}