package jpql.main;

import jakarta.persistence.*;
import jpql.Member;
import jpql.MemberDTO;
import jpql.Team;

import java.util.List;

public class Main2 {
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

            em.flush();
            em.clear();

/*            List<Member> selectMFromMemberM = em.createQuery("select m from Member m ", Member.class).getResultList();
            Member member1 = selectMFromMemberM.get(0);
            member1.setAge(10);*/

//            List<Team> resultList = em.createQuery("select m.team from Member m ", Team.class).getResultList();

            List<MemberDTO> resultList1 = em.createQuery("select new jpql.MemberDTO(m.name , m.age) from Member m", MemberDTO.class).getResultList();
            MemberDTO memberDTO = resultList1.get(0);
            System.out.println("memberDTO.getName() = " + memberDTO.getName());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
