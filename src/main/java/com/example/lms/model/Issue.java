package com.example.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int issue_id;

    @ManyToOne(cascade = { CascadeType.PERSIST , CascadeType.MERGE})
    @JoinColumn(name = "reader_id")
    private Reader reader;

    private Date issue_date;
    private Date due_date;
    private Date return_date;

    @ManyToOne( cascade = { CascadeType.PERSIST , CascadeType.MERGE})
    private Books book;

    @Override
    public String toString() {
        return "Issue [issue_id=" + issue_id + ", reader=" + reader + ", issue_date=" + issue_date +
                ", due_date=" + due_date + ", return_date=" + return_date + ", book=" + book.toString() + "]";
    }
}
