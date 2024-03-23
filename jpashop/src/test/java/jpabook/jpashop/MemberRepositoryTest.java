package jpabook.jpashop;

import org.hibernate.dialect.TiDBDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception{

        //given
        Member member = new Member();
        member.setName("hwang");

        //when
        Long save = memberRepository.save(member);

        //then
        Member byId = memberRepository.findById(save);

        org.assertj.core.api.Assertions.assertThat(member).isSameAs(byId);

    }
    


}