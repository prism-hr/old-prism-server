package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.QualificationInstitutionReference;

public class QualificationInstitutionBuilder {
    
    private Integer id;
    private String domicileCode;
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
    
    public QualificationInstitutionBuilder domicileCode(String code) {
        this.domicileCode = code;
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

    public QualificationInstitution build() {
        QualificationInstitution institution = new QualificationInstitution();
        institution.setId(id);
        institution.setDomicileCode(domicileCode);
        institution.setCode(code);
        institution.setName(name);
        institution.setEnabled(enabled);
        return institution;
    }
    
    public static QualificationInstitutionBuilder aQualificationInstitution(){
        return new QualificationInstitutionBuilder().code("AGH").name("Akademia G\u00F3rniczo-Hutnicza").domicileCode("PL").enabled(true);
    }
    
    public QualificationInstitutionReference buildAsReference() {
        QualificationInstitutionReference institution = new QualificationInstitutionReference();
        institution.setId(id);
        institution.setDomicileCode(domicileCode);
        institution.setCode(code);
        institution.setName(name);
        institution.setEnabled(enabled);
        return institution;
    }
}
