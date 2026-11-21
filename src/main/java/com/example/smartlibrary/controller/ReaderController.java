package com.example.smartlibrary.controller;

import com.example.smartlibrary.dto.LoanResponse;
import com.example.smartlibrary.dto.ReliabilityResponse;
import com.example.smartlibrary.model.Reader;
import com.example.smartlibrary.service.LoanService;
import com.example.smartlibrary.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<Reader> createReader(@RequestBody Reader reader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readerService.createReader(reader));
    }

    @GetMapping("/{readerId}/reliability")
    public ResponseEntity<ReliabilityResponse> getReliability(@PathVariable Long readerId) {
        return ResponseEntity.ok(readerService.checkReliability(readerId));
    }

    @GetMapping("/{readerId}/loans/active")
    public ResponseEntity<List<LoanResponse>> getActiveLoans(@PathVariable Long readerId) {
        return ResponseEntity.ok(loanService.getActiveLoansByReader(readerId));
    }
}