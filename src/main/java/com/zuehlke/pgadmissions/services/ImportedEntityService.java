package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Service
@Transactional
public class ImportedEntityService {

    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private EntityService entityService;

    public <T extends ImportedEntity> T getByCode(Class<? extends ImportedEntity> clazz, Institution institution, String code) {
        return importedEntityDAO.getByCode(clazz, institution, code);
    }

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity> T getById(Class<? extends ImportedEntity> clazz, Institution institution, Integer id) {
        T entity = (T) entityService.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
        return entity;
    }

    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location) {
        return getOrCreateImportedEntityFeed(institution, importedEntityType, location, null, null);
    }
    
    public ImportedEntityFeed getOrCreateImportedEntityFeed(Institution institution, PrismImportedEntity importedEntityType, String location, String username,
            String password) {
        ImportedEntityFeed transientImportedEntityFeed = new ImportedEntityFeed().withImportedEntityType(importedEntityType).withLocation(location)
                .withUserName(username).withPassword(password).withInstitution(institution);
        return entityService.getOrCreate(transientImportedEntityFeed);
    }

    public List<ImportedEntityFeed> getImportedEntityFeedsToImport() {
        return importedEntityDAO.getImportedEntityFeedsToImport();
    }

    public void disableAllEntities(Class<? extends ImportedEntity> entityClass, Institution institution) {
        importedEntityDAO.disableAllEntities(entityClass, institution);
    }

    public void disableAllImportedPrograms(Institution institution) {
        importedEntityDAO.disableAllImportedPrograms(institution);
        importedEntityDAO.disableAllImportedProgramInstances(institution);
    }

    public ImportedEntity getByName(Class<ImportedEntity> entityClass, Institution institution, String name) {
        return importedEntityDAO.getByName(entityClass, institution, name);
    }
    
    public void getOrImportEntity(Class<ImportedEntity> entityClass, Institution institution, ImportedEntity transientEntity) {
        ImportedEntity persistentEntity = entityService.getDuplicateEntity(transientEntity);
        
        if (persistentEntity == null) {
            entityService.save(transientEntity);
        } else {
            String transientCode = transientEntity.getCode();
            String transientName = transientEntity.getName();
            
            String persistentCode = persistentEntity.getCode();
            String persistentName = persistentEntity.getName();
            
            if (transientCode == persistentCode && transientName == persistentName) {
                persistentEntity.setEnabled(true);
            } else {
                if (transientName != persistentName) {
                    ImportedEntity otherPersistentEntity = getByName(entityClass, institution, transientName);
                    if (otherPersistentEntity == null) {
                        persistentEntity.setName(transientName);
                    }
                } else {
                    ImportedEntity otherPersistentEntity = getByCode(entityClass, institution, transientCode);
                    if (otherPersistentEntity == null) {
                        persistentEntity.setCode(transientCode);
                    }
                }
                persistentEntity.setEnabled(true);
            }
        }
    }

}
