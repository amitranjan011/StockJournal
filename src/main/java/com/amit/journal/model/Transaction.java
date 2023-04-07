package com.amit.journal.model;

import java.time.LocalDate;

import com.amit.journal.constants.Constants;
import com.amit.journal.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.data.annotation.Id;

import com.opencsv.bean.CsvBindByName;

public class Transaction extends UserBase {
	@Id
    private String id;
	
	@CsvBindByName(column = "Date")
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate transactionDate;
	
	@CsvBindByName(column = "Instrument")
	private String symbol;
	
	@CsvBindByName(column = "Type")
	private String transactionType;
	
	@CsvBindByName(column = "Qty.")
	private int quantity;
	
	@CsvBindByName(column = "Avg.")
	private double price;
	
	@CsvBindByName(column = "LTP")
	private double lastTradingPrice;

	private double totalValue;
	
	@CsvBindByName(column = "StopLoss")
	private double stopLoss;
	
	@CsvBindByName(column = "Strategy")
	private String strategy;
	
	@CsvBindByName(column = "Comments")
	private String comments;
	
	@CsvBindByName
	private String name;
	@CsvBindByName
	private String action; // fresh buy/sale/ add more
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LocalDate getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}
/*	public void setTransactionDate() {
		this.transactionDate = LocalDate.now();
	}*/
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int quantity) {
		if (quantity > -1) {
			this.transactionType = Constants.TRANSACTION_TYPE_BUY;
		} else this.transactionType = Constants.TRANSACTION_TYPE_SELL;
//		setTransactionDate(LocalDate.now());
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
		setTransactionType(quantity);
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
	public void setTotalValue() {
		this.totalValue = CommonUtil.round(getQuantity() * getPrice(), 2);
	}

	public double getLastTradingPrice() {
		return lastTradingPrice;
	}
	public void setLastTradingPrice(double lastTradingPrice) {
		this.lastTradingPrice = CommonUtil.round(lastTradingPrice, 2);
		setTotalValue();
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

	@Override
	public String toString() {
		return "Transaction{" +
				"id='" + id + '\'' +
				", transactionDate=" + transactionDate +
				", symbol='" + symbol + '\'' +
				", transactionType='" + transactionType + '\'' +
				", quantity=" + quantity +
				", price=" + price +
				", lastTradingPrice=" + lastTradingPrice +
				", totalValue=" + totalValue +
				", stopLoss=" + stopLoss +
				", strategy='" + strategy + '\'' +
				", comments='" + comments + '\'' +
				", name='" + name + '\'' +
				", action='" + action + '\'' +
				'}';
	}
}
