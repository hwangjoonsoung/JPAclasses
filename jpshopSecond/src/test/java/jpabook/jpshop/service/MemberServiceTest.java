package jpabook.jpshop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jpabook.jpshop.domin.Member;
import jpabook.jpshop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    @Rollback(value = false)
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("Park");

        //when
        Long join = memberService.join(member);
        //then
        Member member1 = memberService.findMember(join);
        Assertions.assertEquals(member , member1);

    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        Member member = new Member();
        member.setName("Park");

        //when

        //then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.join(member);
        });


    }


}