package uk.co.alumeni.prism.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.dao.EntityDAO;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.UniqueEntity.EntitySignature;
import uk.co.alumeni.prism.exceptions.DeduplicationException;
import uk.co.alumeni.prism.utils.PrismReflectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EntityService {

    @Autowired
    private EntityDAO entityDAO;

    public <T> T getById(Class<T> klass, Object id) {
        return entityDAO.getById(klass, id);
    }

    public <T> T getByProperty(Class<T> klass, String propertyName, Object propertyValue) {
        return entityDAO.getByProperty(klass, propertyName, propertyValue);
    }

    public <T> T getByProperties(Class<T> klass, Map<String, Object> properties) {
        return entityDAO.getByProperties(klass, properties);
    }

    public <T> List<T> getAll(Class<T> klass) {
        return entityDAO.list(klass);
    }

    public <T extends UniqueEntity> T getDuplicateEntity(T uniqueResource) throws DeduplicationException {
        return entityDAO.getDuplicateEntity(uniqueResource);
    }

    public <T extends UniqueEntity> T getDuplicateEntity(Class<T> entityClass, EntitySignature entitySignature) throws DeduplicationException {
        return entityDAO.getDuplicateEntity(entityClass, entitySignature);
    }

    public <T extends UniqueEntity> T getOrCreate(T transientResource) throws DeduplicationException {
        T persistentResource = getDuplicateEntity(transientResource);
        if (persistentResource == null) {
            save(transientResource);
            persistentResource = transientResource;
        }
        return persistentResource;
    }

    public <T extends UniqueEntity> T createOrUpdate(T transientEntity) throws DeduplicationException {
        T persistentEntity = getDuplicateEntity(transientEntity);
        if (persistentEntity == null) {
            save(transientEntity);
            persistentEntity = transientEntity;
        } else {
            persistentEntity = replace(persistentEntity, transientEntity);
        }

        return persistentEntity;
    }

    public <T extends UniqueEntity> T replace(T persistentEntity, T transientEntity) {
        persistentEntity = overwriteProperties(persistentEntity, transientEntity);
        flush();
        return persistentEntity;
    }

    public Serializable save(Object entity) {
        return entityDAO.save(entity);
    }

    public void delete(Object entity) {
        entityDAO.delete(entity);
    }

    public void flush() {
        entityDAO.flush();
    }

    public void clear() {
        entityDAO.clear();
    }

    public void evict(Object entity) {
        entityDAO.evict(entity);
    }

    public <T> void deleteAll(Class<T> classReference) {
        entityDAO.deleteAll(classReference);
    }

    @SuppressWarnings("unchecked")
    private <T extends UniqueEntity> T overwriteProperties(T persistentEntity, T transientEntity) {
        Object persistentId = PrismReflectionUtils.getProperty(persistentEntity, "id");
        PrismReflectionUtils.setProperty(transientEntity, "id", persistentId);
        T mergedEntity = (T) entityDAO.merge(transientEntity);
        flush();
        return mergedEntity;
    }

}
