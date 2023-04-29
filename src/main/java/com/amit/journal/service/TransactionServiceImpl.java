package com.amit.journal.service;

import com.amit.journal.constants.Constants;
import com.amit.journal.csv.helper.CSVHelper;
import com.amit.journal.domain.repo.TransactionsDAOImpl;
import com.amit.journal.model.Transaction;
import com.amit.journal.util.CommonUtil;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
	private static final Logger LOG = LogManager.getLogger(TransactionServiceImpl.class);
	@Autowired
	private TransactionsDAOImpl transactionsDAO;

	@Autowired
	private TransactionSummaryService transactionSummaryService;

	@Override
	public void saveFile(MultipartFile file, LocalDate transactionDate) {
		CSVHelper.saveFile(file, Constants.TRANSACTIONS_DIR);
		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            // create csv bean reader
            CsvToBean<Transaction> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Transaction.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // convert `CsvToBean` object to list of users
            List<Transaction> transactions = csvToBean.parse();
			LocalDate today = LocalDate.now();
			String batchId = UUID.randomUUID().toString();
			if (!CommonUtil.isObjectNullOrEmpty(transactionDate)) {
				transactions.forEach(transaction -> setDateAndBatchId(transaction, transactionDate, batchId));
			} else {
				transactions.forEach(transaction -> setDateAndBatchId(transaction, today, batchId));
			}
			LOG.info("Successfully saved the transactions file and populated transactions objects for file : {}", file.getOriginalFilename());

			transactionsDAO.insertMultiDocuments(transactions);
			transactionSummaryService.copySummaryToHistory();
			transactionSummaryService.updateSummaryBatchId(batchId);
			transactionSummaryService.processTransactions(transactions);
			LOG.info("Successfully saved the transactions objects for file : {}", file.getOriginalFilename());
        } catch (Exception ex) {
			LOG.error("Exception while saving file for transaction upload for file: {} : {}"
					,file.getOriginalFilename(), ExceptionUtils.getStackTrace(ex));
			throw new RuntimeException(ex);
        }
	}
	@Override
	public List<Transaction> getTransactions() {
		return transactionsDAO.getAllTransactions();
	}

	@Override
	public List<Transaction> getTransactions(String symbol, LocalDate startDate, LocalDate endDate) {
		return transactionsDAO.getTransactions(symbol, startDate, endDate);
	}

	private void setDateAndBatchId(Transaction transaction, LocalDate transactionDate, String batchId) {
		transaction.setTransactionDate(transactionDate);
		transaction.setBatchId(batchId);
	}
}
