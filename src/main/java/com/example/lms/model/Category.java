package com.example.lms.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = { CascadeType.PERSIST , CascadeType.MERGE} , fetch = FetchType.EAGER)
    private List<Books> books = new ArrayList<>();

    public Category(String category_name) {
        this.name = category_name;
    }

    @Override
    public String toString() {
        return "Categoyr: "+name;
    }

}
