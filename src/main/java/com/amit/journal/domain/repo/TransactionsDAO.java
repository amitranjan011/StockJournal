package com.amit.journal.domain.repo;

import com.amit.journal.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsDAO {


    List<Transaction> getTransactions(String symbol, LocalDate startDate, LocalDate endDate);

    List<Transaction> getAllTransactions();

    void deleteAllByBatchId(String batchId);
}