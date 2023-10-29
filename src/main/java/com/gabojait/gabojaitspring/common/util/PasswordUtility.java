package com.gabojait.gabojaitspring.common.util;

import com.gabojait.gabojaitspring.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class PasswordUtility {

    private final PasswordEncoder passwordEncoder;

    /**
     * 랜덤 코드 생성
     */
    public String generateRandomCode(int codeLength) {
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 비밀번호 암호화
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 비밀번호 검증
     */
    public boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
}
