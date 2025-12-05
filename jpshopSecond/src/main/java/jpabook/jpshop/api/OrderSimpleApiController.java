package jpabook.jpshop.api;

import jpabook.jpshop.domin.Order;
import jpabook.jpshop.domin.OrderSearch;
import jpabook.jpshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne(ManyToOne, OneToOne)에서의 성능 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 **/
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    /**
     * 여기서 발생하는 문제는 다음과 같음
     * 1. entity에서 @jsonIgnore를 하지 않는경우: 연관관계 끼리 루프를 생성하기 때문에 무한 루프에 빠져 select query 발생
     * 2. @jsonIgnore를 적용하면 해당 객체에는 프록시 객체가 들어가는데 json이 프록시 객체를 처리할수 없어 exception 발생
     * 3. @hibernate5module을 bean으로 만들어 주입하면 해결가능하지만 모든 lazyloading이 동작하기 때문에 불필요한 select query 발생
     * 4. 무엇보다 entity가 노출되고 있음
     **/

    /**
     * order.getMember().getName();
     * 여기서 getMember() 까지는 프록시 객체로 유지되지만 getName을 하는순간 query가 발생한다
     **/
}
