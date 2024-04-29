package jpabook.jpshop.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpshop.domin.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;


    public void save(Member member) {
        em.persist(member);
    }

    public Member findMember(Long id){
        Member member = em.find(Member.class, id);
        return member;
    }


    public List<Member> findAll(){
        List<Member> resultList = em.createQuery("select m from Member  m", Member.class).getResultList();

        return resultList;
    }

    public List<Member> findByName(String name) {
        List<Member> resultList = em.createQuery("select m from Member m where name = :name", Member.class).setParameter("name", name).getResultList();
        return resultList;
    }

}
