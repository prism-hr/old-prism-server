package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("rejection_reason")
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
