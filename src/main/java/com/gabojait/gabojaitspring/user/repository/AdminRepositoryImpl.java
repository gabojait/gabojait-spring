package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.user.domain.Admin;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.user.domain.QAdmin.admin;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final PageProvider pageProvider;

    @Override
    public Page<Admin> searchUnapprovedOrderByCreatedAt(long id, Pageable pageable) {
        List<Admin> admins = queryFactory
                .selectFrom(admin)
                .where(
                        admin.id.lt(id),
                        admin.isApproved.isNull(),
                        admin.isDeleted.isFalse()
                )
                .orderBy(admin.createdAt.asc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(admin.count())
                .from(admin)
                .where(
                        admin.isApproved.isNull(),
                        admin.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(admins, pageable, count);
    }
}
