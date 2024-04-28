package jpabook.jpshop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class Member2RepositoryTest {

    @Autowired
    MemberRepository memberRepository;


    @Test
    @Transactional
    public void testMember() throws Exception{

        //given
        Member2 member2 = new Member2();
        member2.setUsername("Kim");

        //when
        Long save = memberRepository.save(member2);
        Member2 member1 = memberRepository.find(save);

        //then
        Assertions.assertThat(member1).isEqualTo(member2);
        Assertions.assertThat(member1.getUsername()).isEqualTo(member2.getUsername());
        Assertions.assertThat(member1.getId()).isEqualTo(member2.getId());

    }



}