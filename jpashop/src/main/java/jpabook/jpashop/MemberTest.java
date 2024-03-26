package jpabook.jpashop;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
public class MemberTest {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

}
