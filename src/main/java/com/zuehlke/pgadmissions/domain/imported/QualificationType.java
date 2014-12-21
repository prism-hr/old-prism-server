package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("QUALIFICATION_TYPE")
public class QualificationType extends SimpleImportedEntity {

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

    public QualificationType withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }
}
