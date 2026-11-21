package com.example.smartlibrary.dto;

import lombok.Data;

@Data
public class IssueLoanRequest {
    private Long readerId;
    private Long bookId;
}