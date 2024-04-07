package hellojpa;

import jakarta.persistence.*;

import java.util.List;

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
//            Member member = new Member();
//            member.setId(100L);
//            member.setName("Lee");
//            // 영속
//            em.persist(member);
//            // 준영속
//            em.detach(member);
//            // 삭제
//            em.remove(member);
            
            /**
            *select
            */
            Member member = em.find(Member.class, 1L);

            /**
            *delete
            */
//            em.remove(1L);
            
            /**
            *update
            */
//            Member member1 = em.find(Member.class, 1L);
//            member.setName("park");
//            List<Member> members = em.createQuery("select m from Member as m", Member.class).getResultList();
//            for (Member member : members) {
//                System.out.println("member.getName() = " + member.getName());
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
