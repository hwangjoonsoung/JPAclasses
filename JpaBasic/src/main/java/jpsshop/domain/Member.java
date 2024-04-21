package jpsshop.domain;

import jakarta.persistence.*;
import org.hibernate.sql.results.graph.instantiation.internal.ArgumentDomainResult;

import java.security.spec.RSAOtherPrimeInfo;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue
    private Long id;

    private String name;

    //address
//    private String city;
//    private String street;
//    private String zipCode;


//    public Set<String> getFavoriteFoods() {
//        return favoriteFoods;
//    }

//    public void setFavoriteFoods(Set<String> favoriteFoods) {
//        this.favoriteFoods = favoriteFoods;
//    }

//    public List<Address> getAddressHistory() {
//        return addressHistory;
//    }
//
//    public void setAddressHistory(List<Address> addressHistory) {
//        this.addressHistory = addressHistory;
//    }


//    public List<AddressEntity> getAddressHistory() {
//        return addressHistory;
//    }

//    public void setAddressHistory(List<AddressEntity> addressHistory) {
//        this.addressHistory = addressHistory;
//    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Embedded
    private Address homeAddress;

//    @ElementCollection
//    @CollectionTable(name = "favorite_food" , joinColumns = @JoinColumn(name = "member_id"))
//    @Column(name = "food_name")
//    private Set<String> favoriteFoods = new HashSet<>();

//    @ElementCollection
//    @CollectionTable(name = "address" , joinColumns = @JoinColumn(name = "member_id"))
//    private List<Address> addressHistory = new ArrayList<>();

//    @OneToMany(cascade = CascadeType.ALL , orphanRemoval = true)
//    @JoinColumn(name = "member_Id")
//    private List<AddressEntity> addressHistory = new ArrayList<>();

    //period
//    private LocalDateTime started_at;
//    private LocalDateTime ended_at;
    @Embedded
    private Period period;


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


}
