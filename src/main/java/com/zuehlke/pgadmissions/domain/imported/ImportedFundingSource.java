package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("FUNDING_SOURCE")
public class ImportedFundingSource extends ImportedEntitySimple {

    public ImportedFundingSource withName(String name) {
        setName(name);
        return this;
    }

    public ImportedFundingSource withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
