package jpabook.jpshop.repository.order.query;

import jakarta.persistence.EntityManager;
import jpabook.jpshop.dto.OrderFlatDto;
import jpabook.jpshop.dto.OrderItemQueryDto;
import jpabook.jpshop.dto.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> orders = findOrders();
        orders.forEach(order -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(order.getOrderId());
            order.setOrderItem(orderItems);
        });
        return orders;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {

        return em.createQuery("select new jpabook.jpshop.dto.OrderItemQueryDto(oi.order.id , i.name, i.price, oi.count) from OrderItem oi" +
                " join oi.item i " +
                " where oi.order.id = :orderId", OrderItemQueryDto.class).setParameter("orderId", orderId).getResultList();

    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpshop.dto.OrderQueryDto(o.id , m.name , o.orderDateTime , o.orderStatus , d.address ) from Order o " +
                " join o.member m" +
                " join o.delivery d ", OrderQueryDto.class).getResultList();
    }

    public List<OrderQueryDto> findOrderQueryDtosOptimization() {
        List<OrderQueryDto> result = findOrders();
        List<Long> orderIds = result.stream().map(orderQueryDto -> orderQueryDto.getOrderId()).collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpshop.dto.OrderItemQueryDto(oi.order.id , i.name, i.price, oi.count) from OrderItem oi" +
                " join oi.item i " +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class).setParameter("orderIds", orderIds).getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.forEach(o -> o.setOrderItem(orderItemMap.get(o.getOrderId())));
        return result;

    }

    public List<OrderFlatDto> findOrderQueryDtosFlat() {
        return em.createQuery(
                "select new jpabook.jpshop.dto.OrderFlatDto(o.id, m.name, o.orderDateTime, o.orderStatus, d.address, i.name, i.price,oi.count )" +
                        " from Order  o " +
                        " join o.member m" +
                        " join o.delivery d " +
                        " join o.orderItems oi " +
                        " join oi.item i", OrderFlatDto.class
        ).getResultList();

    }
}
