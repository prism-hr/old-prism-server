package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;

@Service
@Transactional
public class ImportedEntityService {

    @Autowired
    private ImportedEntityDAO importedEntityDAO;
    
    public <T extends ImportedEntity> T getByCode(Class<? extends ImportedEntity> clazz, String code){
        return importedEntityDAO.getByCode(clazz, code);
    }

    public List<Disability> getAllDisabilities() {
        return importedEntityDAO.getImportedEntities(Disability.class);
    }

    public Disability getDisabilityById(Integer id) {
        return importedEntityDAO.getById(id);
    }

    public List<Language> getAllLanguages() {
        return importedEntityDAO.getImportedEntities(Language.class);
    }

    public Language getLanguageById(Integer id) {
        return importedEntityDAO.getById(id);
    }

    public List<SourcesOfInterest> getAllSourcesOfInterest() {
        return importedEntityDAO.getImportedEntities(SourcesOfInterest.class);
    }

    public SourcesOfInterest getSourceOfInterestById(Integer id) {
        return importedEntityDAO.getById(id);
    }

    public List<Ethnicity> getAllEthnicities() {
        return importedEntityDAO.getImportedEntities(Ethnicity.class);
    }

    public Ethnicity getEthnicityById(Integer id) {
        return importedEntityDAO.getById(id);
    }

    public List<Domicile> getAllDomiciles() {
        return importedEntityDAO.getImportedEntities(Domicile.class);
    }

    public Domicile getDomicileById(Integer id) {
        return importedEntityDAO.getById(id);
    }

    public List<Country> getAllCountries() {
        return importedEntityDAO.getImportedEntities(Country.class);
    }

    public Domicile getCountryById(Integer id) {
        return importedEntityDAO.getById(id);
    }

    public List<QualificationType> getAllQualificationTypes() {
        return importedEntityDAO.getImportedEntities(QualificationType.class);
    }

    public QualificationType getQualificationTypeById(Integer id) {
        return importedEntityDAO.getById(id);
    }
}
