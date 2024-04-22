package jpsshop.domain;

import jakarta.persistence.*;
import org.hibernate.sql.ast.tree.expression.Literal;

import java.util.List;

@Entity
public class Product extends BaseEntity{

    @Id
    @GeneratedValue
    private Long id;
    private String name;

//    @ManyToMany
//    @JoinTable(name = "member_product")
//    private List<Member> members;

    @OneToMany(mappedBy = "product")
    private List<Member_Product> products;


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

