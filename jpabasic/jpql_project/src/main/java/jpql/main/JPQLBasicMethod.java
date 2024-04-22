package jpql.main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.Member;
import jpql.MemberType;
import jpql.Team;

import javax.lang.model.element.NestingKind;
import java.util.List;
import java.util.Objects;

public class JPQLBasicMethod {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setName("member");
            member.setAge(20);
            member.setTeam(team);
            member.setMemberType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

/*            String concatJpql = "select 'a' || 'b' from Member m ";
            String concatJpql = "select cconcat('a','b') from Member m ";

            List<String> resultList = em.createQuery(concatJpql , String.class).getResultList();
            for (String member1 : resultList) {
                System.out.println("member1 = " + member1);
            }*/

//            String concatJpql = "select 'a' || 'b' from Member m ";
            String sizeJpql = "select size(t.members) from Team t ";

            List<String> resultList = em.createQuery(sizeJpql , String.class).getResultList();
            for (String member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
            transaction.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
