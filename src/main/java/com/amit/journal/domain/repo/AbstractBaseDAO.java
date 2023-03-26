package com.amit.journal.domain.repo;

import java.io.Serializable;
import java.util.List;

import com.amit.journal.constants.Constants;
import com.amit.journal.interceptor.UserContext;
import com.amit.journal.model.UserBase;
import com.amit.journal.util.ContextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;


public abstract class AbstractBaseDAO<T, PK extends Serializable> {
    private static final Logger LOG = LogManager.getLogger(AbstractBaseDAO.class);

    protected Class<T> persistentClassType;

    public AbstractBaseDAO() {
        this.persistentClassType = getPersistentClassType();

    }

    public abstract Class<T> getPersistentClassType();

    public abstract String getCollectionName();

    public abstract MongoTemplate getMongoTemplate();

    public Query getUserQuery() {
        Query query = new Query();
        query.addCriteria(getUserCriteria());
        return query;
    }
    protected Criteria getUserCriteria() {
        return Criteria.where(Constants.USERID).is(UserContext.getUserId());
    }

    public void clear() {
    }

    public void persist(T transientInstance) {
        persist(transientInstance, getCollectionName());
    }

    public T persist(T transientInstance, String collectionName) {
        ContextUtil.populateUserId((UserBase) transientInstance);
        return getMongoTemplate().save(transientInstance, collectionName);
    }

    public void insertMultiDocuments(List<T> transients) {
        transients.stream().forEach(transientInstance -> ContextUtil.populateUserId((UserBase) transientInstance));
        getMongoTemplate().insert(transients, getCollectionName());
    }
    
    public void insertMultiDocuments(List<T> transients, String collectionName) {
        getMongoTemplate().insert(transients, collectionName);
    }

    public void delete(Query query) {
        delete(query, getCollectionName());
    }

    public void delete(Query query, Class<?> entityClass, String collectionName) {
        getMongoTemplate().remove(query, entityClass, collectionName);
    }

    public void delete(Query query, String collectionName) {
        delete(query, persistentClassType, collectionName);
    }

    public <V> V findOneByQuery(Query query, Class<V> entityClass, String collectionName) {
        return getMongoTemplate().findOne(query, entityClass, collectionName);
    }

    public T findOneByQuery(Query query) {
        return findOneByQuery(query, persistentClassType, getCollectionName());
    }

    public T findByFieldId(String field, PK value, String collectionName) {
        Query query = getUserQuery();
        query.addCriteria(Criteria.where(field).is(value));
        return getMongoTemplate().findOne(query, persistentClassType, collectionName);
    }

    public List<T> findAllByFieldId(String field, PK value, String collectionName) {
        Query query = getUserQuery();
        query.addCriteria(Criteria.where(field).is(value));
        return getQueryResult(query, collectionName);
    }

    public List<T> findAll() {
        return findAll(getCollectionName());
    }

    public List<T> findAll(String collectionName) {
        return getQueryResult(getUserQuery(), collectionName);
    }

    public List<T> findAll(Query query) {
        return getQueryResult(query, persistentClassType, getCollectionName());
    }

    public <V> List<V> findAll(Query query, Class<V> entityClass, String collectionName) {
        return getQueryResult(query, entityClass, collectionName);
    }

    protected <V> List<V> getQueryResult(Query query, Class<V> entityClass, String collectionName) {
        List<V> results = getMongoTemplate().find(query, entityClass, collectionName);
        LOG.debug("find by example successful, result size: {}", results.size());
        return results;
    }

    public List<T> getQueryResult(Query query, String collectionName) {
        List<T> results = getMongoTemplate().find(query, persistentClassType, collectionName);
        LOG.debug("find by example successful, result size: {}", results.size());
        return results;
    }

    public UpdateResult updateFirst(Update update, Query query, String collectionName) {
        return getMongoTemplate().updateFirst(query, update, persistentClassType, collectionName);
    }

    public void updateObject(T transientInstance, Query query, String collectionName) {
        Document document = new Document();
        getMongoTemplate().getConverter().write(transientInstance, document);
        Update update = Update.fromDocument(document);
        updateFirst(update, query, collectionName);
    }

    public void updateMulti(Update update, Query query, String collectionName) {
        getMongoTemplate().updateMulti(query, update, persistentClassType, collectionName);
    }

    public T findAndRemove(Query query) {
        return findAndRemove(query, getCollectionName());
    }

    public T findAndRemove(Query query, String collectionName) {
        return findAndRemove(query, persistentClassType, collectionName);
    }

    public <V> V findAndRemove(Query query, Class<V> entityClass, String collectionName) {
        return getMongoTemplate().findAndRemove(query, entityClass, collectionName);
    }

    public void findAndReplace(Query query) {
        findAndReplace(query, persistentClassType, getCollectionName());
    }

    public <V> void findAndReplace(Query query, Class<V> entityClass, String collectionName) {
        getMongoTemplate().findAndReplace(query, entityClass, collectionName);
    }

    public void findAndReplace(Query query, T replacement) {
        findAndReplace(query, replacement, getCollectionName());
    }

    public void findAndReplace(Query query, T replacement, String collectionName) {
        getMongoTemplate().findAndReplace(query, replacement, collectionName);
    }

    public long count(Query query, String collectionName) {
        return getMongoTemplate().count(query, collectionName);
    }

    public long count(Query query) {
        return count(query, getCollectionName());
    }

    public T findAndModify(Query query, Update update, boolean retuenNew) {
        return findAndModify(query, update, retuenNew, false, getPersistentClassType(), getCollectionName());
    }

    public T findAndModify(Query query, Update update, boolean retuenNew, boolean upsert) {
        return findAndModify(query, update, retuenNew, upsert, getPersistentClassType(), getCollectionName());
    }

    public <V> V findAndModify(Query query, Update update, boolean retuenNew, boolean upsert, Class<V> entityClass, String collectionName) {
        return getMongoTemplate().findAndModify(query, update, new FindAndModifyOptions().upsert(upsert).returnNew(retuenNew),
                entityClass, collectionName);
    }


}