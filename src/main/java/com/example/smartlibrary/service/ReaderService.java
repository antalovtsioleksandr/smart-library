package com.example.smartlibrary.service;

import com.example.smartlibrary.dto.ReliabilityResponse;
import com.example.smartlibrary.model.Loan;
import com.example.smartlibrary.model.Reader;
import com.example.smartlibrary.repository.LoanRepository;
import com.example.smartlibrary.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public Reader createReader(Reader reader) {
        if (readerRepository.existsByEmail(reader.getEmail())) {
            throw new IllegalArgumentException("Читач з таким email вже зареєстрований: " + reader.getEmail());
        }
        return readerRepository.save(reader);
    }

    public ReliabilityResponse checkReliability(Long readerId) {
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("Читача не знайдено"));

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        List<Loan> activeLoans = loanRepository.findByReaderIdAndReturnDateIsNull(readerId);
        boolean hasOverdueActive = activeLoans.stream()
                .anyMatch(loan -> loan.getDueDate().isBefore(today));

        List<Loan> returnedRecently = loanRepository.findReturnedLoansSince(readerId, thirtyDaysAgo);

        boolean returnedAtLeastThree = returnedRecently.size() >= 3;
        boolean allReturnedOnTime = returnedRecently.stream()
                .allMatch(loan -> !loan.getReturnDate().isAfter(loan.getDueDate()));

        boolean isReliable = !hasOverdueActive && returnedAtLeastThree && allReturnedOnTime;
        int maxAllowed = isReliable ? 5 : 2;

        return new ReliabilityResponse(
                reader.getId(),
                isReliable,
                activeLoans.size(),
                returnedRecently.size(),
                maxAllowed
        );
    }
}