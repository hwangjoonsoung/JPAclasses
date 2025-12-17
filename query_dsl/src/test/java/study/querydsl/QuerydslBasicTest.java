package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Transactional
@SpringBootTest
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory ;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("team A");
        Team teamB = new Team("team B");
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
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("start jpql")
    void startJpql() throws Exception {
        //given
        String username = "member1";

        //when
        String qlString = "select m from Member  m where m.username = :username";
        Member member = em.createQuery(qlString, Member.class).setParameter("username", username).getSingleResult();

        //then
        Assertions.assertThat(member.getUsername()).isEqualTo(username);

    }

    @Test
    @DisplayName("transfer jpql to querydsl ")
    void transferJpqlToQuerydsl() throws Exception {
        //given
        String username = "member12";
        //when
        QMember m = new QMember(username);

        Member findMember = queryFactory.select(m).from(m).where(m.username.eq(username)).fetchOne();

        //then
        Assertions.assertThat(findMember.getUsername()).isEqualTo(username);

    }

    @Test
    @DisplayName("develop querydsl using static import")
    void developQuerydsl() throws Exception {
        //given
        String username = "member1";
        //when
        Member findMember = queryFactory.select(member).from(member).where(member.username.eq(username)).fetchOne();

        //then
        Assertions.assertThat(findMember.getUsername()).isEqualTo(username);

    }

    @Test
    @DisplayName("search")
    void search() throws Exception {
        //given
        //when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();

        //then
        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName("search and param")
    void searchAndParam() throws Exception {
        //given
        //when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),(member.age.eq(10))
                )
                .fetchOne();

        //then
        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    /**
     * where함수에서 and조건의 경우 and함수를 사용하는 방법과 " , "를 사용하는 방법이 있다.
     * 이때 " , " 를 사용하는 것이 더 좋은데 그 이유는 .where(member.username.eq("member1"), member.age.eq(10), null) 일때 자동으로 null을 무시한다.
     * 이는 곧 NPE를 방어할 수 있다.
     **/

    @Test
    @DisplayName("resultFetch")
    void resultFetch() throws Exception {
        List<Member> fetch = queryFactory.selectFrom(member).fetch();
        Member fetchOne = queryFactory.selectFrom(member).fetchOne();
        Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
        QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults();
        results.getTotal();
        results.getOffset();

        queryFactory.selectFrom(member).fetchCount();

    }
    /**
     * total을 가져오기 위해서 query를 2번 날리는데 이때 count query는 id를 count한다
     * select count(m1_0.id) from member m1_0
     **/

    @Test
    @DisplayName("sorting")
    void sorting() throws Exception {
        //given
        /**
         * 회원 정렬순서
         * 1. 나이 내림차순
         * 2, 이름 오름차순
         * 단. 2에서 회원 이름이 없으면 마지막에 출력
         **/
        em.persist(new Member(null,100));
        em.persist(new Member("member5",100));
        em.persist(new Member("member6",100));
        //when
        List<Member> fetch = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        //then
        Member member5 = fetch.get(0);
        Member member6 = fetch.get(1);
        Member memberIsNull = fetch.get(2);

        Assertions.assertThat(member5.getUsername()).isEqualTo("member5");
        Assertions.assertThat(member6.getUsername()).isEqualTo("member6");
        Assertions.assertThat(memberIsNull.getUsername()).isNull();
    }

    @Test
    @DisplayName("paging")
    void paging() throws Exception {
        List<Member> fetch = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        //then
        for (Member fetchMember : fetch) {
            System.out.println(fetchMember);
        }
        Assertions.assertThat(fetch.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("paging all")
    void pagingAll() throws Exception {
        QueryResults<Member> results = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        //then
        Assertions.assertThat(results.getLimit()).isEqualTo(2);
        Assertions.assertThat(results.getResults().size()).isEqualTo(2);
        Assertions.assertThat(results.getOffset()).isEqualTo(1);
        Assertions.assertThat(results.getTotal()).isEqualTo(4);

    }

    @Test
    @DisplayName("aggregation")
    void aggregation() throws Exception {
        //when
        List<Tuple> fetch = queryFactory.select(member.count()
                        , member.age.avg()
                        , member.age.sum()
                        , member.age.max()
                        , member.age.min()
                ).from(member)
                .fetch();
        //then
        Tuple tuple = fetch.get(0);
        System.out.println(tuple.get(member.count()));
        System.out.println(tuple.get(member.age.sum()));
        System.out.println(tuple.get(member.age.max()));
        System.out.println(tuple.get(member.age.min()));
    }

    @Test
    @DisplayName("group by")
    void groupBy() throws Exception {
        //given
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(0);
        //then

        System.out.println(teamA);
        System.out.println(teamB);
    }
}
