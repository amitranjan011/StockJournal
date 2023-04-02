package com.amit.journal.service;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.domain.repo.TransactionsSummaryDAOImpl;
import com.amit.journal.interceptor.UserContext;
import com.amit.journal.model.Transaction;
import com.amit.journal.model.TransactionKPI;
import com.amit.journal.model.TransactionSummary;
import com.amit.journal.model.TransactionSummaryKPIHolder;
import com.amit.journal.service.util.TransactionSummaryServiceUtil;
import com.amit.journal.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Service
public class TransactionSummaryServiceImpl implements TransactionSummaryService {
    private static final Logger LOG = LogManager.getLogger(TransactionSummaryServiceImpl.class);

    @Autowired
    private TransactionsSummaryDAOImpl transactionsSummaryDAO;

    @Autowired
    private TransactionKPIService transactionKPIService;

    @Override
    public void processTransactions(List<Transaction> transactions) {
        List<CompletableFuture<String>> futures = transactions.stream()
                .filter(transaction -> !CommonUtil.isObjectNullOrEmpty(transaction))
                .map(this::execute)
                .collect(Collectors.toList());
        LOG.info("Transactions  processed");
    }
    private CompletableFuture<String> execute(Transaction transactionEntry) {
        return CompletableFuture.supplyAsync(() -> {
            UserContext.setUserId(transactionEntry.getUserId());
            LOG.info("processing transactionEntry : {}",transactionEntry);
            return mapToTransactionSummary(transactionEntry);
        }).thenApply(transactionSummary -> {
            return processTransactionSummary(transactionSummary);
        }).exceptionally(exception -> {
            LOG.error("Exception for transactionEntry : {}", transactionEntry);
            LOG.error("Exception : {}", CommonUtil.getStackTrace(exception));
            return null;
        });
    }

    private TransactionSummary mapToTransactionSummary(Transaction transactionEntry) {
        return TransactionSummaryServiceUtil.mapToTransactionSummary(transactionEntry);
    }
    private String processTransactionSummary(TransactionSummary transactionSummary) {
        TransactionSummary transactionSummaryDB = transactionsSummaryDAO.findBySymbolAnsOpen(transactionSummary.getSymbol());
        LOG.debug("transactionSummaryDB in db is : {}", transactionSummaryDB);
        if (CommonUtil.isObjectNullOrEmpty(transactionSummaryDB)) {
            addSummaryToDB(transactionSummary); // new entry
        } else {
            String id = transactionSummaryDB.getId();
            copyTranSummaryToHistory(transactionSummaryDB);
            transactionSummaryDB.setId(id);
            TransactionSummaryServiceUtil.aggregateSummary(transactionSummary, transactionSummaryDB);
            updateSummaryToDB(transactionSummaryDB);
        }
        return transactionSummary.getId();
    }
    private void copyTranSummaryToHistory(TransactionSummary transactionSummary) {
        LOG.info("transactionSummary copied to history : {}", transactionSummary);
        transactionSummary.setId(null);
        transactionsSummaryDAO.persist(transactionSummary, CollectionsName.TRANSACTIONS_SUMMARY_HISTORY);
    }

    private String addSummaryToDB(TransactionSummary transactionSummary) {
        transactionsSummaryDAO.persist(transactionSummary);
        return transactionSummary.getId();
    }
    private String updateSummaryToDB(TransactionSummary transactionSummary) {
        transactionsSummaryDAO.persist(transactionSummary);
        return transactionSummary.getId();
    }

    @Override
    public List<TransactionSummary> getSummaryRecords(String symbol, LocalDate startDate, LocalDate endDate) {
        return transactionsSummaryDAO.getSummaryRecords(symbol, startDate, endDate);
    }

    @Override
    public TransactionSummaryKPIHolder getSummaryRecordsAndKPI(String symbol, LocalDate startDate, LocalDate endDate) {
        TransactionSummaryKPIHolder holder = new TransactionSummaryKPIHolder();
        List<TransactionSummary> summaryList = getSummaryRecords(symbol, startDate, endDate);
        TransactionKPI transactionKPI = transactionKPIService.generateKPI(summaryList);

        populateLastTradingPrice(summaryList);
        holder.setSummaryList(summaryList);
        holder.setTransactionKPI(transactionKPI);
        holder.setUserId(UserContext.getUserId());
        holder.setLastUpdate(LocalDate.now());
        return holder;
    }

    private double populateLastTradingPrice(List<TransactionSummary> summaryList) {
        long start = System.currentTimeMillis();
        LOG.info("Started calculating LTP ================ START");
        summaryList.forEach(summary -> summary.setLastTradingPrice(populateLastTradingPrice(summary.getSymbol())));
        LOG.info("Completed calculating LTP ================ Time taken : {} ms", ( System.currentTimeMillis() - start));
        return 0;
    }
    private double populateLastTradingPrice(String symbol) {
        double stockPrice = -1;
        try {
            String yahooSymbol = symbol + ".BO";
            Stock stock = YahooFinance.get(yahooSymbol);
            if (stock != null) {
                BigDecimal price = stock.getQuote().getPrice();
                if (price != null) stockPrice = price.doubleValue();
            }
            LOG.info("Price for : {} is : {}", symbol, stockPrice);
        } catch (IOException e) {
            LOG.error("Exception fetching price for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(e));
        }
        return stockPrice;
    }
}
