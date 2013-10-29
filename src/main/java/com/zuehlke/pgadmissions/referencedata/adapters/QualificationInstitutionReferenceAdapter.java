package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Institutions.Institution;

public class QualificationInstitutionReferenceAdapter implements ImportData {

    private Institution institution;
    
    public QualificationInstitutionReferenceAdapter(Institution institution) {
        this.institution = institution;
    }
    
    public String getName() {
        return institution.getName();
    }

    @Override
    public String getStringCode() {
        return institution.getCode();
    }
    
    public String getDomicileCode() {
        return institution.getCountry();
    }

    @Override
    public com.zuehlke.pgadmissions.domain.QualificationInstitutionReference createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
        com.zuehlke.pgadmissions.domain.QualificationInstitutionReference result = new com.zuehlke.pgadmissions.domain.QualificationInstitutionReference();
        result.setCode(institution.getCode());
        result.setName(institution.getName());
        result.setEnabled(true);
        result.setDomicileCode(institution.getCountry());
        return result;
    }

}
