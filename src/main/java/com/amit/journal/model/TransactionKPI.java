package com.amit.journal.model;

public class TransactionKPI extends UserBase {

    private StockKPI stockClosed;
    private StockKPI stockOpen;

    public StockKPI getStockClosed() {
        return stockClosed;
    }

    public void setStockClosed(StockKPI stockClosed) {
        this.stockClosed = stockClosed;
    }

    public StockKPI getStockOpen() {
        return stockOpen;
    }

    public void setStockOpen(StockKPI stockOpen) {
        this.stockOpen = stockOpen;
    }

    @Override
    public String toString() {
        return "TransactionKPI{" +
                "stockClosed=" + stockClosed +
                ", stockOpen=" + stockOpen +
                '}';
    }
    /*private double avgGainPctClosed;
    private double avgLossPctClosed;
    private double avgOverallGainPctClosed;
    private double avgHoldDaysClosed;
    private TransactionSummary bestClosedStock;
    private TransactionSummary worstClosedStock;


    private double avgGainPctOpen;
    private double avgLossPctOpen;
    private double avgOverallGainPctOpen;
    private TransactionSummary bestOpenStock;
    private TransactionSummary worstOpenStock;
    //max holding period

    public double getAvgGainPctClosed() {
        return avgGainPctClosed;
    }

    public void setAvgGainPctClosed(double avgGainPctClosed) {
        this.avgGainPctClosed = avgGainPctClosed;
    }

    public double getAvgLossPctClosed() {
        return avgLossPctClosed;
    }

    public void setAvgLossPctClosed(double avgLossPctClosed) {
        this.avgLossPctClosed = avgLossPctClosed;
    }

    public TransactionSummary getBestClosedStock() {
        return bestClosedStock;
    }

    public void setBestClosedStock(TransactionSummary bestClosedStock) {
        this.bestClosedStock = bestClosedStock;
    }

    public TransactionSummary getWorstClosedStock() {
        return worstClosedStock;
    }

    public void setWorstClosedStock(TransactionSummary worstClosedStock) {
        this.worstClosedStock = worstClosedStock;
    }

    public double getAvgHoldDaysClosed() {
        return avgHoldDaysClosed;
    }

    public void setAvgHoldDaysClosed(double avgHoldDaysClosed) {
        this.avgHoldDaysClosed = avgHoldDaysClosed;
    }

    public TransactionSummary getBestOpenStock() {
        return bestOpenStock;
    }

    public void setBestOpenStock(TransactionSummary bestOpenStock) {
        this.bestOpenStock = bestOpenStock;
    }

    public TransactionSummary getWorstOpenStock() {
        return worstOpenStock;
    }

    public void setWorstOpenStock(TransactionSummary worstOpenStock) {
        this.worstOpenStock = worstOpenStock;
    }

    public double getAvgOverallGainPctClosed() {
        return avgOverallGainPctClosed;
    }

    public void setAvgOverallGainPctClosed(double avgOverallGainPctClosed) {
        this.avgOverallGainPctClosed = avgOverallGainPctClosed;
    }

    public double getAvgGainPctOpen() {
        return avgGainPctOpen;
    }

    public void setAvgGainPctOpen(double avgGainPctOpen) {
        this.avgGainPctOpen = avgGainPctOpen;
    }

    public double getAvgLossPctOpen() {
        return avgLossPctOpen;
    }

    public void setAvgLossPctOpen(double avgLossPctOpen) {
        this.avgLossPctOpen = avgLossPctOpen;
    }

    public double getAvgOverallGainPctOpen() {
        return avgOverallGainPctOpen;
    }

    public void setAvgOverallGainPctOpen(double avgOverallGainPctOpen) {
        this.avgOverallGainPctOpen = avgOverallGainPctOpen;
    }*/


}
