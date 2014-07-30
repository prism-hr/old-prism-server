package com.zuehlke.pgadmissions.services;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.ReferralSource;

@Service
@Transactional
public class ImportedEntityService {

    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private EntityDAO entityDAO;
    
    public <T extends ImportedEntity> T getByCode(Class<? extends ImportedEntity> clazz, Institution institution, String code){
        return importedEntityDAO.getByCode(clazz, institution, code);
    }

    public <T extends ImportedEntity> T getById(Class<? extends ImportedEntity> clazz, Institution institution, Integer id){
        T entity = (T) entityDAO.getByProperties(clazz, ImmutableMap.of("institution", institution, "id", id));
        if(entity == null) {
            throw new NullPointerException("Object of class " + clazz + " with id " + id + " for institution " + institution.getName() + " not found.");
        }
        return entity;
    }

    public List<Disability> getAllDisabilities() {
        return importedEntityDAO.getImportedEntities(Disability.class);
    }

    public List<Language> getAllLanguages(Institution institution) {
        return importedEntityDAO.getImportedEntities(Language.class);
    }

    public List<ReferralSource> getAllSourcesOfInterest(Institution institution) {
        return importedEntityDAO.getImportedEntities(ReferralSource.class);
    }

    public List<Ethnicity> getAllEthnicities(Institution institution) {
        return importedEntityDAO.getImportedEntities(Ethnicity.class);
    }

    public List<Domicile> getAllDomiciles(Institution institution) {
        return importedEntityDAO.getImportedEntities(Domicile.class);
    }

    public List<Country> getAllCountries(Institution institution) {
        return importedEntityDAO.getImportedEntities(Country.class);
    }

    public List<QualificationType> getAllQualificationTypes(Institution institution) {
        return importedEntityDAO.getImportedEntities(QualificationType.class);
    }
    
    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityDAO.getImportedEntityFeeds();
    }

    public void disableAllEntities(Class<? extends ImportedEntity> entityClass) {
        importedEntityDAO.disableAllEntities(entityClass);
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

}
