package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.LocalDate;
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

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntity> T getById(Class<? extends ImportedEntity> clazz, Institution institution, Integer id) {
        T entity = (T) entityService.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
        return entity;
    }
    
    public <T extends ImportedEntity> T getByCode(Class<? extends ImportedEntity> clazz, Institution institution, String code) {
        return importedEntityDAO.getByCode(clazz, institution, code);
    }
    
    public ImportedEntity getByName(Class<ImportedEntity> entityClass, Institution institution, String name) {
        return importedEntityDAO.getByName(entityClass, institution, name);
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

    public void disableAllImportedPrograms(Institution institution, LocalDate baseline) {
        importedEntityDAO.disableAllImportedPrograms(institution, baseline);
        importedEntityDAO.disableAllImportedProgramStudyOptions(institution);
        importedEntityDAO.disableAllImportedProgramStudyOptionInstances(institution);
    }

}
