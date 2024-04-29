package jpabook.jpshop.domin;

import jakarta.persistence.*;
import jpabook.jpshop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn
public class Item {

    @GeneratedValue
    @Id
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int stockQuantity;

    private int price;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();


    /*==비즈니스 로직==*/
    public void addStock(int quentity) {
        this.stockQuantity += quentity;
    }

    public void removeStock(int quentyty) {
        int restStock = this.stockQuantity - quentyty;

        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;


    }



}
