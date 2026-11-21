package com.example.smartlibrary.service;

import com.example.smartlibrary.dto.IssueLoanRequest;
import com.example.smartlibrary.dto.LoanResponse;
import com.example.smartlibrary.dto.ReliabilityResponse;
import com.example.smartlibrary.model.Book;
import com.example.smartlibrary.model.Loan;
import com.example.smartlibrary.model.Reader;
import com.example.smartlibrary.repository.BookRepository;
import com.example.smartlibrary.repository.LoanRepository;
import com.example.smartlibrary.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final ReaderService readerService;

    @Transactional
    public LoanResponse issueLoan(IssueLoanRequest request) {
        Reader reader = readerRepository.findById(request.getReaderId())
                .orElseThrow(() -> new IllegalArgumentException("Читача з ID " + request.getReaderId() + " не знайдено"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Книгу з ID " + request.getBookId() + " не знайдено"));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Книга вже видана і недоступна");
        }

        ReliabilityResponse reliability = readerService.checkReliability(reader.getId());
        long activeCount = loanRepository.countByReaderIdAndReturnDateIsNull(reader.getId());

        if (activeCount >= reliability.getMaxAllowedLoans()) {
            throw new IllegalStateException("Перевищено ліміт позик. Максимум дозволено: " + reliability.getMaxAllowedLoans());
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(book, reader, today, today.plusDays(14));

        book.setAvailable(false);
        bookRepository.save(book);

        return new LoanResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Позику не знайдено"));

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("Книгу за цією позикою вже повернуто");
        }

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return new LoanResponse(loanRepository.save(loan));
    }

    public List<LoanResponse> getActiveLoansByReader(Long readerId) {
        if (!readerRepository.existsById(readerId)) {
            throw new IllegalArgumentException("Читача не знайдено");
        }

        return loanRepository.findByReaderIdAndReturnDateIsNull(readerId)
                .stream()
                .map(LoanResponse::new)
                .toList();
    }
}