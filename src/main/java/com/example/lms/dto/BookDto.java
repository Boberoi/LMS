package com.example.lms.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Book;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookDto {
    @CsvBindByName(column = "Id")
    @Id
    private int id;
    @CsvBindByName(column = "Title")
    private String title;
    @CsvBindByName(column = "Description")
    private String description;
    @CsvBindByName(column = "Location")
    private String location;
    @CsvBindByName(column = "Price")
    private BigDecimal price;
    @CsvBindByName(column = "AvailableQuantity")
    private int available_quantity;
    @CsvBindByName(column = "IssuedQuantity")
    private int issue_quantity;
    @CsvBindByName(column = "AuthorNames")
    private String authorNames;
    @CsvBindByName(column = "CategoryName")
    private String categoryName;

    public BookDto(int id , String title) {
        this.id = id;
        this.title = title;
    }
}
