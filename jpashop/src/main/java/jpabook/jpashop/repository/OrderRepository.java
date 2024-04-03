package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.io.ObjectStreamField;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /**
    * usingString
    */
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        if(orderSearch.getOrderStatus() != null){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        if(StringUtils.hasText(orderSearch.getMemberName())){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000);

        if(orderSearch.getOrderStatus() != null){
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if(StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
    *JPA criteria
    */
    public List<Order> finAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
        Root<Order> from = query.from(Order.class);
        Join<Object, Object> member = from.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();
        if(orderSearch.getOrderStatus() != null){
            Predicate status = criteriaBuilder.equal(from.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = criteriaBuilder.like(member.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        query.where(criteriaBuilder.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query1 = em.createQuery(query).setMaxResults(1000);
        return query1.getResultList();

    }

}
