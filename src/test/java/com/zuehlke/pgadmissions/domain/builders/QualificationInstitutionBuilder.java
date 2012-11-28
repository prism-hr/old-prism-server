package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.QualificationInstitutionReference;

public class QualificationInstitutionBuilder {
    
    private Integer id;
    private String countryCode;
    private String name;
    private Boolean enabled;
    private String code;
    
    public QualificationInstitutionBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public QualificationInstitutionBuilder code(String code) {
        this.code = code;
        return this;
    }
    
    public QualificationInstitutionBuilder countryCode(String code) {
        this.countryCode = code;
        return this;
    }

    public QualificationInstitutionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public QualificationInstitutionBuilder enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public QualificationInstitution toQualificationInstitution() {
        QualificationInstitution institution = new QualificationInstitution();
        institution.setId(id);
        institution.setDomicileCode(countryCode);
        institution.setCode(code);
        institution.setName(name);
        institution.setEnabled(enabled);
        return institution;
    }
    
    public QualificationInstitutionReference toQualificationInstitutionReference() {
        QualificationInstitutionReference institution = new QualificationInstitutionReference();
        institution.setId(id);
        institution.setDomicileCode(countryCode);
        institution.setCode(code);
        institution.setName(name);
        institution.setEnabled(enabled);
        return institution;
    }
}
