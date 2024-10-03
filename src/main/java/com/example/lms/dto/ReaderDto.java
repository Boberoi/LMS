package com.example.lms.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReaderDto {
    @CsvBindByName(column = "Id")
    @Id
    private int id;
    @CsvBindByName(column = "Name")
    private String Name;
    @CsvBindByName(column = "Age")
    private int age;
    @CsvBindByName(column = "Email")
    private String email;
    @CsvBindByName(column = "Phone")
    private String pno;
    @CsvBindByName(column = "Address")
    private String address;
}