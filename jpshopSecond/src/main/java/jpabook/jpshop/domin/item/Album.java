package jpabook.jpshop.domin.item;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jpabook.jpshop.domin.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Album extends Item {

    private String actor;
    private String etc;

}
