package com.amit.journal.model;

import com.amit.journal.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

public class TransactionBasic {
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;
    private String transactionType;
    private int quantity;
    private double price;
    private double totalValue;
    private double stopLoss;

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = CommonUtil.round(price, 2);
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = CommonUtil.round(totalValue, 2);
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    @Override
    public String toString() {
        return "TransactionBasic{" +
                "transactionDate=" + transactionDate +
                ", transactionType='" + transactionType + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalValue=" + totalValue +
                ", stopLoss=" + stopLoss +
                '}';
    }
}
