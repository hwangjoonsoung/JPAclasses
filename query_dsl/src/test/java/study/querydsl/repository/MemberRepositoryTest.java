package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("spring jpa basic test")
    void basicTest() throws Exception {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        //when
        Member findMember = memberRepository.findById(member.getId()).get();
        List<Member> all = memberRepository.findAll();
        List<Member> findByUsernameMember = memberRepository.findByUsername(member.getUsername());

        //then
        Assertions.assertThat(findMember).isEqualTo(member);
        Assertions.assertThat(all).contains(member);
        Assertions.assertThat(findByUsernameMember).contains(member);

    }

    @Test
    @DisplayName("spring jpa with query dsl using custom class basic test")
    void springJpaWithQueryDsl() throws Exception {
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

        List<MemberTeamDto> memberTeamDtos = memberRepository.search(condition);
        //then

        Assertions.assertThat(memberTeamDtos).extracting("username").containsExactly(member3.getUsername(),member4.getUsername());

    }

    @Test
    @DisplayName("page simple")
    void springJpaWithQueryDslPageingSimple() throws Exception {
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
        PageRequest pageRequest = PageRequest.of(0, 3);
        condition.setTeamName("teamB");
        condition.setAgeLoe(41);
        condition.setAgeGoe(10);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);
        //then
        Assertions.assertThat(result.getSize()).isEqualTo(3);
        Assertions.assertThat(result.getContent()).extracting("username").containsExactly("member3", "member4");

    }

    @Test
    @DisplayName("page complex")
    void springJpaWithQueryDslPagingComplex() throws Exception {
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
        PageRequest pageRequest = PageRequest.of(0, 3);
        condition.setTeamName("teamB");
        condition.setAgeLoe(41);
        condition.setAgeGoe(10);

        Page<MemberTeamDto> result = memberRepository.searchPageComplex(condition, pageRequest);
        //then
        Assertions.assertThat(result.getSize()).isEqualTo(3);
        Assertions.assertThat(result.getContent()).extracting("username").containsExactly("member3", "member4");

    }

    @Test
    @DisplayName("querydsl pridicateExcutor")
    void queryDslPridicateExcutor() throws Exception {
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
        QMember member = QMember.member;

        Iterable<Member> members = memberRepository.findAll(member.age.between(10, 40).and(member.username.eq("member1")));
        //then
        for (Member o : members) {
            System.out.println(o);
        }
    }

    /**
     * 한계
     * 1. 묵시적 조인은 가능하지만 left join이 불가능 하다.
     * 2. 클라이언트 코드가 query dsl에 의존한다.
     * 2-1. service layer에서 repository를 불러서 데이터를 가져올때 어떤 dto나 검색 조건 파라미터를 넘기는 것이 아닌, Predicate를 만들어 넘긴다.
     * 2-2. 이는 추후에 데이터를 가져오는 조건 자체를 수정하는 경우 repository layer를 수정하는 것이 아닌 service layer를 수정을 야기한다. (논리적으로 이상하다.)
     **/

}