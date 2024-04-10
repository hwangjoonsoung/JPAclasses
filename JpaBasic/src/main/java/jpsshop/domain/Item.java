package jpsshop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Entity
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue
    private Long id;
    private String name;

    private int price;
    private int stockQuantity;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

}
