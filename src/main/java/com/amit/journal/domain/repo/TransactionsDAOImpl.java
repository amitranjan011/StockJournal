package com.amit.journal.domain.repo;

import com.amit.journal.constants.Constants;
import com.amit.journal.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.model.Transaction;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class TransactionsDAOImpl extends AbstractBaseDAO<Transaction, String> implements TransactionsDAO {
	@Autowired
	private MongoTemplate template;
	
	@Override
	public Class<Transaction> getPersistentClassType() {
		return Transaction.class;
	}

	@Override
	public String getCollectionName() {
		return CollectionsName.TRANSACTIONS;
	}

	@Override
	public MongoTemplate getMongoTemplate() {
		return this.template;
	}

	@Override
	public List<Transaction> getTransactions(String symbol, LocalDate startDate, LocalDate endDate) {
		Query query  = getUserQuery();
		if (!CommonUtil.isNullOrEmpty(symbol)) {
			query.addCriteria(where(Constants.SYMBOL).is(symbol));
		}
		if (!CommonUtil.isObjectNullOrEmpty(startDate) && !CommonUtil.isObjectNullOrEmpty(endDate)) {
			query.addCriteria(where(Constants.TRANSACTION_DATE).gte(startDate).lte(endDate));
		}
		query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, Constants.TRANSACTION_DATE)));
		return findAll(query);
	}

	@Override
	public List<Transaction> getAllTransactions() {
		Query query  = getUserQuery();
		query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, Constants.TRANSACTION_DATE)));
		return findAll(query);
	}

	@Override
	public void deleteAllByBatchId(String batchId) {
		Query query  = getUserQuery();
		query.addCriteria(where(Constants.BATCH_ID).is(batchId));
		delete(query, getCollectionName());
	}
}
