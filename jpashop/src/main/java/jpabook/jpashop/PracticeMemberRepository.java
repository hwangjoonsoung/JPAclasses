package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PracticeMemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(PracticeMember member){
        em.persist(member);
        return member.getId();
    }

    public PracticeMember findById(long id){
        return em.find(PracticeMember.class, id);
    }

}
