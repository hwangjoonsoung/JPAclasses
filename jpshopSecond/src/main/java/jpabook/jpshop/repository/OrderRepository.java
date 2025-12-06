package jpabook.jpshop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jpabook.jpshop.domin.Order;
import jpabook.jpshop.domin.OrderSearch;
import jpabook.jpshop.dto.OrderSimpleQueryDto;
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

    public List<Order> findAllByString(OrderSearch orderSearch) {

        /**
         * 기본
         * 문제는 이제 값이 ordersearch에 값이 없을때 문제다.
         */
/*        em.createQuery("select o from Order o join o.member m" +
                " where o.orderStatus = :status " +
                " and m.name like :name", Order.class)
                .setParameter("status",orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000).getResultList();*/

        String jpql = "select o from Order o join o.member m ";
        boolean isFirstCondition = true;
        if (orderSearch.getMemberName() != null) {
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        if (orderSearch.getOrderStatus() != null) {
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " o.orderStatus = :status";
        }

        TypedQuery<Order> orderTypedQuery = em.createQuery(jpql, Order.class).setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            orderTypedQuery = orderTypedQuery.setParameter("status", orderSearch.getOrderStatus());
        }
        if (orderSearch.getMemberName() != null) {
            orderTypedQuery = orderTypedQuery.setParameter("name", orderSearch.getMemberName());
        }

        List<Order> resultList = orderTypedQuery.getResultList();
        return resultList;

    }

    //fetch join
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order  o " +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpshop.dto.OrderSimpleQueryDto(o.id,m.name,o.orderDateTime , o.orderStatus , d.address) o from Order o" +
                " join o.member m " +
                " join o.delivery d ", OrderSimpleQueryDto.class).getResultList();
    }
}
