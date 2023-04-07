package com.amit.journal.model;

import com.amit.journal.util.CommonUtil;
import com.opencsv.bean.CsvBindByName;

public class HoldingItem {
    @CsvBindByName(column = "Instrument")
    public String instrument;
    @CsvBindByName(column = "Qty.")
    public int quantity;
    @CsvBindByName(column = "Avg. cost")
    public double avgCost;
    @CsvBindByName(column = "LTP")
    public double lastPrice;
    @CsvBindByName(column = "Cur. val")
    public double currVal;

    public double buyVal;
    @CsvBindByName(column = "P&L")
    public double profit;
    @CsvBindByName(column = "Net chg.")
    public double netChgPct;
    @CsvBindByName(column = "Day chg.")
    public double symbolDayChangePct;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(double avgCost) {
        this.avgCost = avgCost;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
        double currVal = getAvgCost() * getQuantity();
        setCurrVal(currVal);

        double buyVal = getAvgCost() * getQuantity();
        setBuyVal(buyVal);
    }

    public double getCurrVal() {
        return currVal;
    }

    public void setCurrVal(double currVal) {
        this.currVal = CommonUtil.round(currVal, 2);
    }

    public double getBuyVal() {
        return buyVal;
    }

    public void setBuyVal(double buyVal) {
        this.buyVal = CommonUtil.round(buyVal, 2);
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = CommonUtil.round(profit, 2);
    }

    public double getNetChgPct() {
        return netChgPct;
    }

    public void setNetChgPct(double netChgPct) {
        this.netChgPct = CommonUtil.round(netChgPct, 1);
    }

    public double getSymbolDayChangePct() {
        return symbolDayChangePct;
    }

    public void setSymbolDayChangePct(double symbolDayChangePct) {
        this.symbolDayChangePct = CommonUtil.round(symbolDayChangePct, 2);
    }

    @Override
    public String toString() {
        return "HoldingItem{" +
                "instrument='" + instrument + '\'' +
                ", quantity=" + quantity +
                ", avgCost=" + avgCost +
                ", lastPrice=" + lastPrice +
                ", currVal=" + currVal +
                ", buyVal=" + buyVal +
                ", profit=" + profit +
                ", netChgPct=" + netChgPct +
                ", symbolDayChangePct=" + symbolDayChangePct +
                '}';
    }
}
