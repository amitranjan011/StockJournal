package com.amit.journal.domain.repo;

import com.amit.journal.constants.CollectionsName;
import com.amit.journal.constants.Constants;
import com.amit.journal.model.SymbolMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class SymbolMapperDAOImpl extends AbstractBaseDAO<SymbolMapping, String> {
    @Autowired
    private MongoTemplate template;
    @Override
    public Class<SymbolMapping> getPersistentClassType() {
        return SymbolMapping.class;
    }

    @Override
    public String getCollectionName() {
        return CollectionsName.SYMBOL_MAP;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return this.template;
    }

    public SymbolMapping getMapping(String symbol) {
        Query query = getQuery();
        query.addCriteria(where(Constants.SYMBOL_TO_MAP).is(symbol));
        return findOneByQuery(query);
    }
}
