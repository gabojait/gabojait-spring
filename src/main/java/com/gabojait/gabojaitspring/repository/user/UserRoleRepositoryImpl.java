package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.user.QUser.user;
import static com.gabojait.gabojaitspring.domain.user.QUserRole.userRole;

@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserRole> findAll(String username) {
        return queryFactory
                .selectFrom(userRole)
                .leftJoin(userRole.user, user)
                .fetchJoin()
                .where(
                        userRole.user.username.eq(username)
                ).fetch();
    }
}
