package jpabook.jpshop.domin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;

    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice() {

        return getOrderPrice() * getCount();

    }

    /*==생성 메서드==*/
    public static OrderItem createItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        orderItem.setItem(item);
        item.removeStock(count);
        return orderItem;
    }



}
