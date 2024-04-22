package jpsshop.jpql;

import jakarta.persistence.*;
import jpsshop.domain.Album;
import jpsshop.domain.Member;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            String JPQL = "select m from Member m where m.name like '%kim%'";

            List<Member> resultList = em.createQuery(JPQL, Member.class).getResultList();

            for (Member member : resultList) {
                System.out.println("member = " + member);
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
