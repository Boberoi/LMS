package com.example.lms.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IssueDto {
    @CsvBindByName(column = "Id")
    @Id
    private int id;
    @CsvBindByName(column = "BookId")
    private int bookId;
    @CsvBindByName(column = "ReaderId")
    private int readerId;
    @CsvBindByName(column = "IssueDate")
    @CsvDate("yyyy-MM-dd")
    private Date issueDate;
    @CsvBindByName(column = "DueDate")
    @CsvDate("yyyy-MM-dd")
    private Date dueDate;
    @CsvDate("yyyy-MM-dd")
    @CsvBindByName(column = "ReturnDate")
    private Date returnDate;
}
