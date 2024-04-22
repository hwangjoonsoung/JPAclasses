package jpql.main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpql.Member;
import jpql.MemberType;
import jpql.Team;

import java.rmi.server.RemoteRef;
import java.util.List;
import java.util.Objects;

public class JPQLType {
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

            List<Objects[]> resultList = em.createQuery("select m.name from Member m WHERE M.memberType = jpql.MemberType.ADMIN").getResultList();
            for (Object[] member1 : resultList) {
                System.out.println("member1 = " + member1[0]);
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
