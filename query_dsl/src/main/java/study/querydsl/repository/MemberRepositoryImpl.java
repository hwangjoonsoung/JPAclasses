package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory.select(new QMemberTeamDto(member.id.as("memberId"), member.username , member.age , team.id.as("teamId"),team.name)).from(member).leftJoin(member.team, team).where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
        ).fetch();
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }


    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> memberTeamDtoQueryResults = queryFactory.select(new QMemberTeamDto(member.id.as("memberId"), member.username, member.age, team.id.as("teamId"), team.name)).from(member).leftJoin(member.team, team).where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
        ).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<MemberTeamDto> contents = memberTeamDtoQueryResults.getResults();
        long total = memberTeamDtoQueryResults.getTotal();
        return new PageImpl<>(contents, pageable,total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> contents = queryFactory.select(new QMemberTeamDto(member.id.as("memberId"), member.username, member.age, team.id.as("teamId"), team.name)).from(member).leftJoin(member.team, team).where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
        ).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        long total = queryFactory.select(member).from(member).leftJoin(member.team, team).where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
        ).fetchCount();

        return new PageImpl<>(contents, pageable,total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplexExecutionUtils(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> contents = queryFactory.select(new QMemberTeamDto(member.id.as("memberId"), member.username, member.age, team.id.as("teamId"), team.name)).from(member).leftJoin(member.team, team).where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
        ).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        JPAQuery<Member> countQuery = queryFactory.select(member).from(member).leftJoin(member.team, team).where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
        );

        return PageableExecutionUtils.getPage(contents, pageable, () -> countQuery.fetchCount());
    }

    /**
     * total count를 쿼리를 날릴때 최적화 상태로 날릴 수 있다.
     * 최적화가 된다는 의미는 데이터를 가져왔을 때 해당 데이터가 마지막 페이지의 데이터 인 경우 (ex: page size = 20 total data = 10)
     * 실질적으로 total data는 가져온 데이터의 수량임으로 count query를 날리지 않는다.
     **/
}
