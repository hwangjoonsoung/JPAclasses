package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class memberJpaRepositoryTest {

    @Autowired
    memberJpaRepository jpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        System.out.println(member.getId());
        Member savedMember = jpaRepository.save(member);
        System.out.println(savedMember.getId());

        Member findMember = jpaRepository.find(savedMember.getId());

        org.assertj.core.api.Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        org.assertj.core.api.Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        org.assertj.core.api.Assertions.assertThat(member).isEqualTo(savedMember);
        org.assertj.core.api.Assertions.assertThat(savedMember).isEqualTo(findMember);
    }

}