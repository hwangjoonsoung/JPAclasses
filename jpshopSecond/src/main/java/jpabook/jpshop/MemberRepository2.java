package jpabook.jpshop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpshop.domin.Member;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository2 {

    @PersistenceContext
    private EntityManager em;

    public Long save(Member2 member){
        em.persist(member);
        return member.getId();
    }

    public Member2 find(Long id) {
        Member2 member2 = em.find(Member2.class, id);
        return member2;
    }


}
