package jpsshop.domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.PriorityQueue;

@Entity
public class Member_Product {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

//    @ManyToMany
//    @JoinTable(name = "member_product")
//    private List<Member> members;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int count;

    private int price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

