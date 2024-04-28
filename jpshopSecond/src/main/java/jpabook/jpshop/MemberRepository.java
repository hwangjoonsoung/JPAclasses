package jpabook.jpshop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member2 member2){
        em.persist(member2);
        return member2.getId();
    }

    public Member2 find(Long id) {
        Member2 member2 = em.find(Member2.class, id);
        return member2;
    }


}
