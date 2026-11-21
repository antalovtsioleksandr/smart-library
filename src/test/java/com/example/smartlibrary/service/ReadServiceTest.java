package com.example.smartlibrary.service;

import com.example.smartlibrary.dto.ReliabilityResponse;
import com.example.smartlibrary.model.Loan;
import com.example.smartlibrary.model.Reader;
import com.example.smartlibrary.repository.LoanRepository;
import com.example.smartlibrary.repository.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ReaderService readerService;

    private Reader testReader;

    @BeforeEach
    void setUp() {
        testReader = new Reader("Олександр Анталовці", "antalovtsi@example.com");
        testReader.setId(1L);
    }

    @Test
    void checkReliability_ShouldReturnReliable_WhenReaderHasReturnedThreeBooksOnTime() {
        when(readerRepository.findById(1L)).thenReturn(Optional.of(testReader));
        when(loanRepository.findByReaderIdAndReturnDateIsNull(1L)).thenReturn(Collections.emptyList());

        LocalDate today = LocalDate.now();
        Loan loan1 = new Loan(null, testReader, today.minusDays(20), today.minusDays(6));
        loan1.setReturnDate(today.minusDays(10));

        Loan loan2 = new Loan(null, testReader, today.minusDays(15), today.minusDays(1));
        loan2.setReturnDate(today.minusDays(5));

        Loan loan3 = new Loan(null, testReader, today.minusDays(10), today.plusDays(4));
        loan3.setReturnDate(today.minusDays(2));

        when(loanRepository.findReturnedLoansSince(eq(1L), any(LocalDate.class)))
                .thenReturn(List.of(loan1, loan2, loan3));

        ReliabilityResponse response = readerService.checkReliability(1L);

        assertTrue(response.isReliable());
        assertEquals(5, response.getMaxAllowedLoans());
    }

    @Test
    void checkReliability_ShouldReturnNotReliable_WhenReaderHasLessThanThreeReturnedBooks() {
        when(readerRepository.findById(1L)).thenReturn(Optional.of(testReader));
        when(loanRepository.findByReaderIdAndReturnDateIsNull(1L)).thenReturn(Collections.emptyList());

        when(loanRepository.findReturnedLoansSince(eq(1L), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        ReliabilityResponse response = readerService.checkReliability(1L);

        assertFalse(response.isReliable());
        assertEquals(2, response.getMaxAllowedLoans());
    }
}