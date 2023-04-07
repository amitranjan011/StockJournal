package com.amit.journal.model;

import com.amit.journal.util.CommonUtil;

public class StockKPI {
    private TransactionSummary bestStock;
    private TransactionSummary worstStock;
    private double avgGainingPct;
    private double avgLossingPct;
    private double avgOverallGainPct;
    private double avgHoldDaysStock;

    public TransactionSummary getBestStock() {
        return bestStock;
    }

    public void setBestStock(TransactionSummary bestStock) {
        this.bestStock = bestStock;
    }

    public TransactionSummary getWorstStock() {
        return worstStock;
    }

    public void setWorstStock(TransactionSummary worstStock) {
        this.worstStock = worstStock;
    }

    public double getAvgGainingPct() {
        return avgGainingPct;
    }

    public void setAvgGainingPct(double avgGainingPct) {
        this.avgGainingPct = CommonUtil.round(avgGainingPct, 1);
    }

    public double getAvgLossingPct() {
        return avgLossingPct;
    }

    public void setAvgLossingPct(double avgLossingPct) {
        this.avgLossingPct = CommonUtil.round(avgLossingPct, 1);
    }

    public double getAvgOverallGainPct() {
        return avgOverallGainPct;
    }

    public void setAvgOverallGainPct(double avgOverallGainPct) {
        this.avgOverallGainPct = CommonUtil.round(avgOverallGainPct, 1);
    }

    public double getAvgHoldDaysStock() {
        return avgHoldDaysStock;
    }

    public void setAvgHoldDaysStock(double avgHoldDaysStock) {
        this.avgHoldDaysStock = CommonUtil.round(avgHoldDaysStock, 1);
    }

    @Override
    public String toString() {
        return "StockKPI{" +
                "bestStock=" + bestStock +
                ", worstStock=" + worstStock +
                ", avgGainingPct=" + avgGainingPct +
                ", avgLossingPct=" + avgLossingPct +
                ", avgOverallGainPct=" + avgOverallGainPct +
                ", avgHoldDaysStock=" + avgHoldDaysStock +
                '}';
    }
}
