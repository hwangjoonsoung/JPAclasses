package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("basic test")
    void basicTest() throws Exception {
        //given
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);
        //when
        Member findMember = memberJpaRepository.findById(member.getId()).get();
        List<Member> all = memberJpaRepository.findAll();
        List<Member> findByUsernameMember = memberJpaRepository.findByUsername(member.getUsername());

        //then
        Assertions.assertThat(findMember).isEqualTo(member);
        Assertions.assertThat(all).contains(member);
        Assertions.assertThat(findByUsernameMember).contains(member);

    }

    @Test
    @DisplayName("basic test querydsl")
    void basicTestQuerydsl() throws Exception {
        //given
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);
        //when
        Member findMember = memberJpaRepository.findById(member.getId()).get();
        List<Member> all = memberJpaRepository.findAllQuerydsl();
        List<Member> findByUsernameMember = memberJpaRepository.findByUsernameQueryDsl(member.getUsername());

        //then
        Assertions.assertThat(findMember).isEqualTo(member);
        Assertions.assertThat(all).contains(member);
        Assertions.assertThat(findByUsernameMember).contains(member);

    }

    @Test
    @DisplayName("search test by BooleanBuilder")
    void searchTestByBooleanBuilder() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        //when
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setTeamName("teamB");
        condition.setAgeLoe(30);
        condition.setAgeGoe(10);

        List<MemberTeamDto> memberTeamDtos = memberJpaRepository.searchByBooleanBuilder(condition);
        //then

        Assertions.assertThat(memberTeamDtos).extracting("username").containsExactly("member3",member4.getUsername());
    }

    @Test
    @DisplayName("search test BooleanExpression")
    void searchTestByBooleanExpression() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        //when
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setTeamName("teamB");
        condition.setAgeLoe(41);
        condition.setAgeGoe(10);

        List<MemberTeamDto> memberTeamDtos = memberJpaRepository.searchByBooleanExpression(condition);
        //then

        Assertions.assertThat(memberTeamDtos).extracting("username").containsExactly(member3.getUsername(),member4.getUsername());
    }

}