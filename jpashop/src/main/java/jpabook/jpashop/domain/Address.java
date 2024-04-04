package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.springframework.context.annotation.Primary;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
