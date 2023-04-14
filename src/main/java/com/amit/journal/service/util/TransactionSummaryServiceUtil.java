package com.amit.journal.service.util;

import com.amit.journal.constants.Constants;
import com.amit.journal.model.Transaction;
import com.amit.journal.model.TransactionBasic;
import com.amit.journal.model.TransactionSummary;
import com.amit.journal.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class TransactionSummaryServiceUtil {
    private static final Logger LOG = LogManager.getLogger(TransactionSummaryServiceUtil.class);
    public static void updateBuySellData(Transaction transactionEntry, TransactionSummary transactionSummary) {
        if (transactionEntry.getTransactionType().equals(Constants.TRANSACTION_TYPE_BUY)) {
            transactionSummary.setBuyQuantity(transactionEntry.getQuantity());
            transactionSummary.setBuyPrice(transactionEntry.getPrice());
            transactionSummary.setBuyValue(transactionSummary.getBuyQuantity() * transactionSummary.getBuyPrice());
            transactionSummary.setEntryDate(transactionEntry.getTransactionDate());
            transactionSummary.setUnsoldQty(transactionEntry.getQuantity());
            transactionSummary.setSellDate(null);
            transactionSummary.setPctReturn(Constants.RETURN_PERCENT_UNSOLD_UNREALISED);
        } else {
            transactionSummary.setSellQuantity(Math.abs(transactionEntry.getQuantity()));
            transactionSummary.setSellPrice(transactionEntry.getPrice());
            transactionSummary.setSellValue(transactionSummary.getSellQuantity() * transactionSummary.getSellPrice());
            transactionSummary.setSellDate(transactionEntry.getTransactionDate());
        }
    }

    public static TransactionSummary aggregateSummary(TransactionSummary tranSummaryNew, TransactionSummary tranSummaryDB) {
        if (tranSummaryNew.getBuyQuantity() > 0) { //if buy
            tranSummaryDB.setBuyQuantity(tranSummaryDB.getBuyQuantity() + tranSummaryNew.getBuyQuantity());
            tranSummaryDB.setBuyValue(tranSummaryDB.getBuyValue() + tranSummaryNew.getBuyValue());
            tranSummaryDB.setBuyPrice(tranSummaryDB.getBuyValue() / tranSummaryDB.getBuyQuantity());
            tranSummaryDB.setUnsoldQty(tranSummaryDB.getUnsoldQty() + tranSummaryNew.getBuyQuantity());
        } else { // if sell
            tranSummaryDB.setSellQuantity(tranSummaryDB.getSellQuantity() + tranSummaryNew.getSellQuantity());
            tranSummaryDB.setSellValue(tranSummaryDB.getSellValue() + tranSummaryNew.getSellValue());
            tranSummaryDB.setUnsoldQty(tranSummaryDB.getUnsoldQty() - tranSummaryNew.getSellQuantity());
            tranSummaryDB.setSellPrice(tranSummaryDB.getSellValue() / tranSummaryDB.getSellQuantity());
            tranSummaryDB.setSellDate(tranSummaryNew.getSellDate());

            if (tranSummaryDB.getUnsoldQty() == 0) tranSummaryDB.setPositionStatus(Constants.POSITION_STATUS_CLOSED);

//            Period period = Period.between(tranSummaryDB.getEntryDate(), tranSummaryDB.getSellDate());
            tranSummaryDB.setDaysHeld(getDays(tranSummaryDB));
        }
        tranSummaryDB.setBatchId(tranSummaryNew.getBatchId());
        if (tranSummaryNew.getStopLoss() != 0) tranSummaryDB.setStopLoss(tranSummaryNew.getStopLoss());
        if (!CommonUtil.isNullOrEmpty(tranSummaryNew.getStrategy())) tranSummaryDB.setStrategy(tranSummaryNew.getStrategy());
        if (!CommonUtil.isNullOrEmpty(tranSummaryNew.getComments())) tranSummaryDB.setComments(tranSummaryNew.getComments());
        if (!CommonUtil.isNullOrEmpty(tranSummaryNew.getAction())) tranSummaryDB.setAction(tranSummaryNew.getAction());
        tranSummaryDB.getTransList().addAll(tranSummaryNew.getTransList());
        updateProfit(tranSummaryDB);
        return tranSummaryDB;
    }
    private static int getDays(TransactionSummary tranSummary) {
        if (!CommonUtil.isObjectNullOrEmpty(tranSummary.getEntryDate()) && !CommonUtil.isObjectNullOrEmpty(tranSummary.getSellDate())) {
            Period period = Period.between(tranSummary.getEntryDate(), tranSummary.getSellDate());
            return period.getDays();
        }
        return 0;
    }

    public static void updateProfit(TransactionSummary tranSummaryDB) {
        int buyQty = tranSummaryDB.getBuyQuantity();
        int sellQty = tranSummaryDB.getSellQuantity();
        double buyPrice = tranSummaryDB.getBuyPrice();
        double sellPrice = tranSummaryDB.getSellPrice();
        double buyVal = sellQty * tranSummaryDB.getBuyPrice();
        double sellValue = sellQty * tranSummaryDB.getSellPrice();
        double profit = sellValue - buyVal;
        double percentProfit = 0;
        if (sellPrice == 0d) {
            LOG.info("******* sellPrice: {}, buyPrice : {}, Setting percent profit to arbitrary number : {}", percentProfit);
            percentProfit = Constants.RETURN_PERCENT_UNSOLD_UNREALISED;
        } else {
            percentProfit = ((sellPrice - buyPrice) / buyPrice) * 100;
            LOG.info("******* sellPrice: {}, buyPrice : {}, percent profit temp is : {}", percentProfit);
        }
        tranSummaryDB.setPctReturn(percentProfit);
        tranSummaryDB.setProfit(profit);
    }

    public static TransactionSummary mapToTransactionSummary(Transaction transactionEntry) {
        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setBatchId(transactionEntry.getBatchId());
        transactionSummary.setName(transactionEntry.getName());
        transactionSummary.setSymbol(transactionEntry.getSymbol());
        transactionSummary.setInternalSymbol((transactionEntry.getSymbol() + Constants.BSE_EXTENSION));
        transactionSummary.setComments(transactionEntry.getComments());
        transactionSummary.setStrategy(transactionEntry.getStrategy());
        transactionSummary.setAction(transactionEntry.getAction());
        transactionSummary.setStopLoss(transactionEntry.getStopLoss());
        transactionSummary.setPositionStatus(Constants.POSITION_STATUS_OPEN);
        transactionSummary.setDaysHeld(0); // update while aggregation
        transactionSummary.setLastTradingPrice(transactionEntry.getLastTradingPrice()); // update while fetching data
        transactionSummary.setPctReturn(0); // update while aggregation
        transactionSummary.setProfit(0); // update while aggregation
        transactionSummary.setUnsoldQty(0); // update while aggregation
        transactionSummary.setSellDate(null);
        TransactionBasic transactionBasic = getTransBasicData(transactionEntry);
        transactionSummary.getTransList().add(transactionBasic);
        TransactionSummaryServiceUtil.updateBuySellData(transactionEntry, transactionSummary);
        return transactionSummary;
    }

    private static TransactionBasic getTransBasicData(Transaction transactionEntry) {
        TransactionBasic transactionBasic = new TransactionBasic();
        transactionBasic.setPrice(transactionEntry.getPrice());
        transactionBasic.setQuantity(transactionEntry.getQuantity());
        transactionBasic.setStopLoss(transactionEntry.getStopLoss());
        transactionBasic.setTotalValue(transactionEntry.getTotalValue());
        transactionBasic.setTransactionDate(transactionEntry.getTransactionDate());
        transactionBasic.setTransactionType(transactionEntry.getTransactionType());
        return transactionBasic;
    }

    public static double getLastTradingPrice(String symbol) {
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
    public static TransactionSummary populateAdditionalData(TransactionSummary summary) {
        double latestPrice = TransactionSummaryServiceUtil.getLastTradingPrice(summary.getInternalSymbol());
        if (latestPrice < 0) {
            updateSymbolForNSE(summary);
            latestPrice = TransactionSummaryServiceUtil.getLastTradingPrice(summary.getInternalSymbol());
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

    private static void updateSymbolForNSE(TransactionSummary summary) {
        summary.setInternalSymbol(summary.getSymbol() + Constants.NSE_EXTENSION);
    }
}
