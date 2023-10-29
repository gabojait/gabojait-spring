package com.gabojait.gabojaitspring.api.service.develop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DevelopServiceTest {

    @Autowired private DevelopService developService;

    @Test
    @DisplayName("서버명을 조회한다.")
    void givenValid_whenGetServerName_thenReturn() {
        // given & when
        String serverName = developService.getServerName();

        // then
        assertEquals("Gabojait Test", serverName);
    }
}