package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository jpaRepository;

    @Test
    public void testMember(){
        System.out.println(jpaRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = jpaRepository.save(member);

        Member findMember = jpaRepository.find(savedMember.getId());

        org.assertj.core.api.Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        org.assertj.core.api.Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        org.assertj.core.api.Assertions.assertThat(member).isEqualTo(savedMember);
        org.assertj.core.api.Assertions.assertThat(savedMember).isEqualTo(findMember);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        jpaRepository.save(member1);
        jpaRepository.save(member2);

        Member findMember1 = jpaRepository.findById(member1.getId()).get();
        Member findMember2 = jpaRepository.findById(member2.getId()).get();

        org.assertj.core.api.Assertions.assertThat(member1).isEqualTo(findMember1);
        org.assertj.core.api.Assertions.assertThat(member2).isEqualTo(findMember2);

        long count = jpaRepository.count();
        org.assertj.core.api.Assertions.assertThat(count).isEqualTo(2);

        List<Member> members = jpaRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(members.size()).isEqualTo(2);

        jpaRepository.delete(member1);
        jpaRepository.delete(member2);

        members = jpaRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(members.size()).isEqualTo(0);

    }


    @Test
    public void fidByUserNameAndAgeGreaterThen() {
        Member member1 = new Member("member",20);
        Member member2 = new Member("member",10);

        jpaRepository.save(member1);
        jpaRepository.save(member2);

        List<Member> member = jpaRepository.findByUsernameAndAgeGreaterThen("member", 15);
        Assertions.assertThat(member.size()).isEqualTo(1);
    }


    @Test
    public void pageing() {
        Member member1 = new Member("member1",10);
        Member member2 = new Member("member2",20);
        Member member3 = new Member("member4",30);
        Member member4 = new Member("member5",40);
        Member member5 = new Member("member6",50);

        jpaRepository.save(member1);
        jpaRepository.save(member2);
        jpaRepository.save(member3);
        jpaRepository.save(member4);
        jpaRepository.save(member5);

        int age = 10;
        int offset = 0;
        int limit = 10;

        //when
        List<Member> members = jpaRepository.findByPage(age, offset, limit);
        long totalCount = jpaRepository.totalCount(age);

        //then
        Assertions.assertThat(members.size()).isEqualTo(1);
        Assertions.assertThat(totalCount).isEqualTo(1);

    }

    @Test
    public void bulkUpdateAge() {
        Member member1 = new Member("member1",10);
        Member member2 = new Member("member2",20);
        Member member3 = new Member("member3",30);
        Member member4 = new Member("member4",10);
        Member member5 = new Member("member5",20);

        jpaRepository.save(member1);
        jpaRepository.save(member2);
        jpaRepository.save(member3);
        jpaRepository.save(member4);
        jpaRepository.save(member5);

        int resultCount = jpaRepository.bulkAgePlus(20);
        Assertions.assertThat(resultCount).isEqualTo(3);

    }

}