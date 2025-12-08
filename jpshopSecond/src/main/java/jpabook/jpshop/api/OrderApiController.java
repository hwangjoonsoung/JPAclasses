package jpabook.jpshop.api;

import jpabook.jpshop.domin.*;
import jpabook.jpshop.dto.OrderFlatDto;
import jpabook.jpshop.dto.OrderItemQueryDto;
import jpabook.jpshop.dto.OrderQueryDto;
import jpabook.jpshop.repository.OrderRepository;
import jpabook.jpshop.repository.order.query.OrderQueryRepository;
import jpabook.jpshop.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderService orderService;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        for (Order order : orders) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(orderItem -> orderItem.getItem().getName());

        }
        return orders;
    }

    /**
     * V1의 문제점
     * 1. entity를 json으로 보내서 노출되고 있음.
     **/

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        return orders.stream().map(order -> new OrderDto(order))
                .collect(Collectors.toList());
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //        private List<OrderItem> orderItems; ///entity가 외부에 노출됨
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDateTime();
            this.orderStatus = order.getOrderStatus();
            this.address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(orderItem -> orderItem.getItem().getName()); //entity가 외부에 노출됨.
            this.orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }

    /**
     * V2의 문제점
     * 1. orderItem을 내보낼때도 entity로 내보내는것이 아닌 dto로 변환해서 내보내야 한다.
     * 1-1: order.getOrderItems().stream().forEach(orderItem -> orderItem.getItem().getName());
     **/

    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItemPagingTest();
//        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream().map(order -> new OrderDto(order))
                .collect(Collectors.toList());
    }

    /**
     * V3의 문제점
     * 1. 1:N을 fetch join을 사용하는 순간 paging query가 안나간다. firstResult/maxResults specified with collection fetch; applying in memory 발생
     * 1-1: select o1_0.order_id,d1_0.delivery_id,d1_0.city,d1_0.street,d1_0.zipcode,d1_0.status,m1_0.member_id,m1_0.city,m1_0.street,m1_0.zipcode,m1_0.name,o1_0.order_date_time,o2_0.order_id,o2_0.order_item_id,o2_0.count,i1_0.item_id,i1_0.dtype,i1_0.name,i1_0.price,i1_0.stock_quantity,i1_0.actor,i1_0.etc,i1_0.author,i1_0.isbn,i1_0.director,o2_0.order_price,o1_0.order_status from orders o1_0 join member m1_0 on m1_0.member_id=o1_0.member_id join delivery d1_0 on d1_0.delivery_id=o1_0.delivery_id join order_item o2_0 on o1_0.order_id=o2_0.order_id join item i1_0 on i1_0.item_id=o2_0.item_id
     * 1-2: 위 쿼리를 보면 limit offset을 확인할 수 없다.
     * 1-3: 이는 hibernate가 memory에 올려서 paging작업을 한다.이는 쿼리를 날렸을때 데이터가 많으면 문제가 발생할 가능성이 다분하다.
     * 1-4: hibernate가 memory에 올려서 동작하는 이유는 sql기준에서 보면 limit을 추가해서 쿼리를 날리는 경우와 jpa가 받는 결과가 다를 가능성이 있다. (해당 프로젝트 기준 쿼리는 4개의 row를 생성하지만 order 정보가 중복으로 됨으로 jpa에서 return해주는건 2개의 row로 변경된다.)
     * 1-5: 이렇다 보니 limit을 추가해서 query를 날리면 hibernate는 1개의 row만 받아야 하는데, 그것이 불가능하기 때문이다.
     * */

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(order -> new OrderDto(order))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v3.1.1/orders")
    public List<OrderDto> orderV3_1_1_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        return orders.stream().map(order -> new OrderDto(order))
                .collect(Collectors.toList());
    }

    /**
     * 1. toOne은 fetch join을 사용 paging query 적용할 수 있음
     * 2. toMany는 lazy loading으로 조회한다.대신 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size @BatchSize를 적용한다.
     * 2-1. default_batch_fetch_size를 사용하면 global하게 batch size를 적용할 수 있다.
     * 2-2. batch size를 적용한다는 의미는 1+N+M 문제를 해결할 수 있다는 의미다. (1+N+M -> 1+1+1)
     * 2-3. 1+N+M이 되는 이유는 예제를 기준으로 order는 2개의 row, 1개의 order에 2개의 item이 있다. 1개의 order에서 orderItems(N)를 조회하고 그 조회한 값에서 item(M)을 찾아와야 하기 때문이다.
     * 2-4. default_batch_fetch_size를 사용하면 설정한 size만큼 toMany를 조회할때 in query로 변경되서 select query가 나간다.
     * 2-5. ex)select o1_0.order_id,o1_0.order_item_id,o1_0.count,o1_0.item_id,o1_0.order_price from order_item o1_0 where o1_0.order_id in (1,2);
     * 3. default_batch_fetch_size는 최대 1000으로 한다.
     * 3-1. DB에 따라서 다르지만 in query가 1000개 이상 넘기면 오류가 나는 DB가 있음.
     * 3-2. fetch_size를 너무 크게 하면 순간 큰 부하가 생길 가능성이 있으며 너무 작게 하면 부하는 작지만 속도는 느리다. 이거는 서버 스펙에 따라 다르다.
     **/

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     * toOne 관계는 한개의 row로 가져올 수 있지만 toMany관계는 그렇지 않다.
     * 그렇기 때문에 loop를 돌면서 순회하면서 별도로 조회하는 방법을 사용한다.
     * 1. 여기서 순회를 하면서 조회하는 코드로 인해 N+1문제가 발생된다.
     * 2. 근데 예제에서는 item까지 조회를 해야 함으로 M+N+1문제가 발생한다. (order(2 row) -> orderItems (2 row) -> items (4 row))
     * OrderQueryRepository.class
     * orders.forEach(order -> {
     *     List<OrderItemQueryDto> orderItems = findOrderItems(order.getOrderId());
     *     order.setOrderItem(orderItems);
     * });
     **/

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5(){
        return orderQueryRepository.findOrderQueryDtosOptimization();
    }
    /**
     * 쿼리 조건을 in 절로 변경함으로써 한번에 1+1형식으로 가져올 수 있도록 변경
     **/

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findOrderQueryDtosFlat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());

