package com.amit.journal.service.util;

import com.amit.journal.config.PropertyReader;
import com.amit.journal.constants.Constants;
import com.amit.journal.model.*;
import com.amit.journal.util.CommonUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.CrumbManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

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
            tranSummaryDB.setDaysHeld(getDays(tranSummaryDB));
        }
        tranSummaryDB.setBatchId(tranSummaryNew.getBatchId());
        if (tranSummaryNew.getStopLoss() > 0) tranSummaryDB.setStopLoss(tranSummaryNew.getStopLoss());
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
            LOG.info("******* symbol: {}, sellPrice: {}, buyPrice : {}, Setting percent profit to arbitrary number : {}",tranSummaryDB.getSymbol(), sellPrice, buyPrice, percentProfit);
            percentProfit = Constants.RETURN_PERCENT_UNSOLD_UNREALISED;
        } else {
            percentProfit = ((sellPrice - buyPrice) / buyPrice) * 100;
            LOG.info("******* symbol: {}, sellPrice: {}, buyPrice : {}, percent profit temp is : {}",tranSummaryDB.getSymbol(), sellPrice, buyPrice, percentProfit);
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

    public static Stock getStockData(String symbol) {
        try {
            Stock stock = getLastTradingData(symbol + Constants.NSE_EXTENSION);
            if (CommonUtil.isObjectNullOrEmpty(stock)) stock = getLastTradingData(symbol + Constants.BSE_EXTENSION);
            return stock;
        } catch (Exception e) {
            LOG.error("Exception fetching price for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(e));
        }
        return null;
    }
    public static StockData getStockDataOwn(String symbol) {
        try {
            StockData stock = getQuoteInternal(symbol + Constants.NSE_EXTENSION);
            if (CommonUtil.isObjectNullOrEmpty(stock)) stock = getQuoteInternal(symbol + Constants.BSE_EXTENSION);
            return stock;
        } catch (Exception e) {
            LOG.error("Exception fetching price for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(e));
        }
        return null;
    }
    public static StockData getLastTradingDataOwn(String intlSymbol) {
        try {
            StockData stock = getQuoteInternal(intlSymbol);
            return stock;
        } catch (Exception e) {
            LOG.error("Exception fetching price for : {}, exception : {}"
                    , intlSymbol, CommonUtil.getStackTrace(e));
        }
        return null;
    }
    public static Stock getLastTradingData(String symbol) {
        try {
            Stock stock = YahooFinance.get(symbol);
            return stock;
        } catch (IOException e) {
            LOG.error("Exception fetching price for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(e));
        }
        return null;
    }
    public static double getLatestStockPrice(Stock stock, String symbol) {
        if (stock == null) return -1;
        double stockPrice = -1;
        try {
            BigDecimal price = stock.getQuote().getPrice();
            if (price != null) stockPrice = price.doubleValue();
            LOG.info("Price for : {} is : {}", symbol, stockPrice);
        } catch (Exception ex) {
            LOG.error("Exception fetching price for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(ex));
        }
        return stockPrice;
    }
    public static double getLatestPe(Stock stock, String symbol) {
        if (stock == null) return -1;
        double stockPe = -1;
        try {
            BigDecimal pe = stock.getStats().getPe();
            if (pe != null) stockPe = pe.doubleValue();
            LOG.info("stockPe for : {} is : {}", symbol, stockPe);
        } catch (Exception ex) {
            LOG.error("Exception fetching stockPe for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(ex));
        }
        return stockPe;
    }

    public static String getName(Stock stock, String symbol) {
        try {
            if (stock != null) {
                return stock.getName();
            }
        } catch (Exception ex) {
            LOG.error("Exception fetching stockPe for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(ex));
        }
        return "";
    }
    public static TransactionSummary populateAdditionalData(TransactionSummary summary) {
        if (summary.getPositionStatus().equalsIgnoreCase(Constants.POSITION_STATUS_OPEN)) {
            updatePriceAndPEOwn(summary);
        }

        if (summary.getUnsoldQty() > 0) {
            double unsoldLatestValue = summary.getUnsoldQty() * summary.getLastTradingPrice();
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
        setStopLossIndicator(summary);
        return summary;
    }

    private static void updatePriceAndPEOwn(TransactionSummary summary) {
        StockData stock = getLastTradingDataOwn(summary.getInternalSymbol());

        if (CommonUtil.isObjectNullOrEmpty(stock)/*latestPrice < 0*/) {
            LOG.error("Exception fetching stock data for : {}", summary.getSymbol());
            stock = TransactionSummaryServiceUtil.getStockDataOwn(summary.getSymbol());
        }
        if (!CommonUtil.isObjectNullOrEmpty(stock)) {
            double latestPrice = stock.getPrice();
            summary.setLastTradingPrice(latestPrice);
            summary.setInternalSymbol(stock.getSymbol());
//            updatePE(summary, stock);
        }
    }
    private static void updatePriceAndPE(TransactionSummary summary) {
        Stock stock = getLastTradingData(summary.getInternalSymbol());

        if (CommonUtil.isObjectNullOrEmpty(stock)/*latestPrice < 0*/) {
            LOG.error("Exception fetching stock data for : {}", summary.getSymbol());
            stock = TransactionSummaryServiceUtil.getStockData(summary.getSymbol());

        }
        if (!CommonUtil.isObjectNullOrEmpty(stock)) {
            double latestPrice = TransactionSummaryServiceUtil.getLatestStockPrice(stock, summary.getSymbol());
            summary.setLastTradingPrice(latestPrice);
            summary.setInternalSymbol(stock.getSymbol());
            updatePE(summary, stock);
        }
    }
    private static void updatePE(TransactionSummary summary, Stock stock) {
        double pe = getLatestPe(stock, summary.getSymbol());

        if (summary.getPeData() == null) {
            PEData peData = new PEData();
            peData.setInitPe(pe);
            peData.setCurrentPe(pe);
            summary.setPeData(peData);
        } else {
            PEData peData = summary.getPeData();
            if (peData.getInitPe() == 0) peData.setInitPe(pe);
            if (peData.getCurrentPe() == 0) peData.setCurrentPe(pe);
        }
    }


    private static void updateSymbolForNSE(TransactionSummary summary) {
        summary.setInternalSymbol(summary.getSymbol() + Constants.NSE_EXTENSION);
    }

    public static void setStopLossIndicator(TransactionSummary summary) {
        if (summary.getPositionStatus().equalsIgnoreCase(Constants.POSITION_STATUS_CLOSED)) {
            summary.setStopLossAlert(false);
            return;
        }
        double ltp = summary.getLastTradingPrice();
        double stopPct = Constants.STOPLOSS_THRESHOLD_PCT;
        double thresholdPrice = ltp * stopPct;
        double stopPrice = summary.getStopLoss();
        if (ltp < stopPrice || thresholdPrice < stopPrice) {
            summary.setStopLossAlert(true);
        } else {
            summary.setStopLossAlert(false);
        }
    }
    public static StockData getQuoteInternalNew(String symbol) {
        StockData stockData = new StockData();
        try {
            Map<String, String> dataMap = YahooFinanceUtil.getCookie();

            System.setProperty("yahoofinance.crumb", dataMap.get(Constants.YAHOO_CRUMB));
            System.setProperty("yahoofinance.cookie", dataMap.get(Constants.YAHOO_COOKIE));
            Stock stock = getLastTradingData(symbol);
            stockData.setPrice(CommonUtil.getDoubleFromBigDecimal(stock.getQuote().getPrice()));
            stockData.setSymbol(stock.getSymbol());
        } catch (Exception e) {
            LOG.error("Exception fetching getQuoteInternal for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(e));
        }
        return stockData;
    }


    public static StockData getQuoteInternal(String symbol) {
        double price = -1;
        try {
            String url = YahooFinance.HISTQUOTES2_BASE_URL + symbol;
//            ResponseEntity<String> response = PropertyReader.getInstance().getRestTemplate().getForEntity(url, String.class);
            String quoteData = PropertyReader.getInstance().getRestTemplate().getForObject(url, String.class);
//            String quoteData = response.getBody();
            String [] lines = quoteData.split("\n");
            if (lines != null && lines.length == 2) {
                String dataLine = lines[1];
                String [] stockDataArr = dataLine.split(",");
                if(stockDataArr != null && stockDataArr.length > 4) {
                    String sPrice = stockDataArr[4];
                    price =CommonUtil.getDouble(sPrice);
                    if (price > 0) {
                        LOG.info("Quote for  symbol : {}, price : {}"
                                , symbol, price);
                        StockData stockData = new StockData();
                        stockData.setSymbol(symbol);
                        stockData.setPrice(price);
                        return stockData;
                    }
                }
            }
//            System.out.println(price);
        } catch (Exception e) {
            LOG.error("Exception fetching getQuoteInternal for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(e));
        }
        return null;
    }

    public static void getQuoteInternalNew(String symbol, String crumb) {
        double price = -1;
        StringBuilder url = new StringBuilder(YahooFinance.QUOTES_QUERY1V7_BASE_URL + "?symbols=");
        try {
             url.append(symbol).append("&crumb=").append(crumb);
//            ResponseEntity<String> response = PropertyReader.getInstance().getRestTemplate().getForEntity(url, String.class);
            Object quoteData = PropertyReader.getInstance().getRestTemplate().getForObject(url.toString(), Object.class);
//            String quoteData = response.getBody();

            System.out.println(quoteData);
        } catch (Exception e) {
            LOG.error("Exception fetching getQuoteInternal for : {}, exception : {}"
                    , symbol, CommonUtil.getStackTrace(e));
        }
    }

    public static String testStockData(String symbol) {
        try {
            String storedCrumb = System.getProperty("yahoofinance.crumb");
            if (CommonUtil.isNullOrEmpty(storedCrumb)) {
                Map<String, String> dataMap = YahooFinanceUtil.getCookie();
                storedCrumb = dataMap.get(Constants.YAHOO_CRUMB);
                System.setProperty("yahoofinance.crumb", storedCrumb);
                System.setProperty("yahoofinance.cookie", dataMap.get(Constants.YAHOO_COOKIE));
            }
//            String crumb = CrumbManager.getCrumb();
//            String cookie = CrumbManager.getCookie();
            getQuoteInternalNew(symbol, storedCrumb);
            return "";
        } catch (Exception e) {
            LOG.error("Exception while fetching crumb: {} : {}"
                    , ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}
