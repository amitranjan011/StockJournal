package com.amit.journal.model;

public class TransactionKPI /*extends UserBase */{
    private double avgGainPct;
    private double avgLossPct;
    private TransactionSummary bestStock;
    private TransactionSummary worstStock;
    //max holding period

    public double getAvgGainPct() {
        return avgGainPct;
    }

    public void setAvgGainPct(double avgGainPct) {
        this.avgGainPct = avgGainPct;
    }

    public double getAvgLossPct() {
        return avgLossPct;
    }

    public void setAvgLossPct(double avgLossPct) {
        this.avgLossPct = avgLossPct;
    }

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

    @Override
    public String toString() {
        return "TransactionKPI{" +
                "avgGainPct=" + avgGainPct +
                ", avgLossPct=" + avgLossPct +
                ", bestStock=" + bestStock +
                ", worstStock=" + worstStock +
                '}';
    }
}
