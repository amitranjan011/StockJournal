package com.amit.journal.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amit.journal.model.Transaction;

public interface TransactionService {

	void saveFile(MultipartFile file, LocalDate transactionDate);

	List<Transaction> getTransactions();

    List<Transaction> getTransactions(String symbol, LocalDate startDate, LocalDate endDate);
}