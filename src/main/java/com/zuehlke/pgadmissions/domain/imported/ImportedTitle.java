package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TITLE")
public class ImportedTitle extends ImportedEntitySimple {

    public ImportedTitle withName(String name) {
        setName(name);
        return this;
    }

    public ImportedTitle withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
