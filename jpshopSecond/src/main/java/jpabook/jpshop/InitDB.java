package jpabook.jpshop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpshop.domin.*;
import jpabook.jpshop.domin.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.support.DelegatingEntityInformation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    private void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;

        public void dbInit1(){
            Member member = createMember("Member", "서울", "1", "01173");
            em.persist(member);

            Book book1 = createBook("jpa1 book", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("jpa2 book", 20000, 200);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
        
        public void dbInit2(){
            Member member = createMember("Member2", "진주", "2", "11111");
            em.persist(member);

            Book book1 = createBook("spring1 book", 30000, 300);
            em.persist(book1);

            Book book2 = createBook("spring2 book", 40000, 400);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createItem(book1, 30000, 3);
            OrderItem orderItem2 = OrderItem.createItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public Member createMember(String name, String city, String street, String zipCode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city,street,zipCode));
            return member;
        }
        public Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        public Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

    }
}


