package com.amit.journal.service;

import com.amit.journal.model.Transaction;
import com.amit.journal.model.TransactionSummary;
import com.amit.journal.model.TransactionSummaryKPIHolder;

import java.time.LocalDate;
import java.util.List;

public interface TransactionSummaryService {

    void processTransactions(List<Transaction> transactions);


    List<TransactionSummary> getSummaryRecords(String symbol, LocalDate startDate, LocalDate endDate);

    TransactionSummaryKPIHolder getSummaryRecordsAndKPI(String symbol, LocalDate startDate, LocalDate endDate);

    void updateAdditionalInfo();

    double getLatestPrice(String symbol);
}
