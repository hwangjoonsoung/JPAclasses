package jpql.main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.Member;
import jpql.MemberType;
import jpql.Team;
import org.hibernate.annotations.processing.SQL;

import java.util.List;
import java.util.Objects;

public class Condition {
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

            /*String sql = "select " +
                    "case when  m.age <= 10 then '학생' "+
                    "    when  m.age >= 60 then '경로' "+
                    "    else '일반' end "+
                    "from Member m ";*/
//            String sql = "select coalesce(m.name , 'no name') from Member m ";
            String sql = "select nullif(m.name , 'member') from Member m ";

            List<String> resultList = em.createQuery(sql , String.class).getResultList();
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
