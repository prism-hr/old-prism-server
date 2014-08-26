package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

@Entity
@DiscriminatorValue("PROGRAM_TYPE")
public class ProgramType extends SimpleImportedEntity {

    public ProgramType withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public ProgramType withCode(PrismProgramType code) {
        setCode(code.toString());
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

}
