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
import com.zuehlke.pgadmissions.domain.ReferralSource;

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

    public List<Language> getAllLanguages() {
        return importedEntityDAO.getImportedEntities(Language.class);
    }

    public List<ReferralSource> getAllSourcesOfInterest() {
        return importedEntityDAO.getImportedEntities(ReferralSource.class);
    }

    public List<Ethnicity> getAllEthnicities() {
        return importedEntityDAO.getImportedEntities(Ethnicity.class);
    }

    public List<Domicile> getAllDomiciles() {
        return importedEntityDAO.getImportedEntities(Domicile.class);
    }

    public List<Country> getAllCountries() {
        return importedEntityDAO.getImportedEntities(Country.class);
    }

    public List<QualificationType> getAllQualificationTypes() {
        return importedEntityDAO.getImportedEntities(QualificationType.class);
    }

}
