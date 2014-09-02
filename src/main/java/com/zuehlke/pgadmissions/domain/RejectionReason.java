package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("REJECTION_REASON")
public class RejectionReason extends AbstractImportedEntity {

    public RejectionReason withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public RejectionReason withCode(String code) {
        setCode(code);
        return this;
    }

    public RejectionReason withName(String name) {
        setName(name);
        return this;
    }

    public RejectionReason withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
