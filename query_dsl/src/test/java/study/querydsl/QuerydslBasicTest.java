package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.*;
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
        em.flush();
        em.clear();
    }

    @AfterEach
    public void afterEach(){
        queryFactory.delete(member).execute();
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
        assertThat(member.getUsername()).isEqualTo(username);

    }

    @Test
    @DisplayName("transfer jpql to querydsl")
    void transferJpqlToQuerydsl() throws Exception {
        //given
        String username = "member1";
        //when
        QMember m = new QMember(username);

        Member findMember = queryFactory.select(m).from(m).where(m.username.eq(username)).fetchOne();

        //then
        assertThat(findMember.getUsername()).isEqualTo(username);

    }

    @Test
    @DisplayName("develop querydsl using static import")
    void developQuerydsl() throws Exception {
        //given
        String username = "member1";
        //when
        Member findMember = queryFactory.select(member).from(member).where(member.username.eq(username)).fetchOne();

        //then
        assertThat(findMember.getUsername()).isEqualTo(username);

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
        assertThat(findMember.getUsername()).isEqualTo("member1");
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
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    /**
     * where함수에서 and조건의 경우 and함수를 사용하는 방법과 " , "를 사용하는 방법이 있다.
     * 이때 " , " 를 사용하는 것이 더 좋은데 그 이유는 .where(member.username.eq("member1"), member.age.eq(10), null) 일때 자동으로 null을 무시한다.
     * 이는 곧 NPE를 방어할 수 있다.
     **/

    @Test
    @DisplayName("resultFetch")
    void resultFetch() throws Exception {
//        List<Member> fetch = queryFactory.selectFrom(member).fetch();
//        Member fetchOne = queryFactory.selectFrom(member).fetchOne();
//        Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
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

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberIsNull.getUsername()).isNull();
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
        assertThat(fetch.size()).isEqualTo(2);

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
        assertThat(results.getLimit()).isEqualTo(2);
        assertThat(results.getResults().size()).isEqualTo(2);
        assertThat(results.getOffset()).isEqualTo(1);
        assertThat(results.getTotal()).isEqualTo(4);

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
    
    @Test
    @DisplayName("join")
    void join() throws Exception {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(fetch).extracting("username").containsExactly("member1", "member2");

    }

    /**
     * 세타조인
     * 회원의 이름이 팀 이름과 같은 회원 조회
     **/
    @Test
    @DisplayName("theta join")
    void thetaJoin() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        //when
        List<Member> result = queryFactory.select(member).from(member, team).where(member.username.eq(team.name)).fetch();
        //then
        assertThat(result).extracting("username").containsExactly("teamA", "teamB");
    }

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조화
     * 여기서 inner join이면 where과 on 조건을 주면 결과는 같지만, outer join이면 where과 on에 조건을 주는 것이 결과가 다르다.
     **/
    @Test
    @DisplayName("join on")
    void joinOn() throws Exception {
        //when
        List<Tuple> fetch = queryFactory.select(member, team).from(member).leftJoin(member.team, team).on(team.name.eq("teamA")).fetch();
        //then
        for (Tuple tuple : fetch) {
            System.out.println(tuple);
        }
    }

    /**
     * 연관관계가 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     **/
    @Test
    @DisplayName("join on no relation")
    void joinOnNoRelation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        //when
        List<Tuple> result = queryFactory
                .select(member,team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name)) // on 조건에 일치하면 team을 데이터를 가져온다.
                .fetch();

        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
    }
    /**
     * left join에서 table을 추가 하면 id를 통해 matching을 하고 그렇지 않으면 세타조인으로 간다.
     **/

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    @DisplayName("fetch join no")
    void fetchJoinNotUse() throws Exception {
        em.flush();
        em.clear();
        //when
        Member member1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")).fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(member1.getTeam());

        assertThat(loaded).isFalse();

    }

    @Test
    @DisplayName("fetch join use")
    void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();
        //when
        Member member1 = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1")).fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(member1.getTeam());

        assertThat(loaded).isTrue();

    }

    /**
     * 나이가 가장 많은 회원을 조회
     **/
    @Test
    @DisplayName("subquery")
    void subquery() throws Exception {
        // 같은 alias를 사용할 수 없기 때문에 새로 생성
        QMember memberSub = new QMember("memberSub");
        //when
        List<Member> fetch = queryFactory
                .selectFrom(member).where(member.age.eq(select(memberSub.age.max()).from(memberSub))).fetch();
        //then
        assertThat(fetch).extracting("age").containsExactly(40);
    }

    /**
     * 나이가 평균 보다 큰 회원을 조회
     **/
    @Test
    @DisplayName("subqueryGoe")
    void subqueryGoe() throws Exception {
        // 같은 alias를 사용할 수 없기 때문에 새로 생성
        QMember memberSub = new QMember("memberSub");
        //when
        List<Member> fetch = queryFactory
                .selectFrom(member).where(member.age.goe(select(memberSub.age.avg()).from(memberSub))).fetch();
        //then
        assertThat(fetch).extracting("age").containsExactly(30,40);
    }

    @Test
    @DisplayName("subqueryIn")
    void subqueryIn() throws Exception {
        // 같은 alias를 사용할 수 없기 때문에 새로 생성
        QMember memberSub = new QMember("memberSub");
        //when
        List<Member> fetch = queryFactory
                .selectFrom(member).where(member.age.in(select(memberSub.age).from(memberSub).where(memberSub.age.gt(10)))).fetch();
        //then
        assertThat(fetch).extracting("age").containsExactly(20,30,40);
    }

    @Test
    @DisplayName("select subquery")
    void selectSubquery() throws Exception {
        // 같은 alias를 사용할 수 없기 때문에 새로 생성
        QMember memberSub = new QMember("memberSub");
        //when
        List<Tuple> fetch = queryFactory.select(member.username, select(memberSub.age.avg()).from(memberSub)).from(member).fetch();
        //then
        for (Tuple tuple : fetch) {
            System.out.println(tuple);
        }
    }
    /**
     * jpa jpql의 한계
     * from절에서 subquery를 사용할 수 없다.
     *
     * 해결방법
     * 1. subquery를 join으로 변경해서 사용
     * 2. 애플레이케션에서 쿼리를 2번으로 나눠서 실행
     * 3. native sql을 사용
     **/

    /**
     * case문
     **/
    @Test
    @DisplayName("case")
    void basicCase() throws Exception {
        //when
        List<String> fetch = queryFactory.select(member.age.when(10).then("열살").when(20).then("스무살").otherwise("기타")).from(member).fetch();

        //then
        System.out.println(fetch);
    }
    
    @Test
    @DisplayName("complexCase")
    void complexCase() throws Exception {
        //when
        List<String> fetch = queryFactory.select(new CaseBuilder().when(member.age.between(0, 20)).then("0~20살").when(member.age.between(21, 30)).then("21~30살").otherwise("기타")).from(member).fetch();
        //then
        System.out.println(fetch);
    }

    /**
     * 상수, 문자 더하기
     **/
    @Test
    @DisplayName("contant")
    void contant() throws Exception {
        //when
        List<Tuple> fetch = queryFactory.select(member.username, Expressions.constant("A")).from(member).fetch();
        //then
        for (Tuple tuple : fetch) {
            System.out.println(tuple);
        }
    }
    @Test
    @DisplayName("concat")
    void concat() throws Exception {
        //when
        List<String> fetch = queryFactory.select(member.username.concat("_").concat(member.age.stringValue())).from(member).fetch();
        //then
        for (String tuple : fetch) {
            System.out.println(tuple);
        }
    }
    /**
     * stringValue를 사용하면 string으로 return가능
     **/

    @Test
    @DisplayName("projection one target")
    void projectionOneTarget() throws Exception {
        //when
        List<String> username = queryFactory.select(member.username).from(member).fetch();
        List<Member> fetch = queryFactory.select(member).from(member).fetch();
        //then
        System.out.println(username);
        System.out.println(fetch);
    }

    @Test
    @DisplayName("tuple projection")
    void tupleProjection() throws Exception {
        //when
        List<Tuple> fetch = queryFactory.select(member.username,member.age).from(member).fetch();

        //then
        for (Tuple tuple : fetch) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println(username);
            System.out.println(age);
        }
    }
    /**
     * repository layer에서만 사용할 수 있도록 한다. 다른 layer가 jpa나 query dsl를 아는건 좋은 설계가 아님
     * 따라서 service로 던져야 하는 경우 dto로 변환해서 던지는 것을 추천한다.
     **/

    @Test
    @DisplayName("projection result convert dto using property")
    void projectionResultConvertDtoUsingProperty() throws Exception {
        //when
        List<MemberDto> fetch = queryFactory.select(Projections.bean(MemberDto.class, member.username, member.age))
                .from(member).fetch();

        //then
        System.out.println("fetch = " + fetch);
    }

    @Test
    @DisplayName("projection result convert dto using filed")
    void projectionResultConvertDtoUsingFiled() throws Exception {
        //when
        List<MemberDto> fetch = queryFactory.select(Projections.fields(MemberDto.class, member.username, member.age))
                .from(member).fetch();

        //then
        System.out.println("fetch = " + fetch);
    }
    /**
     * filed를 사용하면 setter를 통해서 바로 입력된다.
     **/

    @Test
    @DisplayName("projection result convert dto using constructor")
    void projectionResultConvertDtoUsingConstructor() throws Exception {
        //when
        List<UserDto> fetch = queryFactory.select(Projections.constructor(UserDto.class, member.username, member.age))
                .from(member).fetch();

        //then
        System.out.println("fetch = " + fetch);
    }

    @Test
    @DisplayName("projection result convert dto using filed user")
    void projectionResultConvertDtoUsingFiledUser() throws Exception {
        //when
        List<UserDto> fetch = queryFactory.select(Projections.fields(UserDto.class, member.username.as("name"), member.age))
                .from(member).fetch();

        //then
        System.out.println("fetch = " + fetch);
    }
    /**
     * filed 변수가 일치하지 않는 경우 초기화로 입력된다.
     * .as()를 통해 변수 매핑가능함
     **/


    @Test
    @DisplayName("projection result convert dto using filed user expression")
    void projectionResultConvertDtoUsingFiledUserExpression() throws Exception {
        //when
        QMember memberSub = new QMember("memberSub");
        List<UserDto> fetch = queryFactory
                .select(Projections
                        .fields(UserDto.class, member.username.as("name"),
                                ExpressionUtils.as(JPAExpressions.select(memberSub.age.max()).from(memberSub),"age")))
                .from(member).fetch();

        //then
        System.out.println("fetch = " + fetch);
    }


    @Test
    @DisplayName("findDtoByQueryProjection")
    void fundDtoQueryProjection() throws Exception {
        List<MemberDto> fetch = queryFactory.select(new QMemberDto(member.username, member.age)).from(member).fetch();
        //then
        for (MemberDto memberDto : fetch) {
            System.out.println(memberDto);
        }
    }
    /**
     * dto를 Q file로 compile하는 방법
     * dto에 @QueryProjection을 생성자 위에 적용하면 된다 MemberDto.class 참고)
     * 사용하면 생성자 기반으로 Q file을 만들어 줌으로 정형화 된 함수에 넣기만 하면 된다.
     *
     * projection constructor를 사용하면 파라미터가 정형화 되어 있지 않기 때문에 uncheckedException을 발생한다.
     * 하지만 @QueryProjection을 이용하면 checkedException을 발생함으로 컴파일 이전에 발견할 수 있다.
     * 문제는 querydsl에 의존성을 가지기 때문에 여러 layer에 사용하기에는 제약조건이 발생할 가능성이 있다.
     **/

    @Test
    @DisplayName("boolean builder")
    void booleanBuilder() throws Exception {
        //when
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result=  searchMember1(usernameParam, ageParam);
        //thenR
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {
        BooleanBuilder builder = new BooleanBuilder();
        if (usernameParam != null) {
            builder.and(member.username.eq(usernameParam));
        }
        if (ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory.select(member).from(member).where(builder).fetch();
    }
    /**
     * booleanBuilder를 통해서 동적쿼리를 생성할 수 있다.
     **/
    
    @Test
    @DisplayName("where multi param")
    void multiParam() throws Exception {
        //when
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result=  searchMember2(usernameParam, ageParam);
        for (Member member1 : result) {
            System.out.println(member1);
        }
        //thenR
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameCondition, Integer ageCondition) {
//        return queryFactory.select(member).from(member).where(usernameEq(usernameCondition),ageEq(ageCondition)).fetch();
        return queryFactory.select(member).from(member).where(allParam(usernameCondition , ageCondition)).fetch();
    }

    private Predicate usernameEq(String usernameCondition) {
        if (usernameCondition != null) {
            return member.username.eq(usernameCondition);
        } else {
            return null;
        }
    }

    private Predicate ageEq(Integer ageCondition) {
        if (ageCondition != null) {
            return member.age.eq(ageCondition);
        } else {
            return null;
        }
    }

    private BooleanExpression usernameEqBooleanExpression(String usernameCondition) {
        if (usernameCondition != null) {
            return member.username.eq(usernameCondition);
        } else {
            return null;
        }
    }

    private BooleanExpression ageEqBooleanExpression(Integer ageCondition) {
        if (ageCondition != null) {
            return member.age.eq(ageCondition);
        } else {
            return null;
        }
    }

    private BooleanExpression allParam(String usernameCondition, Integer ageCondition) {
        return usernameEqBooleanExpression(usernameCondition).and(ageEqBooleanExpression(ageCondition));
    }
    /**
     * where 조건에 null이 입력되면 무시한다
     * 이를 이용해서 where 조건에 들어갈 함수를 만들어 사용할 수 있다
     * 이거를 응욯하면 BooleanExpression을 사용할 수 있는데.
     * 이렇게 되면 쿼리에 대한 가독성이 높아진다.
     **/

    @Test
    @DisplayName("bulk update")
    void bulkUpdate() throws Exception {
        //then
        List<Member> beforeUpdateExecute = queryFactory.select(member).from(member).fetch();
        for (Member fetch1 : beforeUpdateExecute) {
            System.out.println("before: " + fetch1);
        }
        //when
        long count = queryFactory.update(member).set(member.username, "비회원").where(member.age.lt(28)).execute();
        em.flush();
        em.clear();

        //then
        List<Member> afterUpdateExecute = queryFactory.select(member).from(member).fetch();
        for (Member fetch1 : afterUpdateExecute) {
            System.out.println("after: "+fetch1);
        }
    }
    /**
     *  bulk update할때는 persist context를 유의해야 한다.
     *  update이전에 select를 하고 update를 한 뒤 다시 select하면 1차 캐시에서 가져오는 문제가 있다.
     *  따라서 해당 부분은 persist context를 날랴주는 것이 좋다.
     **/

    @Test
    @DisplayName("bulk add")
    void bulkAdd() throws Exception {
        //when
        queryFactory.update(member).set(member.age, member.age.add(-1)).execute();

        //then
    }

    @Test
    @DisplayName("bulk delete")
    void bulkDelete() throws Exception {
        //when
        queryFactory.delete(member).where(member.age.gt(31)).execute();
        //then
    }

    @Test
    @DisplayName("sql Function")
    @Commit
    void sqlFunction() throws Exception {
        //when
        List<String> fetch = queryFactory.select(
                Expressions.stringTemplate("function('replace',{0},{1},{2})",
                        member.username, "member", "m")
        ).from(member).fetch();
        for (String s : fetch) {
            System.out.println(s);
        }
    }

    @Test
    @DisplayName("sql function2")
    void sqlFunction2() throws Exception {
        //when
//        List<String> fetch = queryFactory.select(member.username).from(member).where(member.username.eq(Expressions.stringTemplate("function('lower',{0})", member.username))).fetch();
        List<String> fetch = queryFactory.select(member.username).from(member).where(member.username.eq(member.username.lower())).fetch(); //표준 함수는 내장되어 있음
        //then
        System.out.println(fetch);
    }
}
