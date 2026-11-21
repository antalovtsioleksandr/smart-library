package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByReaderIdAndReturnDateIsNull(Long readerId);

    long countByReaderIdAndReturnDateIsNull(Long readerId);

    @Query("SELECT l FROM Loan l WHERE l.reader.id = :readerId AND l.returnDate IS NOT NULL AND l.returnDate >= :startDate")
    List<Loan> findReturnedLoansSince(@Param("readerId") Long readerId, @Param("startDate") LocalDate startDate);
}