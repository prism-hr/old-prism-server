package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("REFERRAL_SOURCE")
public class ReferralSource extends SimpleImportedEntity {

    public ReferralSource withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public ReferralSource withCode(String code) {
        setCode(code);
        return this;
    }

    public ReferralSource withName(String name) {
        setName(name);
        return this;
    }

    public ReferralSource withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
