package com.gabojait.gabojaitspring.auth;

import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.domain.user.UserRole;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserRole> userRoles = userRoleRepository.findAll(username);

        User user = userRoles.stream()
                .findFirst()
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND))
                .getUser();
        List<GrantedAuthority> grantedAuthorities = userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                grantedAuthorities);
    }
}
