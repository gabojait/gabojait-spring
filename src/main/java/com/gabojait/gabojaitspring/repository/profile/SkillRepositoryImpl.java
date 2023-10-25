package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.profile.QSkill.skill;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class SkillRepositoryImpl implements SkillCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Skill> findAll(long userId) {
        return queryFactory
                .select(skill)
                .from(skill)
                .leftJoin(skill.user, user)
                .where(skill.user.id.eq(userId))
                .orderBy(skill.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Skill> findAllInFetchUser(List<Long> userIds) {
        return queryFactory
                .select(skill)
                .from(skill)
                .leftJoin(skill.user, user)
                .fetchJoin()
                .where(skill.user.id.in(userIds))
                .orderBy(skill.user.id.desc())
                .orderBy(skill.createdAt.desc())
                .fetch();
    }
}
