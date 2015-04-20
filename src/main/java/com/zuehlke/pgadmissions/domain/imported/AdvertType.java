package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("ADVERT_TYPE")
public class AdvertType extends SimpleImportedEntity {

    public AdvertType withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public AdvertType withCode(String code) {
        setCode(code);
        return this;
    }

    public AdvertType withName(String name) {
        setName(name);
        return this;
    }

    public AdvertType withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    public PrismAdvertType getPrismAdvertType() {
        return PrismAdvertType.valueOf(getCode());
    }

}
