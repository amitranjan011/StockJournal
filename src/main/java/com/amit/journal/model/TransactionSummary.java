package com.amit.journal.model;

import com.amit.journal.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSummary extends UserBase {
    @Id
    private String id;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate sellDate;

    private int daysHeld;
    private String symbol;
    private String internalSymbol;

    private int buyQuantity;
    private int sellQuantity;
    private int unsoldQty;

    private double unrealizedProfit;
    private double unrealizedProfitPct;
    private double buyPrice;
    private double sellPrice;
    private double lastTradingPrice;
    private double buyValue;
    private double sellValue;
    private double stopLoss;
    private String strategy;
    private String comments;
    private String name;
    private double profit;
    private double pctReturn;

    private double totalCurrValue;

    private String action;
    private String positionStatus = "OPEN";
    private List<TransactionBasic> transList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getSellDate() {
        return sellDate;
    }

    public void setSellDate(LocalDate sellDate) {
        this.sellDate = sellDate;
    }

    public int getDaysHeld() {
        return daysHeld;
    }

    public void setDaysHeld(int daysHeld) {
        this.daysHeld = daysHeld;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public int getSellQuantity() {
        return this.sellQuantity;
    }

    public void setSellQuantity(int sellQuantity) {
        this.sellQuantity = sellQuantity;
    }

    public int getUnsoldQty() {
        setTotalCurrValue(getLastTradingPrice() * unsoldQty);
        return unsoldQty;
    }

    public void setUnsoldQty(int unsoldQty) {
        this.unsoldQty = unsoldQty;
        setTotalCurrValue(getLastTradingPrice() * unsoldQty);
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = CommonUtil.round(buyPrice, 2);
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = CommonUtil.round(sellPrice, 2);
    }

    public double getLastTradingPrice() {
//        setTotalCurrValue(lastTradingPrice * getUnsoldQty());
        return lastTradingPrice;
    }

    public void setLastTradingPrice(double lastTradingPrice) {
        this.lastTradingPrice = CommonUtil.round(lastTradingPrice, 2);
        setTotalCurrValue(lastTradingPrice * getUnsoldQty());
    }

    public double getBuyValue() {
        return buyValue;
    }

    public void setBuyValue(double buyValue) {
        this.buyValue = CommonUtil.round(buyValue, 2);
    }

    public double getSellValue() {
        return sellValue;
    }

    public void setSellValue(double sellValue) {
        this.sellValue = CommonUtil.round(sellValue, 2);
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = CommonUtil.round(profit, 2);
    }

    public double getPctReturn() {
        return CommonUtil.round(pctReturn, 1);
    }

    public void setPctReturn(double pctReturn) {
        this.pctReturn = CommonUtil.round(pctReturn, 1);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    public List<TransactionBasic> getTransList() {
        if (this.transList == null) this.transList = new ArrayList<>();
        return transList;
    }

    public void setTransList(List<TransactionBasic> transList) {
        this.transList = transList;
    }

    public String getInternalSymbol() {
        return internalSymbol;
    }

    public void setInternalSymbol(String internalSymbol) {
        this.internalSymbol = internalSymbol;
    }

    public double getUnrealizedProfit() {
        return unrealizedProfit;
    }

    public void setUnrealizedProfit(double unrealizedProfit) {
        this.unrealizedProfit = CommonUtil.round(unrealizedProfit, 2);
    }

    public double getUnrealizedProfitPct() {
        return unrealizedProfitPct;
    }

    public void setUnrealizedProfitPct(double unrealizedProfitPct) {
        this.unrealizedProfitPct = CommonUtil.round(unrealizedProfitPct, 1);
    }

    public double getTotalCurrValue() {
        return totalCurrValue;
    }

    public void setTotalCurrValue(double totalCurrValue) {
        this.totalCurrValue = CommonUtil.round(totalCurrValue, 2);
    }

    @Override
    public String toString() {
        return "TransactionSummary{" +
                "id='" + id + '\'' +
                ", entryDate=" + entryDate +
                ", sellDate=" + sellDate +
                ", daysHeld=" + daysHeld +
                ", symbol='" + symbol + '\'' +
                ", internalSymbol='" + internalSymbol + '\'' +
                ", buyQuantity=" + buyQuantity +
                ", sellQuantity=" + sellQuantity +
                ", unsoldQty=" + unsoldQty +
                ", unrealizedProfit=" + unrealizedProfit +
                ", unrealizedProfitPct=" + unrealizedProfitPct +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", lastTradingPrice=" + lastTradingPrice +
                ", buyValue=" + buyValue +
                ", sellValue=" + sellValue +
                ", stopLoss=" + stopLoss +
                ", strategy='" + strategy + '\'' +
                ", comments='" + comments + '\'' +
                ", name='" + name + '\'' +
                ", profit=" + profit +
                ", pctReturn=" + pctReturn +
                ", totalCurrValue=" + totalCurrValue +
                ", action='" + action + '\'' +
                ", positionStatus='" + positionStatus + '\'' +
                ", transList=" + transList +
                '}';
    }
}
