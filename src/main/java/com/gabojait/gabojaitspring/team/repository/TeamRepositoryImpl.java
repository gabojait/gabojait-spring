package com.gabojait.gabojaitspring.team.repository;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.team.domain.QTeam.team;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final PageProvider pageProvider;

    @Override
    public Page<Team> searchByIsPositionFullOrderByCreatedAt(long id, Position position, Pageable pageable) {
        Page<Team> teams;

        switch (position) {
            case DESIGNER:
                teams = searchByIsDesignerFullOrderByCreatedAt(id, pageable);
                break;
            case BACKEND:
                teams = searchByIsBackendFullOrderByCreatedAt(id, pageable);
                break;
            case FRONTEND:
                teams = searchByIsFrontendFullOrderByCreatedAt(id, pageable);
                break;
            case MANAGER:
                teams = searchByIsManagerFullOrderByCreatedAt(id, pageable);
                break;
            default:
                teams = searchOrderByCreatedAt(id, pageable);
                break;
        }

        return teams;
    }

    private Page<Team> searchOrderByCreatedAt(long id, Pageable pageable) {
        List<Team> teams = queryFactory
                .selectFrom(team)
                .where(
                        team.id.lt(id),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .orderBy(team.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(team.count())
                .from(team)
                .where(
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(teams, pageable, count);
    }

    private Page<Team> searchByIsDesignerFullOrderByCreatedAt(long id, Pageable pageable) {
        List<Team> teams = queryFactory
                .selectFrom(team)
                .where(
                        team.id.lt(id),
                        team.isDesignerFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .orderBy(team.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(team.count())
                .from(team)
                .where(
                        team.isDesignerFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(teams, pageable, count);
    }

    private Page<Team> searchByIsBackendFullOrderByCreatedAt(long id, Pageable pageable) {
        List<Team> teams = queryFactory
                .selectFrom(team)
                .where(
                        team.id.lt(id),
                        team.isBackendFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .orderBy(team.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(team.count())
                .from(team)
                .where(
                        team.isBackendFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(teams, pageable, count);
    }

    private Page<Team> searchByIsFrontendFullOrderByCreatedAt(long id, Pageable pageable) {
        List<Team> teams = queryFactory
                .selectFrom(team)
                .where(
                        team.id.lt(id),
                        team.isFrontendFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .orderBy(team.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(team.count())
                .from(team)
                .where(
                        team.isFrontendFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(teams, pageable, count);
    }

    private Page<Team> searchByIsManagerFullOrderByCreatedAt(long id, Pageable pageable) {
        List<Team> teams = queryFactory
                .selectFrom(team)
                .where(
                        team.id.lt(id),
                        team.isManagerFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .orderBy(team.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(team.count())
                .from(team)
                .where(
                        team.isManagerFull.isFalse(),
                        team.isRecruiting.isTrue(),
                        team.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(teams, pageable, count);
    }
}
