package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("REJECTION_REASON")
public class ImportedRejectionReason extends ImportedEntitySimple {

    public ImportedRejectionReason withName(String name) {
        setName(name);
        return this;
    }

    public ImportedRejectionReason withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
