package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@DiscriminatorValue("REFERRAL_SOURCE")
public class ReferralSource extends ImportedEntitySimple {

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

    public ReferralSource withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
