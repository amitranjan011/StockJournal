package com.amit.journal.domain.repo;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.constants.Constants;
import com.amit.journal.model.TransactionSummary;
import com.amit.journal.util.CommonUtil;
import com.amit.journal.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class TransactionsSummaryDAOImpl extends AbstractBaseDAO<TransactionSummary, String> implements TransactionsSummaryDAO {
    private static final Logger LOG = LogManager.getLogger(TransactionsSummaryDAOImpl.class);
    @Autowired
    private MongoTemplate template;

    @Override
    public Class<TransactionSummary> getPersistentClassType() {
        return TransactionSummary.class;
    }

    @Override
    public String getCollectionName() {
        return CollectionsName.TRANSACTIONS_SUMMARY;
    }
    @Override
    public MongoTemplate getMongoTemplate() {
        return this.template;
    }

    @Override
    public TransactionSummary findBySymbolAndOpen(String symbol) {
        Query query  = getUserQuery();
        query.addCriteria(new Criteria().andOperator(
                where(Constants.POSITION_STATUS_key).is(Constants.POSITION_STATUS_OPEN), where(Constants.SYMBOL).is(symbol)));
        List<TransactionSummary> results = getQueryResult(query, getCollectionName());
        int size = results.size();
        if (size > 0) {
            if ( size > 1) {
                LOG.error("Multiple record exists for symbol in open status : {}", symbol);
            }
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<TransactionSummary> getSummaryRecords(String symbol, LocalDate startDate, LocalDate endDate) {
        Query query  = getUserQuery();
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, Constants.POSITION_STATUS_key)));
        if (!CommonUtil.isNullOrEmpty(symbol)) {
            query.addCriteria(where(Constants.SYMBOL).is(symbol));
        }
        if (!CommonUtil.isObjectNullOrEmpty(startDate) && !CommonUtil.isObjectNullOrEmpty(endDate)) {
            query.addCriteria(where(Constants.ENTRY_DATE).gte(startDate).lte(endDate));
        }
        return findAll(query);
    }

    @Override
    public List<TransactionSummary> getAllRecords() {
        return findAll();
    }

    @Override
    public List<TransactionSummary> findAllBySymbolsAndOpen(List<String> symbols) {
        Query query  = getUserQuery();
        query.addCriteria(where(Constants.SYMBOL).in(symbols));
//        query.addCriteria(new Criteria().andOperator(
//                where(Constants.POSITION_STATUS_key).is(Constants.POSITION_STATUS_OPEN), where(Constants.SYMBOL).in(symbols)));
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, Constants.ENTRY_DATE)));
        List<TransactionSummary> results = getQueryResult(query, getCollectionName());
        return results;
    }

    @Override
    public TransactionSummary getSingleSummaryRecord(int limit) {
        Query query  = getUserQuery();
        query.limit(limit);
        List<TransactionSummary> list = findAll(query);
        if (!CommonUtil.isObjectNullOrEmpty(list) && list.size() > 0) return list.get(0);
        return null;
    }
    @Override
    public void deleteAllSummaryRecordsForUser() {
        Query query  = getUserQuery();
        delete(query, getCollectionName());
    }


    @Override
    public TransactionSummary getLatestRecordFromHistory() {
        Query query  = getUserQuery();
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, Constants.LAST_UPDATE)));
        query.limit(1);
        List<TransactionSummary> list = findAll(query, getPersistentClassType(),CollectionsName.TRANSACTIONS_SUMMARY_HISTORY);
        if (!CommonUtil.isObjectNullOrEmpty(list) && list.size() > 0) return list.get(0);
        return null;
    }

    @Override
    public List<TransactionSummary> getSummaryRecordsByType(String positionStatus) {
        Query query  = getUserQuery();
        query.addCriteria(new Criteria().andOperator(where(Constants.POSITION_STATUS_key).is(positionStatus)));
        return findAll(query);
    }

    @Override
    public void deleteAllSummaryRecordsByBatchId(String batchId) {
        Query query  = getUserQuery();
        query.addCriteria(new Criteria().andOperator(where(Constants.BATCH_ID).is(batchId)));
        delete(query, getCollectionName());
    }
}
