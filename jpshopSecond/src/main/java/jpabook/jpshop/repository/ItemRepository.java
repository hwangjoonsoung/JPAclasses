package jpabook.jpshop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpshop.domin.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {

        if (item.getId() == null) {
            em.persist(item);
        }else{
            em.merge(item);
        }

    }

    public Item itemOne(Long id) {
        Item item = em.find(Item.class, id);
        return item;
    }

    public List<Item> findAll(){
        List<Item> resultList = em.createQuery("select i from Item i ").getResultList();
        return resultList;
    }

}
