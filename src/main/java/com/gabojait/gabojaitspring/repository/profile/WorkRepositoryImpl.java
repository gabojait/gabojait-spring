package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Work;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.profile.QWork.work;

@Repository
@RequiredArgsConstructor
public class WorkRepositoryImpl implements WorkCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Work> findAll(long userId) {
        return queryFactory
                .selectFrom(work)
                .where(work.user.id.eq(userId))
                .orderBy(work.startedAt.desc())
                .fetch();
    }
}
