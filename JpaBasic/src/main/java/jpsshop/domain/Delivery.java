package jpsshop.domain;

import jakarta.persistence.*;

@Entity
public class Delivery {

    @Id
    @GeneratedValue
    private Long id;

    private String city;
    private String street;
    private String zipcode;
    private DeliveryStatus status;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
