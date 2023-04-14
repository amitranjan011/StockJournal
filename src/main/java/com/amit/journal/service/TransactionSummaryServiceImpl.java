package com.amit.journal.service;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.constants.Constants;
import com.amit.journal.domain.repo.TransactionsSummaryDAOImpl;
import com.amit.journal.interceptor.UserContext;
import com.amit.journal.model.*;
import com.amit.journal.service.util.TransactionSummaryServiceUtil;
import com.amit.journal.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class TransactionSummaryServiceImpl implements TransactionSummaryService {
    private static final Logger LOG = LogManager.getLogger(TransactionSummaryServiceImpl.class);

    @Autowired
    private TransactionsSummaryDAOImpl transactionsSummaryDAO;

    @Autowired
    private TransactionKPIService transactionKPIService;
    @Autowired
    private SymbolMapperService symbolMapperService;

    @Override
    public void processTransactions(List<Transaction> transactions) {
        List<CompletableFuture<String>> futures = transactions.stream()
                .filter(transaction -> !CommonUtil.isObjectNullOrEmpty(transaction))
                .map(this::execute)
                .collect(Collectors.toList());
        LOG.info("Transactions  processed");
        try {
            setTimerForDataUpdate();
        } catch (Exception e) {
            LOG.error("Exception : {}", CommonUtil.getStackTrace(e));
        }
    }
    private CompletableFuture<String> execute(Transaction transactionEntry) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
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

        return future;
    }

    private TransactionSummary mapToTransactionSummary(Transaction transactionEntry) {
        return TransactionSummaryServiceUtil.mapToTransactionSummary(transactionEntry);
    }
    private String processTransactionSummary(TransactionSummary transactionSummary) {
        TransactionSummary transactionSummaryDB = transactionsSummaryDAO.findBySymbolAndOpen(transactionSummary.getSymbol());
        LOG.debug("transactionSummaryDB in db is : {}", transactionSummaryDB);
        if (CommonUtil.isObjectNullOrEmpty(transactionSummaryDB)) {
            SymbolMapping symbolMapping = symbolMapperService.getMapping(transactionSummary.getSymbol());
            String mappedSymbol = "";
            if (symbolMapping != null) {
                mappedSymbol = symbolMapping.getMappedSymbol();
                transactionSummaryDB = transactionsSummaryDAO.findBySymbolAndOpen(mappedSymbol);
            }
        }
        if (CommonUtil.isObjectNullOrEmpty(transactionSummaryDB)) {
            addSummaryToDB(transactionSummary); // new entry
        } else {
            String id = transactionSummaryDB.getId();
//            copyTranSummaryToHistory(transactionSummaryDB);
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
        try {
            TransactionSummaryKPIHolder holder = new TransactionSummaryKPIHolder();
            List<TransactionSummary> summaryList = getSummaryRecords(symbol, startDate, endDate);
            Comparator<TransactionSummary> comparatorByReturnPct = Comparator.comparing(TransactionSummary::getUnrealizedProfitPct).thenComparing(TransactionSummary::getPctReturn);
            summaryList.sort(comparatorByReturnPct);
            TransactionKPI transactionKPI = transactionKPIService.generateKPI(summaryList);

//        populateLastTradingPrice(summaryList);
            holder.setSummaryList(summaryList);
            holder.setTransactionKPI(transactionKPI);
            holder.setUserId(UserContext.getUserId());
            holder.setLastUpdate(LocalDateTime.now());
            return holder;
        } catch (Exception e) {
            LOG.error("Exception : {}", CommonUtil.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateAdditionalInfo() {
        List<TransactionSummary> summaryList = transactionsSummaryDAO.getAllRecords();
        List<CompletableFuture<String>> futures = summaryList.stream()
                .map(this::updateSummary)
                .collect(Collectors.toList());

        LOG.info("Updating LTP and related details completed now...");
    }

    @Override
    public double getLatestPrice(String symbol) {
        double price = TransactionSummaryServiceUtil.getLastTradingPrice(symbol + Constants.BSE_EXTENSION);
        if (price < 0) {
            price = TransactionSummaryServiceUtil.getLastTradingPrice(symbol + Constants.NSE_EXTENSION);
        }
        return price;
    }

    private CompletableFuture<String> updateSummary(TransactionSummary summary) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            UserContext.setUserId(summary.getUserId());
            LOG.info("processing TransactionSummary : {}",summary);
            return TransactionSummaryServiceUtil.populateAdditionalData(summary);
        }).thenApply(transactionSummary -> {
            return updateSummaryToDB(summary);
        }).exceptionally(exception -> {
            LOG.error("Exception for TransactionSummary : {}", summary);
            LOG.error("Exception : {}", CommonUtil.getStackTrace(exception));
            return null;
        });

        return future;
    }
    private void setTimerForDataUpdate() {
        LOG.info("Updating LTP and related details ...");
        String userId = UserContext.getUserId();
        Timer timer = new Timer("UPDATE_LTP_DETAILS_" + CommonUtil.getTodayDateString());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    UserContext.setUserId(userId);
                    updateAdditionalInfo();
                } catch (Exception e) {
                    LOG.error("Exception : {}", CommonUtil.getStackTrace(e));
                }
            }
        };

        int minutes = 1;
        long delay = (1000 * 60 * minutes);
        timer.schedule(timerTask, delay);
    }

    @Override
    public void updateSummaryForSymbolMap(String symbol, String mappedSymbol) {
        try {
            List<String> symbols = new ArrayList<>();
            symbols.add(symbol);
            SymbolMapping symbolMap = symbolMapperService.getMapping(symbol);
            if (!CommonUtil.isNullOrEmpty(mappedSymbol)) symbols.add(mappedSymbol);
            if (CommonUtil.isObjectNullOrEmpty(symbolMap) &&
                    (!CommonUtil.isNullOrEmpty(symbol) && !CommonUtil.isNullOrEmpty(mappedSymbol))) {
                symbolMapperService.saveMapping(symbol, mappedSymbol);
            }
            List<TransactionSummary> results = transactionsSummaryDAO.findAllBySymbolsAndOpen(symbols);
            if (!CommonUtil.isObjectNullOrEmpty(results) && results.size() > 0) {
//                Comparator<TransactionSummary> comparator = Comparator.comparing(TransactionSummary::getEntryDate);
//                Collections.sort(results, comparator);

                TransactionSummary firstSummary = results.get(0);
                List<TransactionSummary> others = results.subList(1, results.size());
                others.forEach(newSummary -> TransactionSummaryServiceUtil.aggregateSummary(newSummary, firstSummary));
                updateSummaryToDB(firstSummary);

                //others.forEach(summary -> transactionsSummaryDAO.delete(summary, CollectionsName.TRANSACTIONS_SUMMARY));

            }
        } catch (Exception exception) {
            LOG.error("Exception updating data for mapped symbol : {}, {}", symbol, CommonUtil.getStackTrace(exception));
        }
    }

    @Override
    public void copySummaryToHistory() {
        List<TransactionSummary> summaryList = transactionsSummaryDAO.getAllRecords();
        summaryList.forEach(summary -> copyTranSummaryToHistory(summary));
    }

    @Override
    public void updateSummaryBatchId(String batchId) {
        List<TransactionSummary> summaryList = transactionsSummaryDAO.getAllRecords();
        summaryList.forEach(summary -> {
            summary.setBatchId(batchId);
            updateSummaryToDB(summary);
        });
    }

    @Override
    public void updateSummaryBatchId() {
        String batchId = UUID.randomUUID().toString();
        updateSummaryBatchId(batchId);
    }
}
