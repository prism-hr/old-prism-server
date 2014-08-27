package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

@Entity
@DiscriminatorValue("STUDY_OPTION")
public class StudyOption extends SimpleImportedEntity {

    @Override
    public void setCode(String code) {
        try {
            PrismStudyOption.valueOf(code);
            super.setCode(code);
        } catch (IllegalArgumentException e) {
            throw new Error("Invalid code for imported study option", e);
        }
    }
    
    
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

    public StudyOption withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

}
