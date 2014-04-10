package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.QualificationInstitutionReference;
import com.zuehlke.pgadmissions.domain.enums.InstitutionState;

public class QualificationInstitutionBuilder {

    private Integer id;
    private String domicileCode;
    private String name;
    private InstitutionState state;
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

    public QualificationInstitutionBuilder state(InstitutionState state) {
        this.state = state;
        return this;
    }

    public Institution build() {
        Institution institution = new Institution();
        institution.setId(id);
        institution.setDomicileCode(domicileCode);
        institution.setCode(code);
        institution.setName(name);
        institution.setState(state);
        return institution;
    }

    public static QualificationInstitutionBuilder aQualificationInstitution() {
        return new QualificationInstitutionBuilder().code("AGH").name("Akademia G\u00F3rniczo-Hutnicza").domicileCode("PL")
                .state(InstitutionState.INSTITUTION_APPROVED);
    }

    public QualificationInstitutionReference buildAsReference() {
        QualificationInstitutionReference institution = new QualificationInstitutionReference();
        institution.setId(id);
        institution.setDomicileCode(domicileCode);
        institution.setCode(code);
        institution.setName(name);
        institution.setEnabled(state == InstitutionState.INSTITUTION_APPROVED);
        return institution;
    }
}
