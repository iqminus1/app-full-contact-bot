package uz.pdp.appfullcontactbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appfullcontactbot.model.Transaction;
import uz.pdp.appfullcontactbot.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionRepository transactionRepository;

    @GetMapping("/read-all")
    public ResponseEntity<?> readAll() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        return ResponseEntity.ok(transactionRepository.findById(id).orElseThrow());
    }

    @GetMapping("/read-all-by-user-id/{userId}")
    public ResponseEntity<?> readAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionRepository.findAllByUserId(userId));
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthly(@RequestParam int year, @RequestParam int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);
        List<Transaction> result = transactionRepository.findAllByYearAndMonth(startDate, endDate);
        return ResponseEntity.ok(result);
    }

}
