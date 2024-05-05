package jpabook.jpshop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpshop.domin.*;
import jpabook.jpshop.domin.item.Book;
import jpabook.jpshop.exception.NotEnoughStockException;
import jpabook.jpshop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;


    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("seoul", "gangbuk", "01173"));
        em.persist(member);

        Item book = new Book();
        book.setName("book1");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        //when
        int orderCount = 1;
        Long order = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order findOrder = orderRepository.findOne(order);

        Assertions.assertEquals(OrderStatus.ORDER,findOrder.getOrderStatus(),"상품 주문시 상태는 order");
        Assertions.assertEquals(1,findOrder.getOrderItems().size(),"주문한 삼품의 종류가 정확해야 한다");
        Assertions.assertEquals(10000,findOrder.getTotalPrice(),"주문 가격은 가격* 수량 이다");
        Assertions.assertEquals(9,book.getStockQuantity(),"주문 수량만큼 재고가 줄어야 한다");

    }

    @Test
    public void 주문취소() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("seoul", "gangbuk", "01173"));
        em.persist(member);

        Item book = new Book();
        book.setName("book1");
        book.setPrice(10000);
        book.setStockQuantity(1);
        em.persist(book);
        //when
        int orderCount = 1;
        Long order = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancelOrder(order);
        Order findOrder = orderRepository.findOne(order);

        //then
        Assertions.assertEquals(1,book.getStockQuantity());
        Assertions.assertEquals(OrderStatus.CANCEL,findOrder.getOrderStatus());

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("seoul", "gangbuk", "01173"));
        em.persist(member);

        Item book = new Book();
        book.setName("book1");
        book.setPrice(10000);
        book.setStockQuantity(1);
        em.persist(book);
        //when
        int orderCount = 2;

        //then
        Assertions.assertThrows(NotEnoughStockException.class, ()->orderService.order(member.getId(), book.getId(), orderCount));


    }

}