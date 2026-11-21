package com.example.smartlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReliabilityResponse {
    private Long readerId;
    private boolean isReliable;
    private long activeLoansCount;
    private int returnedInLast30Days;
    private int maxAllowedLoans;
}