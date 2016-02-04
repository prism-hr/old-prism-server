package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("REJECTION_REASON")
public class RejectionReason extends ImportedEntitySimple {

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

    public RejectionReason withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
