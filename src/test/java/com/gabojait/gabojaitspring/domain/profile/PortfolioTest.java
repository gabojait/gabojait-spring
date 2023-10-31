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
        Portfolio portfolio = createPortfolio("깃허브", "github.com/gabojait", Media.LINK, user);

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

    @Test
    @DisplayName("같은 객체인 포트폴리오를 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio = createPortfolio("깃허브", "github.com/gabojait", Media.LINK, user);

        // when
        boolean result = portfolio.equals(portfolio);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 포트폴리오를 비교하면 동일하다.")
    void givenEqualData_whenEquals_thenReturn() {
        // given
        String portfolioName = "깃허브";
        String portfolioUrl = "github.com/gabojait";
        Media media = Media.LINK;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio1 = createPortfolio(portfolioName, portfolioUrl, media, user);
        Portfolio portfolio2 = createPortfolio(portfolioName, portfolioUrl, media, user);

        // when
        boolean result = portfolio1.equals(portfolio2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 객체인 포트폴리오를 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio = createPortfolio("깃허브", "github.com/gabojait", Media.LINK, user);
        Object object = new Object();

        // when
        boolean result = portfolio.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 회원인 포트폴리오를 비교하면 동일하지 않다.")
    void givenUnequalUser_whenEquals_thenReturn() {
        // given
        String username1 = "tester1";
        String username2 = "tester2";
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username1, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username2, password, nickname, gender, birthdate, now);

        String portfolioName = "깃허브";
        String portfolioUrl = "github.com/gabojait";
        Media media = Media.LINK;
        Portfolio portfolio1 = createPortfolio(portfolioName, portfolioUrl, media, user1);
        Portfolio portfolio2 = createPortfolio(portfolioName, portfolioUrl, media, user2);

        // when
        boolean result = portfolio1.equals(portfolio2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 포트폴리오명인 포트폴리오를 비교하면 동일하지 않다.")
    void givenUnequalPortfolioName_whenEquals_thenReturn() {
        // given
        String portfolioName1 = "깃허브1";
        String portfolioName2 = "깃허브2";

        String portfolioUrl = "github.com/gabojait";
        Media media = Media.LINK;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio1 = createPortfolio(portfolioName1, portfolioUrl, media, user);
        Portfolio portfolio2 = createPortfolio(portfolioName2, portfolioUrl, media, user);

        // when
        boolean result = portfolio1.equals(portfolio2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 포트폴리오 URL인 포트폴리오를 비교하면 동일하지 않다.")
    void givenUnequalPortfolioUrl_whenEquals_thenReturn() {
        // given
        String portfolioUrl1 = "github.com/gabojait1";
        String portfolioUrl2 = "github.com/gabojait2";

        String portfolioName = "깃허브";
        Media media = Media.LINK;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio1 = createPortfolio(portfolioName, portfolioUrl1, media, user);
        Portfolio portfolio2 = createPortfolio(portfolioName, portfolioUrl2, media, user);

        // when
        boolean result = portfolio1.equals(portfolio2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 미디어인 포트폴리오를 비교하면 동일하지 않다.")
    void givenUnequalMedia_whenEquals_thenReturn() {
        // given
        Media media1 = Media.FILE;
        Media media2 = Media.LINK;

        String portfolioName = "깃허브";
        String portfolioUrl = "github.com/gabojait";
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio1 = createPortfolio(portfolioName, portfolioUrl, media1, user);
        Portfolio portfolio2 = createPortfolio(portfolioName, portfolioUrl, media2, user);

        // when
        boolean result = portfolio1.equals(portfolio2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 포트폴리오의 해시코드는 같다.")
    void givenEqual_whenHashCode_thenReturn() {
        // given
        String portfolioName = "깃허브";
        String portfolioUrl = "github.com/gabojait";
        Media media = Media.LINK;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio1 = createPortfolio(portfolioName, portfolioUrl, media, user);
        Portfolio portfolio2 = createPortfolio(portfolioName, portfolioUrl, media, user);

        // when
        int hashCode1 = portfolio1.hashCode();
        int hashCode2 = portfolio2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("동일하지 않은 포트폴리오의 해시코드는 다르다.")
    void givenUnequal_whenHashCode_thenReturn() {
        // given
        String portfolioName1 = "깃허브1";
        String portfolioName2 = "깃허브2";

        String portfolioUrl = "github.com/gabojait";
        Media media = Media.LINK;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio1 = createPortfolio(portfolioName1, portfolioUrl, media, user);
        Portfolio portfolio2 = createPortfolio(portfolioName2, portfolioUrl, media, user);

        // when
        int hashCode1 = portfolio1.hashCode();
        int hashCode2 = portfolio2.hashCode();

        // then
        assertThat(hashCode1).isNotEqualTo(hashCode2);
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