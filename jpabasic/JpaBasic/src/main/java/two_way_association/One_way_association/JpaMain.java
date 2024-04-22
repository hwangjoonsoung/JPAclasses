package two_way_association.One_way_association;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import one_way_association.Team;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {

            TwoWayTeam twoWayTeam = new TwoWayTeam();
            twoWayTeam.setName("TwoWayTeam B");
            em.persist(twoWayTeam);

            TwoWaySection5Member member = new TwoWaySection5Member();
            member.setName("member2");
            member.setTeam(twoWayTeam);
            em.persist(member);

            twoWayTeam.getMembers().add(member);

//            em.flush();
//            em.clear();
            System.out.println("================================");
            TwoWayTeam twoWayTeam1 = em.find(TwoWayTeam.class, twoWayTeam.getId()); //select team query 날라감
            List<TwoWaySection5Member> members = twoWayTeam1.getMembers(); //select member query 날라감

            for (TwoWaySection5Member twoWaySection5Member : members) {
                System.out.println("twoWaySection5Member = " + twoWaySection5Member.getName());
            }

            transaction.commit();
        }catch (Exception e){
            transaction.rollback();
        }finally {
            em.close();
            emf.close();
        }

    }
}
