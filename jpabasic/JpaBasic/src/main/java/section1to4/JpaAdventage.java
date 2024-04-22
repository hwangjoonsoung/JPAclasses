package section1to4;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaAdventage {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    static EntityManager em = emf.createEntityManager();
    static EntityTransaction ts = em.getTransaction();

    public static void main(String[] args) {


        ts.begin();
        SectionMember sectionMember = new SectionMember();
        sectionMember.setId(200L);
        sectionMember.setName("kang");
        em.persist(sectionMember);

        ts.commit();

        SectionMember sectionMember1 = em.find(SectionMember.class, 200L);
        SectionMember sectionMember2 = em.find(SectionMember.class, 200L);
        System.out.println("Result = " + (sectionMember1 == sectionMember2));

    }

    public static void flase(String[] args) {
        SectionMember sectionMember = new SectionMember(300L, "flash");
        ts.begin();

        em.persist(sectionMember);

        em.flush();
        System.out.println("-----------------");
        ts.commit();

    }
}
