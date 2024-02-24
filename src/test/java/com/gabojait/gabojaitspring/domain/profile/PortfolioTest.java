package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioTest {

    @Test
    @DisplayName("포트폴리오 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        User user = createDefaultUser("tester", LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String portfolioName = "깃허브";
        String portfolioUrl = "https://github.com/gabojait";
        Media media = Media.LINK;

        // when
        Portfolio portfolio = createPortfolio(portfolioName, portfolioUrl, media, user);

        // then
        assertThat(portfolio)
                .extracting("portfolioName", "portfolioUrl", "media", "user")
                .containsExactly(portfolioName, portfolioUrl, media, user);
    }

    @Test
    @DisplayName("포트폴리오 업데이트가 정상 작동한다")
    void givenValid_whenUpdate_thenReturn() {
        // given
        User user = createDefaultUser("tester", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Portfolio portfolio = createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user);

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

    private static Stream<Arguments> providerEquals() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester", LocalDate.of(1997, 2, 11), now);
        Portfolio portfolio = createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user);

        User user1 = createDefaultUser("tester1", LocalDate.of(1997, 2, 11), now);
        User user2 = createDefaultUser("tester2", LocalDate.of(1997, 2, 11), now);
        Portfolio userPortfolio1 = createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user1);
        Portfolio userPortfolio2 = createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user2);

        return Stream.of(
                Arguments.of(portfolio, portfolio, true),
                Arguments.of(portfolio, new Object(), false),
                Arguments.of(
                        createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user),
                        createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user),
                        true
                ),
                Arguments.of(userPortfolio1, userPortfolio2, false),
                Arguments.of(
                        createPortfolio("깃허브1", "https://github.com/gabojait", Media.LINK, user),
                        createPortfolio("깃허브2", "https://github.com/gabojait", Media.LINK, user),
                        false
                ),
                Arguments.of(
                        createPortfolio("깃허브", "https://github.com/gabojait1", Media.LINK, user),
                        createPortfolio("깃허브", "https://github.com/gabojait2", Media.LINK, user),
                        false
                ),
                Arguments.of(
                        createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user),
                        createPortfolio("깃허브", "https://github.com/gabojait", Media.FILE, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 포트폴리오 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("포트폴리오 객체 비교가 정상 작동한다")
    void givenProvider_whenEquals_thenReturn(Portfolio portfolio, Object object, boolean result) {
        // when & then
        assertThat(portfolio.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester", LocalDate.of(1997, 2, 11), now);

        return Stream.of(
                Arguments.of(
                        createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user),
                        createPortfolio("깃허브", "https://github.com/gabojait", Media.LINK, user),
                        true
                ),
                Arguments.of(
                        createPortfolio("깃허브1", "https://github.com/gabojait", Media.LINK, user),
                        createPortfolio("깃허브2", "https://github.com/gabojait", Media.LINK, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 포트폴리오 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("포트폴리오 해시코드 비교가 정상 작동한다")
    void givenProvider_whenHashCode_thenReturn(Portfolio portfolio1, Portfolio portfolio2, boolean result) {
        // when
        int hashCode1 = portfolio1.hashCode();
        int hashCode2 = portfolio2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Portfolio createPortfolio(String portfolioName, String portfolioUrl, Media media, User user) {
        return Portfolio.builder()
                .portfolioName(portfolioName)
                .portfolioUrl(portfolioUrl)
                .media(media)
                .user(user)
                .build();
    }

    private static User createDefaultUser(String username,
                                          LocalDate birthdate,
                                          LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password("password1!")
                .nickname("테스터")
                .gender(Gender.M)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}