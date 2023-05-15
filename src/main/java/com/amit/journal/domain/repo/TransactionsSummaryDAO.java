package com.amit.journal.domain.repo;

import com.amit.journal.model.TransactionSummary;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsSummaryDAO {
    TransactionSummary findBySymbolAndOpen(String symbol);

    List<TransactionSummary> getSummaryRecords(String symbol, LocalDate startDate, LocalDate endDate);

    List<TransactionSummary> getAllRecords();

    List<TransactionSummary> findAllBySymbolsAndOpen(List<String> symbols);

    TransactionSummary getSingleSummaryRecord(int limit);

    void deleteAllSummaryRecordsForUser();

    TransactionSummary getLatestRecordFromHistory();

    List<TransactionSummary> getSummaryRecordsByType(String positionStatus);
}
