package jpsshop.main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpsshop.domain.Album;
import jpsshop.domain.Item;
import jpsshop.domain.Order;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Album album = new Album();
            album.setArtist("kim");
            album.setEtc("test");
            em.persist(album);
            em.flush();
            em.clear();
            System.out.println("album.getArtist() = " + album.getArtist());
            List<Album> artist = em.createQuery("select i from Album i where i.artist = :artist").setParameter("artist", album.getArtist()).getResultList();
            for (Album o : artist) {
                System.out.println("o. = " + o.getName());
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