//        return flats.stream().collect(
//                        groupingBy(o -> new OrderQueryDto(
//                                        o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress())
//                                , mapping(o ->
//                                        new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList()
//                                ))).entrySet().stream()
//                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue())).collect(toList());
    }
    /**
     * 장점: 쿼리 1번에 작업 가능
     * 단점:
     * 1. paging 불가능
     * 2. 메모리에서 별도의 작업이 필요하다는 점
     * 3. 상황에 따라서 v5보다 느릴 수 있다.
     **/

    /**
     * 권장 순서
     * 1. 엔티니 조회방식으로 우선 접근
     * 1-1: fetch join으로 쿼리 수를 최적회
     * 1-2: 켈렉션 최적화
     * 1-2-1: 페이징 필요한 경우 : batch_fetch_size적용
     * 1-2-2: 페이징 필요없는 경우 : fetch join 사용
     * 2. 엔티티 조회 방식으로 해결이 안되면 dto조회 방식 사용
     * 2-1: dto를 조회하는 방식은 성능 최적화 방식을 수정하는 경우 코드를 변경해야 할 가능성이 큼
     * 3. dto 조회 방식으로 해결 안되면 nativeSQL or spring jdbc template 사용
     **/

    /**
     * 성능 최적화와 코드 복잡도에서 줄타기를 해야한다.
     * 결국 dto로 최적화해서 가져와야 하면 v5방식을 선택하는것이 최선의 선택이 될 수 있고, 코드 복잡도를 해결하기 위해서는 v3.1.1방식을 사용해야 한다.
     **/

}
