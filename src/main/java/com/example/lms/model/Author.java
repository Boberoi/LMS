package com.example.lms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int author_id;
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors" , cascade = { CascadeType.PERSIST , CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Books> books = new ArrayList<>();

    public Author(int i, String name) {
        this.author_id = i;
        this.name = name;
    }
    @Override
    public String toString() {
        return name +" ,";
    }
}
