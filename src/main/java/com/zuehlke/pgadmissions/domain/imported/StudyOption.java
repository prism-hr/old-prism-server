package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("study_option")
public class StudyOption extends ImportedEntitySimple {

    public StudyOption withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public StudyOption withCode(String code) {
        setCode(code);
        return this;
    }

    public StudyOption withName(String name) {
        setName(name);
        return this;
    }

    public StudyOption withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    public PrismStudyOption getPrismStudyOption() {
        return PrismStudyOption.valueOf(getCode());
    }

}
