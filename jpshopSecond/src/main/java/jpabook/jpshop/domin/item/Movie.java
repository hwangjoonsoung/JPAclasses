package jpabook.jpshop.domin.item;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jpabook.jpshop.domin.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Movie extends Item {

    private String director;
    private String actor;

}
