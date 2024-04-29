package jpabook.jpshop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpshop.domin.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        Order order = em.find(Order.class, id);
        return order;
    }

//    public List<Order> findAll(OrderSearch orderSearch) {
//
//    }


}
