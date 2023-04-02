package com.amit.journal.service.util;

import com.amit.journal.constants.Constants;
import com.amit.journal.model.Transaction;
import com.amit.journal.model.TransactionBasic;
import com.amit.journal.model.TransactionSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            transactionSummary.setPctReturn(Constants.HOLDING_UNSOLD_RETURN_PERCENT);
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

            Period period = Period.between(tranSummaryDB.getEntryDate(), tranSummaryDB.getSellDate());
            tranSummaryDB.setDaysHeld(period.getDays());
        }
        tranSummaryDB.getTransList().addAll(tranSummaryNew.getTransList());
        updateProfit(tranSummaryDB);
        return tranSummaryDB;
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
            percentProfit = Constants.HOLDING_UNSOLD_RETURN_PERCENT;
        } else {
            percentProfit = ((sellPrice - buyPrice) / buyPrice) * 100;
            LOG.info("******* sellPrice: {}, buyPrice : {}, percent profit temp is : {}", percentProfit);
        }
        tranSummaryDB.setPctReturn(percentProfit);
        tranSummaryDB.setProfit(profit);
    }

    public static TransactionSummary mapToTransactionSummary(Transaction transactionEntry) {
        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setName(transactionEntry.getName());
        transactionSummary.setSymbol(transactionEntry.getSymbol());
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
}
