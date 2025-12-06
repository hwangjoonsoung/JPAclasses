package jpabook.jpshop.api;

import jpabook.jpshop.domin.Address;
import jpabook.jpshop.domin.Order;
import jpabook.jpshop.domin.OrderSearch;
import jpabook.jpshop.domin.OrderStatus;
import jpabook.jpshop.dto.OrderSimpleQueryDto;
import jpabook.jpshop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
     * V1에서의 문제점
     *
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

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> list = orders.stream()
                .map(order -> new SimpleOrderDto(order))
                .toList();

        return list;

    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // lazy 초기화
            orderDate = order.getOrderDateTime();
            orderStatus = order.getOrderStatus();
            address = order.getDelivery().getAddress(); // lazy 초기화
        }
    }

    /**
     * V2에서 문제점
     * 1. lazy초기화의 문제점
     * getMember(),getDelivery() 했을 때 값이 persist context가 없는 경우 persist context가 db쿼리를 날림
     * 2. N+1의 문제 (1번 문제로 인한 파생 문제)
     * 2-1: order -> sql 결과 -> N개
     * 2-2: order가 N번 반복될때 lazy초기화로 인해서 getMember(); getDelivery를 사용하는 순간 쿼리가 한번 더 나감
     * 2-3: 결과적으로 보면 order를 한번 조회하는 것으로 order row만큼 쿼리가 N번 더 나간다.
     **/

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> list = orders.stream()
                .map(order -> new SimpleOrderDto(order))
                .toList();

        return list;
    }

    /**
     * V3에서의 문제점
     * 1. fetch join을 사용했지만 entity를 조회해서 dto로 변환을 해야한다는 문제점이 아직 있음
     **/

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        return orderRepository.findOrderDtos();
    }

    /**
     * V4에서의 문제점
     * 1. 재사용성이 너무 떨어짐.
     * 1-1: V3의 경우 해당 쿼리를 사용하는 모든 api에서 사용할 수 있는데 V4는 틀수한 api에서만 fit하게 떨어지 때문에 재사용성이 떨어짐
     **/

    /**
     * 쿼리 방식 선택 권장 순서
     * 1. entity를 dto로 변환하는 방법을 선택
     * 2. 필요하면 fetch join을 사용
     * 3. dto로 직접 조회하는 방법을 사용
     * 4. 최후의 방법으로 jpa가 제공하는 네이티브 sql이나 jdbc template을 사용해서 sql을 직접 사용하는 방법 사용
     **/


}
