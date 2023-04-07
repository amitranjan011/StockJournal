package com.amit.journal.model;

import com.amit.journal.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

public class Holding extends UserBase {
    @Id
    private String id;
    private List<HoldingItem> entries;
    private double totalBuyValue;
    private double totalCurrValue;
    private double profit;
    private double profitPct;

    private double cash;
    private double totalPortfolioValue;
    private double dayChange;
    private double dayChgPct;
    private String dateStr;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    public List<HoldingItem> getEntries() {
        return entries;
    }

    public void setEntries(List<HoldingItem> entries) {
        this.entries = entries;
    }

    public double getTotalBuyValue() {
        return totalBuyValue;
    }

    public void setTotalBuyValue(double totalBuyValue) {
        this.totalBuyValue = CommonUtil.round(totalBuyValue, 2);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTotalCurrValue() {
        return totalCurrValue;
    }

    public void setTotalCurrValue(double totalCurrValue) {
        this.totalCurrValue = CommonUtil.round(totalCurrValue, 2);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = CommonUtil.round(profit, 2);
    }

    public double getProfitPct() {
        return profitPct;
    }

    public void setProfitPct(double profitPct) {
        this.profitPct = CommonUtil.round(profitPct, 1);
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = CommonUtil.round(cash, 2);
    }

    public double getTotalPortfolioValue() {
        return totalPortfolioValue;
    }

    public void setTotalPortfolioValue(double totalPortfolioValue) {
        this.totalPortfolioValue = CommonUtil.round(totalPortfolioValue, 2);
    }
    public void setTotalPortfolioValue() {
        this.totalPortfolioValue = CommonUtil.round((getTotalCurrValue() + getCash()), 2);
    }

    public double getDayChange() {
        return dayChange;
    }

    public void setDayChange(double dayChange) {
        this.dayChange = dayChange;
    }

    public double getDayChgPct() {
        return dayChgPct;
    }

    public void setDayChgPct(double dayChgPct) {
        this.dayChgPct = CommonUtil.round(dayChgPct, 1);
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    @Override
    public String toString() {
        return "Holding{" +
                "id='" + id + '\'' +
                ", entries=" + entries +
                ", totalBuyValue=" + totalBuyValue +
                ", totalCurrValue=" + totalCurrValue +
                ", profit=" + profit +
                ", profitPct=" + profitPct +
                ", cash=" + cash +
                ", totalPortfolioValue=" + totalPortfolioValue +
                ", dayChange=" + dayChange +
                ", dayChgPct=" + dayChgPct +
                ", dateStr='" + dateStr + '\'' +
                ", date=" + date +
                '}';
    }
}
