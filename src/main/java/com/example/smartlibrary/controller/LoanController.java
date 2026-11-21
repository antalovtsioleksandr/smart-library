package com.example.smartlibrary.controller;

import com.example.smartlibrary.dto.IssueLoanRequest;
import com.example.smartlibrary.dto.LoanResponse;
import com.example.smartlibrary.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> issueLoan(@RequestBody IssueLoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.issueLoan(request));
    }

    @PostMapping("/{loanId}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.returnLoan(loanId));
    }
}