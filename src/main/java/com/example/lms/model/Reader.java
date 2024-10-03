package com.example.lms.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "reader")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reader_id;
    @NotNull
    private String name;
    @Min(0)
    @Max(120)
    private int age;
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    private String email;
    @Column( unique = true ,length = 10)
    private String pno;
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "reader", cascade = { CascadeType.PERSIST , CascadeType.MERGE})
    private List<Issue> issues = new ArrayList<>();

    @Override
    public String toString() {
        return " Reader : id: "+reader_id+" name: "+name+" age: "+age+" email: "+email;
    }
}
