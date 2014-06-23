package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("STUDY_OPTION")
public class StudyOption extends ImportedEntity {

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
