package com.gabojait.gabojaitspring.auth;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndIsDeletedIsFalse(username)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                user.getAuthorities());
    }

    public void loadUserByUserId(Long userId) {
        userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(TOKEN_UNAUTHORIZED);
                });
    }

    public User loadAdminByUserId(Long userId) {
        return userRepository.findByIdAndIsDeletedIsNull(userId)
                .orElseThrow(() -> {
                    throw new CustomException(TOKEN_UNAUTHORIZED);
                });
    }
}
