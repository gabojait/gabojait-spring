package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Education;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.profile.QEducation.education;

@Repository
@RequiredArgsConstructor
public class EducationRepositoryImpl implements EducationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Education> findAll(long userId) {
        return queryFactory
                .selectFrom(education)
                .where(education.user.id.eq(userId))
                .orderBy(education.startedAt.desc())
                .fetch();
    }
}
