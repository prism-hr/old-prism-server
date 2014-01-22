package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;

@Service
@Transactional
public class QualificationInstitutionService {

    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @Autowired
    private ApplicationContext applicationContext;

    public QualificationInstitution getInstitutionByCode(String institutionCode) {
        return qualificationInstitutionDAO.getInstitutionByCode(institutionCode);
    }

    public QualificationInstitution getOrCreateCustomInstitution(OpportunityRequest opportunityRequest) {
        QualificationInstitutionService thisBean = applicationContext.getBean(QualificationInstitutionService.class);

        QualificationInstitution institution;
        if ("OTHER".equals(opportunityRequest.getInstitutionCode())) {
            institution = new QualificationInstitution();
            institution.setDomicileCode(opportunityRequest.getInstitutionCountry().getCode());
            institution.setEnabled(true);
            institution.setName(opportunityRequest.getOtherInstitution());
            institution.setCode(thisBean.generateNextInstitutionCode());
            qualificationInstitutionDAO.save(institution);
        } else {
            institution = getInstitutionByCode(opportunityRequest.getInstitutionCode());
        }
        return institution;
    }

    protected String generateNextInstitutionCode() {
        QualificationInstitution lastCustomInstitution = qualificationInstitutionDAO.getLastCustomInstitution();
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