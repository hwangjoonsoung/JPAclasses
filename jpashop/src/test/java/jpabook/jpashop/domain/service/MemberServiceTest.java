package jpabook.jpashop.domain.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;


    @Test
    public void 회원가입() throws Exception{
        
        //given
        Member member = new Member();
        member.setName("hwang");
        //when
        Long joinedId = memberService.join(member);
        em.flush();

        //then
        Assertions.assertEquals(member , memberRepository.findOne(joinedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception{

        //given
        Member member1 = new Member();
        member1.setName("hwang");
        Member member2 = new Member();
        member2.setName("hwang");

        //when
        memberService.join(member1);

        //then
        Assertions.assertThrows(IllegalStateException.class , () -> {
            memberService.join(member2);
        } );

    }

}