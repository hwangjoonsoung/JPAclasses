package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
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

}