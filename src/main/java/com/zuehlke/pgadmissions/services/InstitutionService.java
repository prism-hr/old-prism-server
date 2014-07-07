package com.zuehlke.pgadmissions.services;

import java.util.List;

import com.zuehlke.pgadmissions.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Service
@Transactional
public class InstitutionService {
    
    @Autowired
    private InstitutionDAO institutionDAO;
    
    @Autowired
    private StateService stateService;

    @Autowired
    private ApplicationContext applicationContext;

    public Institution getByCode(String institutionCode) {
        return institutionDAO.getByCode(institutionCode);
    }

    public List<ImportedInstitution> getEnabledImportedInstitutionsByDomicile(Domicile domicile) {
        return institutionDAO.getEnabledImportedInstitutionsByDomicile(domicile);
    }
    
    public Institution getOrCreate(String institutionCode, InstitutionDomicile domicile, String institutionName) {
        Institution persistentInstitution;
        if ("OTHER".equals(institutionCode)) {
            persistentInstitution = institutionDAO.getByDomicileAndName(domicile, institutionName);
            if (persistentInstitution == null) {
                persistentInstitution = new Institution();
                persistentInstitution.setDomicile(domicile);
                persistentInstitution.setState(stateService.getById(PrismState.INSTITUTION_APPROVED));
                persistentInstitution.setName(institutionName);
                institutionDAO.save(persistentInstitution);
            }
        } else {
            persistentInstitution = getByCode(institutionCode);
        }
        return persistentInstitution;
    }

    public List<InstitutionDomicileRegion> getTopLevelRegions(InstitutionDomicile domicile) {
        return institutionDAO.getTopLevelRegions(domicile);
    }
}
