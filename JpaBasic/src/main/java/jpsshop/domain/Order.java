package jpsshop.domain;

import jakarta.persistence.*;

import javax.xml.namespace.QName;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue
    private Long id;

    @Column(name = "member_id")
    private Long memberID;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
