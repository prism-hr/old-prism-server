package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

@Entity
@DiscriminatorValue("PROGRAM_TYPE")
public class ProgramType extends SimpleImportedEntity {

    @Override
    public void setCode(String code) {
        try {
            PrismProgramType.valueOf(code);
            super.setCode(code);
        } catch (IllegalArgumentException e) {
            throw new Error(e);
        }
    }
    
    public ProgramType withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public ProgramType withCode(String code) {
        setCode(code);
        return this;
    }

    public ProgramType withName(String name) {
        setName(name);
        return this;
    }

    public ProgramType withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }
    
    public PrismProgramType getPrismProgramType() {
        return PrismProgramType.valueOf(getCode());
    }

}
