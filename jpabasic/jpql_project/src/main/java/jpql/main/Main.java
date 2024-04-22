package jpql.main;

import jakarta.persistence.*;
import jpql.Member;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Member member = new Member();
            member.setName("kim");
            member.setAge(20);
            em.persist(member);

//             TypedQuery<Member> selectMFromMemberM1 = em.createQuery("select m from Member m", Member.class); //반환 타입이 member인 경우
//            TypedQuery<String> selectMFromMemberM2 = em.createQuery("select m.name  from Member m", String.class); //반환 타입이 string인 경우
//            Query query = em.createQuery("select m.name , m.age from Member m"); //반환 타입이 명확하지 않는 경우
//
//            selectMFromMemberM1.getSingleResult(); //반환 값이 1개 인경우
//            selectMFromMemberM1.getResultList(); //반환값이 2개 이상인 경우
            Member singleResult = em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", "kim").getSingleResult();
            System.out.println("singleResult = " + singleResult.getName() );


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
