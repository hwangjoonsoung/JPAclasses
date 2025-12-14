package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.MemberProjection;
import study.datajpa.dto.UserNameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.nio.channels.Pipe;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    @Autowired
    EntityManager em;
    @Autowired
    private LazyInitializationExcludeFilter eagerJpaMetamodelCacheCleanup;

    @Test
    public void testMember(){
        Member member = new Member("MemberB");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        org.assertj.core.api.Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        org.assertj.core.api.Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        org.assertj.core.api.Assertions.assertThat(member).isEqualTo(savedMember);
        org.assertj.core.api.Assertions.assertThat(savedMember).isEqualTo(findMember);
    }


    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        org.assertj.core.api.Assertions.assertThat(member1).isEqualTo(findMember1);
        org.assertj.core.api.Assertions.assertThat(member2).isEqualTo(findMember2);

        long count = memberRepository.count();
        org.assertj.core.api.Assertions.assertThat(count).isEqualTo(2);

        List<Member> members = memberRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(members.size()).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        members = memberRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(members.size()).isEqualTo(0);

    }

    @Test
    public void fidByUserNameAndAgeGreaterThen() {
        Member member1 = new Member("member",20);
        Member member2 = new Member("member",10);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> member = memberRepository.findByUserNameAndAgeGreaterThan("member", 15);
        Assertions.assertThat(member.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("member",20);
        Member member2 = new Member("member",10);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> member = memberRepository.findUser("member", 15);
        Assertions.assertThat(member.size()).isEqualTo(1);
    }

    @Test
    public void findUserNamaList() {
        Member member1 = new Member("member1",20);
        Member member2 = new Member("member2",10);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();
        System.out.println(usernameList);
    }

    @Test
    public void findMemberDto() {
        Member member1 = new Member("member1",20);

        memberRepository.save(member1);

        Team team1 = new Team("team A");
        member1.setTeam(team1);
        teamRepository.save(team1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println(dto);
        }
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("member1",20);
        Member member2 = new Member("member2",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("member1","member2"));
        for (Member byName : byNames) {
            System.out.println("username = " + byName);
        }

    }

    @Test
    public void findReturnType() {
        Member member1 = new Member("member1",20);
        Member member2 = new Member("member2",20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> foundMember1 = memberRepository.findListByUserName("member1");
        Member foundMember2 = memberRepository.findMemberByUserName("member1");
        Optional<Member> foundMember3 = memberRepository.findOptionalMemberByUserName("member1");

        System.out.println(foundMember1);
        System.out.println(foundMember2);
        System.out.println(foundMember3);

        foundMember1 = memberRepository.findListByUserName("member12");
        foundMember2 = memberRepository.findMemberByUserName("member12");
        foundMember3 = memberRepository.findOptionalMemberByUserName("member12");

        System.out.println(foundMember1); // empty
        System.out.println(foundMember2); // null
        System.out.println(foundMember3); // empty
        /**
         * 별도의 작업을 하지 않아도 return형식을 지정할 수 있다.
         * jpa의 경우 NoResultException을 발생시키지만 spring jpa는 반환값이 항상 있다.
         **/

    }

    @Test
    public void pageingWithPage() {
        Member member1 = new Member("member1",10);
        Member member2 = new Member("member2",20);
        Member member3 = new Member("member4",10);
        Member member4 = new Member("member5",40);
        Member member5 = new Member("member6",10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // entity to dto
        Page<MemberDto> dtoPage = page.map(member -> new MemberDto(member.getId(), member.getUserName(), null));

        /**
         * count query별도로 작성 가능
         **/
        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println(member);
        }
        Assertions.assertThat(content.size()).isEqualTo(3);

    }

    @Test
    public void pageingWithSlice() {
        Member member1 = new Member("member1",10);
        Member member2 = new Member("member2",10);
        Member member3 = new Member("member4",10);
        Member member4 = new Member("member5",10);
        Member member5 = new Member("member6",10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println(member);
        }
        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.hasNext()).isTrue();
        Assertions.assertThat(page.isFirst()).isTrue();
    }


    @Test
    public void bulkUpdateAge() {
        Member member1 = new Member("member1",10);
        Member member2 = new Member("member2",20);
        Member member3 = new Member("member3",30);
        Member member4 = new Member("member4",10);
        Member member5 = new Member("member5",20);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        int resultCount = memberRepository.bulkAgePlus(15);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("member5"));
        Member member = byNames.get(0);

        Assertions.assertThat(resultCount).isEqualTo(3);

        System.out.println(member);
        /**
         * bulk연산을 날리면 persistence context에는 반영이 안된다.
         * 만약 member5의 age를 가져오면 20으로 남아 았을것이다.
         * 따라서 bulk 연산을 날리면 persistence context를 날려야 한다. flush/clear
         * 또는 @Modifying 에서auto flush/claer설정을 한다
         **/

    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println(member.getUserName());
            System.out.println(member.getTeam().getTeamName());
        }

        //then
    }
    /**
     * fetch join을 할 때는 jpql을 사용해야 하는데. 약간 귀찮음.
     * 이를 해결하기 위해서 entity graph를 사용한다.
     * @EntityGraph(attributePaths = "team")
     **/

    @Test
    public void queryHint() {
        //given
        Member save = memberRepository.save(new Member("member1", 10));
        em.flush(); //insert query 발생
        em.clear(); //persistence context 비우기

        //when
        Member member = memberRepository.findById(save.getId()).get(); // select query 발생
        member.setUserName("member2");
        em.flush(); // update query 나감

        //then

    }

    @Test
    public void queryHint2() {
        //given
        Member save = memberRepository.save(new Member("member1", 10));
        em.flush(); //insert query 발생
        em.clear(); //persistence context 비우기

        //when
        Member member = memberRepository.findById(save.getId()).get(); // select query 발생
        member.setUserName("member2");
        em.flush(); // update query 나감

    }

    /**
     * 변경감지 때문에 2개의 객체를 생성하는데 (변경 전 객체, 변경 후 객체)
     * 따라서 변경감지가 필요 없는경우(readonly의 조건)에서 적용한다.
     * @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly" , value = "true"))
     * 근데 여기서 이제 문제는 read only를 조건을 안준다고 해도 GC로 인해서 쉽게 해결이 가능하다는것.
     * */
    
    @Test
    @DisplayName("lock test")
    void locktest() throws Exception {
        //given
        Member save = memberRepository.save(new Member("member1", 10));
        em.flush(); //insert query 발생
        em.clear(); //persistence context 비우기

        //when
        Member member = memberRepository.findLockByUserName("member1").get(0);
        //then
    }

    @Test
    @DisplayName("custom repository")
    void customRepository() throws Exception {
        //given
        List<Member> memberCustom = memberRepository.findMemberCustom();
        System.out.println(memberCustom);

        //when
    
        //then
    }
    /**
     * spring data jpa를 사용하는 도중에 특정 함수는 내가 직접 custom하는 방법
     * 주로: query dsl 적용
     * 규직: 구현체 class를 생성할 때 spring data jpa의 interface name의 Impl을 붙여서 생성한다.
     * 1. interface 생성 (MemberRepositoryCustom)
     * 2. 구현체 class 생성 (MemberRepositoryImpl) 이때 class name은 spring data jpa의 interface이름 + Impl로 작성한다.
     * 3. spring data jpa를 사용하는 interface에서 생성한 interface 상속 (MemberRepository)
     **/

    /**
     * 여기서 이제 생각을 해봐야 하는데.
     * 이 custom하는 기능이 정말 필요할까? 라는 생각을 해야봐야한다.
     * 저렇게 사용하면 interface, 구현체 class를 생성해야 한다. 이는 관리해야 하는 파일이 늘어남을 의미한다.
     * 이 방법 보다는 entity manager를 사용하는 class를 생성하여 사용하는 것이 더 나은 선택일 수 있다.
     **/

    @Test
    @DisplayName("Jpa event base entity")
    void jpaEventBaseEntity() throws Exception {

        //given
        Member member = memberRepository.save(new Member("member1", 10));
        em.flush(); //insert query 발생
        em.clear(); //persistence context 비우기

        //when
        Member findMember = memberRepository.findByNames(Arrays.asList("member1")).get(0);
        System.out.println("insert member's created date: "+ findMember.getCreatedDate());
        System.out.println("insert member's updated date: "+ findMember.getUpdatedDate());
        System.out.println("insert member's created by: "+ findMember.getCreatedBy());
        System.out.println("insert member's updated by: "+ findMember.getUpdatedBy());
        findMember.setUserName("member2");
        em.flush();
        Member updatedMemberFind = memberRepository.findByNames(Arrays.asList("member2")).get(0);
        Thread.sleep(1000);
        System.out.println("update member's created date: "+updatedMemberFind.getCreatedDate());
        System.out.println("update member's updated date: "+updatedMemberFind.getUpdatedDate());
        System.out.println("update member's created by: "+ updatedMemberFind.getCreatedBy());
        System.out.println("update member's updated by: "+ updatedMemberFind.getUpdatedBy());
        //then
    }

    /**
     * entity를 생성할때 공통적인 column이 있는데 created date, updated date, create by, updated by가 있다.
     * 이거는 일반적으로 시스템에서 자동적으로 입력과 수정이 이뤄져야 하는데 데이터가 생성이나 수정될 때마다 입력하는건 너무 불편하다
     * 때문에 entity에 BaseEntity를 상속받는 것으로 entity table에 common column (@MappedSuperclass)을 적용할 수 있다.
     * jpa가 자동적으로 데이터를 insert, update하기 위해서는 다음과 같은 작업이 필요하다
     * case 1: 날짜의 경우
     * 1. @PrePersist,@PreUpdate를 통해 날짜 update
     * case 2: 이름의 경우
     * 1. @SpringBootStarter에 @EnableJpaAuditing 추가
     * 2. AuditorAware<string>을 return하는 환경설정을 통해 작업 (getCurrentAuditor() 참고)-> 이는 자동으로 jpa가 입력하기 위함
     * 3. BaseEntity에 @EntityListeners(AuditingEntityListener.class) 추가
     **/

    @Test
    @DisplayName("query by example")
    void queryByExample() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);
        em.persist(member1);
        em.persist(member2);
        em.flush();
        em.clear();

        //when
        //Probe
        Member exampleMember = new Member("member1");

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase("age");
        Example<Member> memberExample = Example.of(exampleMember,matcher);

        List<Member> memberList = memberRepository.findAll(memberExample);
        //then
        Assertions.assertThat(memberList.get(0).getUserName()).isEqualTo(member1.getUserName());

    }

    /**
     * 장점: 동적 쿼리를 편리하게 처리 가능
     * 도메인 객체를 그대로 사용
     * DB를 변경해도 상관없음
     *
     * 단점: inner join만 사용가능
     * 중첨 제약조건은 안됨 (ex: firstName = ? and lastname = ?)
     * 매칭 조건이 매우 단순함.
     **/

    @Test
    @DisplayName("interface based projections")
    void projections() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);
        em.flush();
        em.clear();

        //when
        List<UserNameOnly> interfaceMembers = memberRepository.findInterfaceBasedProjectionsByUserName("member1");
        List<UserNameOnlyDto> classMembers = memberRepository.findClassBasedProjectionsByUserName("member1");
        List<UserNameOnlyDto> classTypeProjectionMember = memberRepository.findClassBasedWithTypeProjectionsByUserName("member1", UserNameOnlyDto.class);
        List<NestedClosedProjections> nestedClosedProjections = memberRepository.findClassBasedWithTypeProjectionsByUserName("member1", NestedClosedProjections.class);

        //then
        for (UserNameOnly userNameOnly : interfaceMembers) {
            System.out.println("interface userNameOnly = " + userNameOnly.getUserName());
        }
        for (UserNameOnlyDto userNameOnly : classMembers) {
            System.out.println("class userNameOnly = " + userNameOnly.getUserName());
        }
        for (UserNameOnlyDto userNameOnly : classTypeProjectionMember) {
            System.out.println("class userNameOnly = " + userNameOnly.getUserName());
        }
        for (NestedClosedProjections nestedClosedProjection : nestedClosedProjections) {
            System.out.println("nested userNameOnly = " + nestedClosedProjection.getUserName());
            System.out.println("nestedClosedProjection = " + nestedClosedProjection.getTeam());
        }
    }
    /**
     * interface 기반의 projection
     * entity대신에 dto로 바로 조회하는 방법
     * close projection을 사용하면 일치하는 column만 가져올 수 있으며,
     * open projection을 사용하면 내가 원하는 컬럼을 원하는 형태로 가져올 수 있다.
     * 주의할 점은 getter 함수를 만들때 column과 일치시켜야 가져올 수 있다.
     *
     * 중첩구조로 사용하는 경우 최적화 문제가 있음으로
     * root(username)로 조회하는 컬럼은 정상적으로 가져올 수 있는다
     * 그 외의 데이터는 모든 데이터를 가져와 사용하는 방식이다.
     **/

    @Test
    @DisplayName("native query")
    void nativeQueryTest() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);
        em.flush();
        em.clear();

        //when
        Member member = memberRepository.findByNativeQuery("member1");
        //then

        Assertions.assertThat(member.getUserName()).isEqualTo(member1.getUserName());
    }

    @Test
    @DisplayName("Native query with projection")
    void nativeQueryWithProjection() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);
        em.flush();
        em.clear();

        //when
        Page<MemberProjection> byNativeProjection = memberRepository.findByNativeProjection(PageRequest.of(0, 3));
        System.out.println("byNativeProjection = " + byNativeProjection);
        //then
    }
}