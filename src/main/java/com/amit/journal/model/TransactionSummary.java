package com.amit.journal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

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

    private int buyQuantity;
    private int sellQuantity;
    private int unsoldQty;
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

    private String action;
    private String positionStatus = "OPEN";

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
        return unsoldQty;
    }

    public void setUnsoldQty(int unsoldQty) {
        this.unsoldQty = unsoldQty;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getLastTradingPrice() {
        return lastTradingPrice;
    }

    public void setLastTradingPrice(double lastTradingPrice) {
        this.lastTradingPrice = lastTradingPrice;
    }

    public double getBuyValue() {
        return buyValue;
    }

    public void setBuyValue(double buyValue) {
        this.buyValue = buyValue;
    }

    public double getSellValue() {
        return sellValue;
    }

    public void setSellValue(double sellValue) {
        this.sellValue = sellValue;
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
        this.profit = profit;
    }

    public double getPctReturn() {
        return pctReturn;
    }

    public void setPctReturn(double pctReturn) {
        this.pctReturn = pctReturn;
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

    @Override
    public String toString() {
        return "TransactionSummary{" +
                "id='" + id + '\'' +
                ", entryDate=" + entryDate +
                ", sellDate=" + sellDate +
                ", daysHeld=" + daysHeld +
                ", symbol='" + symbol + '\'' +
                ", buyQuantity=" + buyQuantity +
                ", sellQuantity=" + sellQuantity +
                ", unsoldQty=" + unsoldQty +
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
                ", action='" + action + '\'' +
                ", positionStatus='" + positionStatus + '\'' +
                '}';
    }
}
