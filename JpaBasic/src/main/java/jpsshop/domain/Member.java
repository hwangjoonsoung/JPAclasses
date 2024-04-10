package jpsshop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Entity
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue
    private Long id;

    private String name;
    private String city;
    private String street;
    private String zipCode;

    @OneToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

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

    @OneToMany(mappedBy = "member")
    private List<Member_Product> product;

//    @ManyToMany
//    @JoinTable(name = "member_product")
//    private List<Product> products =new ArrayList<>();

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
