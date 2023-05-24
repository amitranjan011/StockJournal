package com.amit.journal.model;

public class StockData extends UserBase {

    private double price;
    private String symbol;
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "price=" + price +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
