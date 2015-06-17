package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("COUNTRY")
public class ImportedCountry extends ImportedEntitySimple {

    public ImportedCountry withName(String name) {
        setName(name);
        return this;
    }

    public ImportedCountry withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
