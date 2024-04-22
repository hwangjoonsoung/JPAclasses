package jpql.main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.Member;
import jpql.Team;
import org.hibernate.annotations.processing.SQL;

import java.time.temporal.TemporalAccessor;
import java.util.List;

public class Join {
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
            em.persist(member);

            em.flush();
            em.clear();

            String innerSql = "select m from Member m inner join m.team t";
            String outerSql = "select m from Member m left outer join m.team t";
            String thetaSql = "select m from Member m , Team t where m.name = t.name";
            String joinFilterSql = "select m , t from Member m inner join m.team t on t.name = 'teamA'";

            List<Member> resultList = em.createQuery(joinFilterSql, Member.class)
                    .setFirstResult(0).setMaxResults(10).getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1.getName());
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
