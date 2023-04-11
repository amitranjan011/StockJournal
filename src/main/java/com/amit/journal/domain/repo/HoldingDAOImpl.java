package com.amit.journal.domain.repo;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.constants.Constants;
import com.amit.journal.model.Holding;
import com.amit.journal.util.CommonUtil;
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

@Repository
public class HoldingDAOImpl extends AbstractBaseDAO<Holding, String> implements HoldingDAO {
    private static final Logger LOG = LogManager.getLogger(HoldingDAOImpl.class);
    @Autowired
    private MongoTemplate template;
    @Override
    public Class<Holding> getPersistentClassType() {
        return Holding.class;
    }

    @Override
    public String getCollectionName() {
        return CollectionsName.HOLDING;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return this.template;
    }

    @Override
    public List<Holding> getHoldingsByDate(LocalDate startDate, LocalDate endDate) {
        Query query  = getUserQuery();

        if (!CommonUtil.isObjectNullOrEmpty(startDate) && !CommonUtil.isObjectNullOrEmpty(endDate)) {
            Criteria criteria = Criteria.where(Constants.HOLDING_DATE).gte(startDate).lte(endDate);
            query.addCriteria(criteria);
        }
        List<Holding> list = findAll(query);
        return list;
    }

    @Override
    public Holding getLatestHolding() {
        Query query = getUserQuery();
        query.limit(1);
        query = query.with(Sort.by(Sort.Direction.DESC, Constants.HOLDING_DATE));

        Holding holding = findOneByQuery(query);
        return holding;
    }

    @Override
    public Holding getLatestWeeklyHolding() {
        Query query = getUserQuery();
        query.limit(1);
        query = query.with(Sort.by(Sort.Direction.DESC, Constants.HOLDING_DATE));

        Holding holding = findOneByQuery(query, getPersistentClassType(), CollectionsName.HOLDING_WEEK);
        return holding;
    }
}
