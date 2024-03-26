package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(MemberTest member){
        em.persist(member);
        return  member.getId();
    }

    public MemberTest findById(long id){
        return em.find(MemberTest.class, id);
    }

}
