package section1to4;

import jakarta.persistence.*;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            /**
            *insert
            */
            // 비영속
//            SectionMember sectionMember = new SectionMember();
//            sectionMember.setId(100L);
//            sectionMember.setName("Lee");
//            // 영속
//            em.persist(sectionMember);
//            // 준영속
//            em.detach(sectionMember);
//            // 삭제
//            em.remove(sectionMember);
            
            /**
            *select
            */
            SectionMember sectionMember = em.find(SectionMember.class, 1L);

            /**
            *delete
            */
//            em.remove(1L);
            
            /**
            *update
            */
//            SectionMember member1 = em.find(SectionMember.class, 1L);
//            sectionMember.setName("park");
//            List<SectionMember> members = em.createQuery("select m from SectionMember as m", SectionMember.class).getResultList();
//            for (SectionMember sectionMember : members) {
//                System.out.println("sectionMember.getName() = " + sectionMember.getName());
//            }
            
            transaction.commit();
        }catch (Exception e){
            transaction.rollback();
        }finally {
            em.close();
            emf.close();
        }


    }
}
