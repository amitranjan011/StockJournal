package com.amit.journal.service;

import com.amit.journal.model.StockData;
import com.amit.journal.model.Transaction;
import com.amit.journal.model.TransactionSummary;
import com.amit.journal.model.TransactionSummaryKPIHolder;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

public interface TransactionSummaryService {

    // GET request
//    StockData getQuote(TransactionSummary summary);

    StockData getQuoteForInternalSymbol(String symbol);

    void processTransactions(List<Transaction> transactions);


    List<TransactionSummary> getSummaryRecords(String symbol, LocalDate startDate, LocalDate endDate);

    TransactionSummaryKPIHolder getSummaryRecordsAndKPI(String symbol, LocalDate startDate, LocalDate endDate);

    void updateAdditionalInfo();

    double getLatestPrice(String symbol);

    void updateSummaryForSymbolMap(String symbol, String mappedSymbol);

    void copySummaryToHistory();

    void updateSummaryBatchId(String batchId);

    void updateSummaryBatchId();

    void resetSummaryData();

    void updateStopLoss(Transaction transaction);

    void exportTransactionSummary(PrintWriter writer, String type);

    String testStockData(String symbol);
}
