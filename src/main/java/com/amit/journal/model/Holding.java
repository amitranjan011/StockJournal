package com.amit.journal.model;

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
        this.totalBuyValue = totalBuyValue;
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
        this.totalCurrValue = totalCurrValue;
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
        this.profit = profit;
    }

    public double getProfitPct() {
        return profitPct;
    }

    public void setProfitPct(double profitPct) {
        this.profitPct = profitPct;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getTotalPortfolioValue() {
        return totalPortfolioValue;
    }

    public void setTotalPortfolioValue(double totalPortfolioValue) {
        this.totalPortfolioValue = totalPortfolioValue;
    }
    public void setTotalPortfolioValue() {
        this.totalPortfolioValue = getTotalCurrValue() + getCash();
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
        this.dayChgPct = dayChgPct;
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
                ", date=" + date +
                '}';
    }
}
