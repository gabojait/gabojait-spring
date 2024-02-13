package com.gabojait.gabojaitspring.api.service.user;

import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.domain.user.UserRole;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserDetailsService {

    private final UserRoleRepository userRoleRepository;

    /**
     * 회원 상세 정보 조회 |
     * @param userId 회원 식별자
     * @return 회원 상세 정보
     */
    public UserDetails findUserDetails(long userId) throws UsernameNotFoundException {
        List<UserRole> userRoles = userRoleRepository.findAll(userId);

        User user = userRoles.stream()
                .findFirst()
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND))
                .getUser();
        List<GrantedAuthority> grantedAuthorities = userRoles.stream()
                .map(userROle -> new SimpleGrantedAuthority(userROle.getRole().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                grantedAuthorities);
    }
}
