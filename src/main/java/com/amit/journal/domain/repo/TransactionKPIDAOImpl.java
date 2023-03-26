package com.amit.journal.domain.repo;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.model.TransactionKPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionKPIDAOImpl extends AbstractBaseDAO<TransactionKPI, String> implements TransactionKPIDAO {
    @Autowired
    private MongoTemplate template;
    @Override
    public Class<TransactionKPI> getPersistentClassType() {
        return TransactionKPI.class;
    }

    @Override
    public String getCollectionName() {
        return CollectionsName.TRANSACTIONKPI;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return this.template;
    }
}
