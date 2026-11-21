package com.example.smartlibrary.dto;

import com.example.smartlibrary.model.Loan;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanResponse {
    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private Long readerId;
    private String readerName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean active;

    public LoanResponse(Loan loan) {
        this.loanId = loan.getId();
        this.bookId = loan.getBook().getId();
        this.bookTitle = loan.getBook().getTitle();
        this.readerId = loan.getReader().getId();
        this.readerName = loan.getReader().getFullName();
        this.issueDate = loan.getIssueDate();
        this.dueDate = loan.getDueDate();
        this.returnDate = loan.getReturnDate();
        this.active = loan.getReturnDate() == null;
    }
}