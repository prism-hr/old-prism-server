package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("PROGRAM_TYPE")
public class ProgramType extends SimpleImportedEntity {

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

    public ProgramType withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    public PrismProgramType getPrismProgramType() {
        return PrismProgramType.valueOf(getCode());
    }

}
