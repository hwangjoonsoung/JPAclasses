package jpabook.jpashop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception{

        //given
        MemberTest member = new MemberTest();
        member.setName("hwang");

        //when
        Long save = memberRepository.save(member);

        //then
        MemberTest byId = memberRepository.findById(save);

        org.assertj.core.api.Assertions.assertThat(member).isSameAs(byId);

    }
    


}