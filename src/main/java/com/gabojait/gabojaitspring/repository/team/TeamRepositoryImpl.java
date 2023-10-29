package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.team.QTeam.team;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Team> findPage(Position position, long pageFrom, int pageSize) {
        Long count = queryFactory.select(team.count())
                .from(team)
                .where(
                        positionEq(position),
                        team.isRecruiting.isTrue(),
                        team.completedAt.isNull(),
                        team.isDeleted.isFalse()
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        List<Team> teams = queryFactory.selectFrom(team)
                .where(
                        team.id.lt(pageFrom),
                        positionEq(position),
                        team.isRecruiting.isTrue(),
                        team.completedAt.isNull(),
                        team.isDeleted.isFalse()
                ).orderBy(team.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(teams, pageable, count);
    }

    private Predicate positionEq(Position position) {
        switch (position) {
            case DESIGNER:
                return team.designerMaxCnt.gt(team.designerCurrentCnt);
            case BACKEND:
                return team.backendMaxCnt.gt(team.backendCurrentCnt);
            case FRONTEND:
                return team.frontendMaxCnt.gt(team.frontendCurrentCnt);
            case MANAGER:
                return team.managerMaxCnt.gt(team.managerCurrentCnt);
            default:
                return null;
        }
    }
}
