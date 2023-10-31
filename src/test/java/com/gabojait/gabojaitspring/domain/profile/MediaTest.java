package com.gabojait.gabojaitspring.domain.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    @Test
    @DisplayName("링크 텍스트를 반환한다.")
    void givenLink_whenGetText_thenReturn() {
        // given
        Media media = Media.LINK;

        // when
        String text = media.getText();

        // then
        assertThat(text).isEqualTo("링크");
    }

    @Test
    @DisplayName("파일 텍스트를 반환한다.")
    void givenFile_whenGetText_thenReturn() {
        // given
        Media media = Media.FILE;

        // when
        String text = media.getText();

        // then
        assertThat(text).isEqualTo("파일");
    }

    @Test
    @DisplayName("전체 미디어를 반환한다.")
    void values() {
        // given & when
        Media[] medias = Media.values();

        // then
        assertThat(medias).containsExactlyInAnyOrder(Media.LINK, Media.FILE);
    }

    @Test
    @DisplayName("값 링크를 미디어로 변환한다.")
    void givenLink_whenValueOf_thenReturn() {
        // given
        String s = "LINK";

        // when
        Media media = Media.valueOf(s);

        // then
        assertThat(media).isEqualTo(Media.LINK);
    }

    @Test
    @DisplayName("값 파일를 미디어로 변환한다.")
    void givenFile_whenValueOf_thenReturn() {
        // given
        String s = "FILE";

        // when
        Media media = Media.valueOf(s);

        // then
        assertThat(media).isEqualTo(Media.FILE);
    }
}