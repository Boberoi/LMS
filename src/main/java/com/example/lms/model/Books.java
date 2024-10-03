package com.example.lms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "book")
public class Books{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 50)
    private String title;
    private String description;
    @Column(nullable = false, length = 50)
    private String location;
    private BigDecimal price;
    private int available_quantity;
    private int issue_quantity;

    @ManyToOne(cascade = { CascadeType.PERSIST , CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(cascade = { CascadeType.PERSIST , CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable( name = "author_book" ,
                joinColumns = @JoinColumn(name = "book_id") ,
                inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "book",cascade = { CascadeType.PERSIST , CascadeType.MERGE})
    private List<Issue> issue = new ArrayList<>();

    public Books(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Book :  id:"+id+" , title:"+title+" , description:"+description+" , location:"+location+
                " ,available_quantity:"+available_quantity+" , issue_quantity:"+issue_quantity+
                " , category:"+ (category != null ? category.getName() : "No category") + " , authors:"+authors.toString();
    }
}
