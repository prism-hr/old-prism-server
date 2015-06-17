package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

@Entity
@DiscriminatorValue("STUDY_OPTION")
public class ImportedStudyOption extends ImportedEntitySimple {

    public ImportedStudyOption withName(String name) {
        setName(name);
        return this;
    }

    public ImportedStudyOption withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    public PrismStudyOption getPrismStudyOption() {
        return PrismStudyOption.valueOf(getName());
    }

}
