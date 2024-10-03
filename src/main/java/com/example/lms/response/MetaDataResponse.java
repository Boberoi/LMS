package com.example.lms.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDataResponse {
    private String code;
    private String message;
    private String noOfRecords;
}
