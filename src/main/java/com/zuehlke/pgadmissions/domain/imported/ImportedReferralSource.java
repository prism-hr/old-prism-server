package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("REFERRAL_SOURCE")
public class ImportedReferralSource extends ImportedEntitySimple {

    public ImportedReferralSource withName(String name) {
        setName(name);
        return this;
    }

    public ImportedReferralSource withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
