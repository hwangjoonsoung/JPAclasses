package inheritance_relationship;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Movie2 movie = new Movie2();
            movie.setAuthor("kim");
            movie.setIsbn("999");
            movie.setName("joker");
            movie.setPrice(10000);

            em.persist(movie);
            em.flush();
            em.clear();

            Movie2 movie1 = em.find(movie.getClass(), movie.getId());
            System.out.println("movie1 = " + movie1.getName());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
