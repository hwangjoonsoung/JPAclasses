package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


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
        Member member = createMember();

        Book book = creatBook("사장학개론", 1000, 10);

        int orderCount = 2;
        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order findOrder = orderRepository.findOne(orderId);

        //then
        Assertions.assertEquals(OrderStatus.ORDER, findOrder.getStatus(), "상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1, findOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        Assertions.assertEquals(orderCount * book.getPrice(), findOrder.getTotalPrice(), "주문한 가격은 가격*수량이다.");
        Assertions.assertEquals(8, book.getStockQuantity(), "주문한 수량만큼 재고가 줄어야 한다.");

    }


    @Test
    public void 주문취소() throws Exception{

        //given
        Member member = createMember();
        Book book = creatBook("사장학개론", 1000, 10);

        int orderCount = 2;
        //when
        Long order = orderService.order(member.getId(), book.getId(), orderCount);
        System.out.println(book.getStockQuantity());
        orderService.cancelOrder(order);

        //then
        Order one = orderRepository.findOne(order);
        Assertions.assertEquals(one.getStatus(),OrderStatus.CANCEL , "주문이 취소되면 orderstate 는 cancel이 된다");
        Assertions.assertEquals(10 , book.getStockQuantity() , "주문이 취소되면 재고는 다시 돌아와야 한다.");


    }
    
    @Test
    public void 상품주문_재고수량초과() throws Exception{
        
        //given
        Member member = createMember();
        Book book = creatBook("사장학개론", 1000, 10);

        int orderCount = 11;
        //when

        //then
        Assertions.assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderCount));
    }


    private Book creatBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("서울", "강북구 미아동","312-20"));
        em.persist(member);
        return member;
    }

}