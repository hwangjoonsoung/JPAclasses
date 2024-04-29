package jpabook.jpshop.service;

import jpabook.jpshop.domin.*;
import jpabook.jpshop.repository.ItemRepository;
import jpabook.jpshop.repository.MemberRepository;
import jpabook.jpshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findMember(memberId);
        Item item = itemRepository.itemOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    public void cancelOrder(Long id) {
        Order order = orderRepository.findOne(id);
        order.cancel();
    }

    /**
     * 주문 검색
     */
//    public List<Order> findOrder(OrderSearch orderSearch){
//        return orderRepository.finfAll();
//    }
    
}
