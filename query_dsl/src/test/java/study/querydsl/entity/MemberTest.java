package study.querydsl.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonWriter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("test entity")
    void testEntity() throws Exception {
        //given
        Team teamA = new Team("team A");
        Team teamB = new Team("team B");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        //when
        em.flush();
        em.clear();
        //then
        List<Member> resultList = em.createQuery("select m from Member  m", Member.class).getResultList();

        for (Member member : resultList) {
            System.out.println(member);
        }

    }


}
