package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Service
@Transactional
public class QualificationInstitutionService {
    
    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;
    
    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private ApplicationContext applicationContext;

    public Institution getByCode(String institutionCode) {
        return qualificationInstitutionDAO.getByCode(institutionCode);
    }

    public List<Institution> getEnabledInstitutionsByDomicileCode(String domicileCode) {
        return qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(domicileCode);
    }
    
    public List<Institution> getEnabledInstitutionsByUserIdAndDomicileCode(Integer userId, String domicileCode) {
        return qualificationInstitutionDAO.getEnabledInstitutionsByUserIdAndDomicileCode(userId, domicileCode);
    }
    
    public List<Institution> getEnabledInstitutionsByDomicileCodeExludingUserId(Integer userId, String domicileCode) {
        return qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCodeExludingUserId(userId, domicileCode);
    }

    public Institution getOrCreate(String institutionCode, Domicile domicile, String institutionName) {
        Institution persistentInstitution;
        if ("OTHER".equals(institutionCode)) {
            persistentInstitution = qualificationInstitutionDAO.getByDomicileAndName(domicile.getCode(), institutionName);
            if (persistentInstitution == null) {
                persistentInstitution = new Institution();
                persistentInstitution.setDomicileCode(domicile.getCode());
                persistentInstitution.setState(stateDAO.getById(PrismState.INSTITUTION_APPROVED));
                persistentInstitution.setName(institutionName);
                persistentInstitution.setCode(generateNextInstitutionCode());
                qualificationInstitutionDAO.save(persistentInstitution);
            }
        } else {
            persistentInstitution = getByCode(institutionCode);
        }
        return persistentInstitution;
    }

    protected String generateNextInstitutionCode() {
        Institution lastCustomInstitution = qualificationInstitutionDAO.getLastCustomInstitution();
        Integer codeNumber;
        if (lastCustomInstitution != null) {
            codeNumber = Integer.valueOf(lastCustomInstitution.getCode().substring(4));
            codeNumber++;
        } else {
            codeNumber = 0;
        }
        return String.format("CUST%05d", codeNumber);
    }

}
