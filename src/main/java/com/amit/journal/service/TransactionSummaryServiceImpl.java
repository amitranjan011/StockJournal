package com.amit.journal.service;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.constants.Constants;
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
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        Comparator<TransactionSummary> comparatorByReturnPct = Comparator.comparing(TransactionSummary::getUnrealizedProfitPct).thenComparing(TransactionSummary::getPctReturn);
        summaryList.sort(comparatorByReturnPct);
        TransactionKPI transactionKPI = transactionKPIService.generateKPI(summaryList);

//        populateLastTradingPrice(summaryList);
        holder.setSummaryList(summaryList);
        holder.setTransactionKPI(transactionKPI);
        holder.setUserId(UserContext.getUserId());
        holder.setLastUpdate(LocalDate.now());
        return holder;
    }
    private double getLastTradingPrice(String symbol) {
        double stockPrice = -1;
        try {
            Stock stock = YahooFinance.get(symbol);
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
        double price = getLastTradingPrice(symbol + Constants.BSE_EXTENSION);
        if (price < 0) {
            price = getLastTradingPrice(symbol + Constants.NSE_EXTENSION);
        }
        return price;
    }

    private CompletableFuture<String> updateSummary(TransactionSummary summary) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            UserContext.setUserId(summary.getUserId());
            LOG.info("processing TransactionSummary : {}",summary);
            return populateAdditionalData(summary);
        }).thenApply(transactionSummary -> {
            return updateSummaryToDB(summary);
        }).exceptionally(exception -> {
            LOG.error("Exception for TransactionSummary : {}", summary);
            LOG.error("Exception : {}", CommonUtil.getStackTrace(exception));
            return null;
        });

        return future;
    }

    private TransactionSummary populateAdditionalData(TransactionSummary summary) {
        double latestPrice = getLastTradingPrice(summary.getInternalSymbol());
        if (latestPrice < 0) {
            updateSymbolForNSE(summary);
            latestPrice = getLastTradingPrice(summary.getInternalSymbol());
        }
        summary.setLastTradingPrice(latestPrice);
        if (summary.getUnsoldQty() > 0) {
            double unsoldLatestValue = summary.getUnsoldQty() * latestPrice;
            double unsoldBuyValue = summary.getUnsoldQty() * summary.getBuyPrice();
            double unrealizedProfit = unsoldLatestValue - unsoldBuyValue;
            double unrealizedProfitPct = (unrealizedProfit/unsoldBuyValue) * 100;

            summary.setUnrealizedProfit(unrealizedProfit);
            summary.setUnrealizedProfitPct(unrealizedProfitPct);
        } else {
            summary.setUnrealizedProfit(0);
            summary.setUnrealizedProfitPct(Constants.RETURN_PERCENT_UNSOLD_UNREALISED);
        }
        if (summary.getBuyQuantity() == 0) {
            summary.setPctReturn(0);
            summary.setProfit(0);
            summary.setPositionStatus(Constants.POSITION_STATUS_CLOSED);
            summary.setSellDate(LocalDate.now());

        }
        return summary;
    }

    private void updateSymbolForNSE(TransactionSummary summary) {
        summary.setInternalSymbol(summary.getSymbol() + Constants.NSE_EXTENSION);
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
}
