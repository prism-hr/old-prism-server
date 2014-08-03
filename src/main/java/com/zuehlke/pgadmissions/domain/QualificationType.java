package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("QUALIFICATION_TYPE")
public class QualificationType extends AbstractImportedEntity {

    public QualificationType withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public QualificationType withCode(String code) {
        setCode(code);
        return this;
    }

    public QualificationType withName(String name) {
        setName(name);
        return this;
    }

    public QualificationType withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }
}
