package jpsshop.main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpsshop.domain.*;
import org.hibernate.grammars.hql.HqlParser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Main2 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Member member = new Member();
            member.setName("hwnag");
            member.setHomeAddress(new Address("home", "address", "zipcode"));
//            member.getFavoriteFoods().add("apple");
//            member.getFavoriteFoods().add("banana");
//            member.getFavoriteFoods().add("chicken");
//
//            member.getAddressHistory().add(new AddressEntity("city1", "address", "zipcode"));
//            member.getAddressHistory().add(new AddressEntity("city2", "address", "zipcode"));
//            member.getAddressHistory().add(new AddressEntity("city3", "address", "zipcode"));
//            member.getAddressHistory().add(new AddressEntity("city4", "address", "zipcode"));

            em.persist(member);
            em.flush();
            em.clear();

            System.out.println("=======================start===============");
            Member member1 = em.find(Member.class, member.getId());

            System.out.println("=======================지연 로딩===============");
//            List<Address> addressHistory = member1.getAddressHistory();
//            List<AddressEntity> addressHistory = member1.getAddressHistory();
//            Set<String> favoriteFoods = member1.getFavoriteFoods();

            //apple -> kiwi
//            favoriteFoods.remove("apple");
//            favoriteFoods.add("kiwi");

            //city4 -> city100



            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
