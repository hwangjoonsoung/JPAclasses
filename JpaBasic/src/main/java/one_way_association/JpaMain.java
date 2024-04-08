package one_way_association;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {

            /**
            *DB의 연관관계처럼 사용하면
             * 불편하게 사용해야 한다.
             * 그 이유는 테이블을 조인하는 상황에서 계속 find를 해야 하기 때문이다.
            */
/*
            TwoWayTeam team = new TwoWayTeam();
            team.setName("TwoWayTeam A");
            em.persist(team);
            TwoWaySection5Member member = new TwoWaySection5Member();
            member.setName("member1");
            member.setTeamId(team.getId());
            em.persist(member);

            //가져올때 (계속 물어봐야 하는 과정이 있어서 불편하다.)
            TwoWaySection5Member member1 = em.find(TwoWaySection5Member.class, member.getId());
            TwoWayTeam team1 = em.find(TwoWayTeam.class, member1.getTeamId());
*/
            /**
            *객체지향적으로 설계를 하면 다음과 같이 사용할 수 있다.
            */
            Team team = new Team();
            team.setName("TwoWayTeam A");
            em.persist(team);

            Section5Member member = new Section5Member();
            member.setName("member1");
            member.setTeam(team);
            em.persist(member);

            /**
            *ID는 변경할 수 없다.
            */
            Section5Member findMember = em.find(Section5Member.class, 302L);
            Team findTeam = findMember.getTeam();
            findTeam.setName("test100");

            transaction.commit();
        }catch (Exception e){
            transaction.rollback();
        }finally {
            em.close();
            emf.close();
        }


    }
}
