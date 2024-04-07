package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import javax.xml.transform.Result;

public class JpaAdventage {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    static EntityManager em = emf.createEntityManager();
    static EntityTransaction ts = em.getTransaction();

    public static void main(String[] args) {


        ts.begin();
        Member member = new Member();
        member.setId(200L);
        member.setName("kang");
        em.persist(member);

        ts.commit();

        Member member1 = em.find(Member.class, 200L);
        Member member2 = em.find(Member.class, 200L);
        System.out.println("Result = " + (member1 == member2));

    }

    public static void flase(String[] args) {
        Member member = new Member(300L, "flash");
        ts.begin();

        em.persist(member);

        em.flush();
        System.out.println("-----------------");
        ts.commit();

    }
}
