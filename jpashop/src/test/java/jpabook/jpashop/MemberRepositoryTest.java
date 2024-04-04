package jpabook.jpashop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    PracticeMemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception{

        //given
        PracticeMember member = new PracticeMember();
        member.setName("hwang");

        //when
        Long save = memberRepository.save(member);

        //then
        PracticeMember byId = memberRepository.findById(save);

        org.assertj.core.api.Assertions.assertThat(member).isSameAs(byId);

    }
    


}