package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
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

    public List<QualificationInstitution> getEnabledInstitutionsByDomicileCode(String domicileCode) {
        return qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(domicileCode);
    }

    public QualificationInstitution getOrCreateCustomInstitution(String institutionCode, Domicile institutionCountry, String otherInstitutionName) {
        QualificationInstitutionService thisBean = applicationContext.getBean(QualificationInstitutionService.class);

        QualificationInstitution institution;
        if ("OTHER".equals(institutionCode)) {
            institution = qualificationInstitutionDAO.getInstitutionByDomicileAndName(institutionCountry.getCode(), otherInstitutionName);
            if (institution == null) {
                institution = new QualificationInstitution();
                institution.setDomicileCode(institutionCountry.getCode());
                institution.setEnabled(true);
                institution.setName(otherInstitutionName);
                institution.setCode(thisBean.generateNextInstitutionCode());
                qualificationInstitutionDAO.save(institution);
            }
        } else {
            institution = getInstitutionByCode(institutionCode);
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