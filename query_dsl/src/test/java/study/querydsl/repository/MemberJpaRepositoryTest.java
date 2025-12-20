package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

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

}