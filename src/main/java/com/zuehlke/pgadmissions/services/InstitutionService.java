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
    
    public List<InstitutionDomicileRegion> getTopLevelRegions(InstitutionDomicile domicile) {
        return institutionDAO.getTopLevelRegions(domicile);
    }
}
