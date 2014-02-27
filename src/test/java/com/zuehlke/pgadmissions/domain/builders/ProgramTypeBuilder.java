package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;

public class ProgramTypeBuilder {
    
    private ProgramTypeId id;
    private Integer defaultStudyDuration;
    
    public ProgramTypeBuilder id(ProgramTypeId programTypeId) {
        this.id = programTypeId;
        return this;
    }
    
    public ProgramTypeBuilder defaultStudyDuration(Integer defaultStudyDuration) {
        this.defaultStudyDuration = defaultStudyDuration;
        return this;
    }
    
    public ProgramType build() {
        ProgramType programType = new ProgramType();
        programType.setId(id);
        programType.setDefaultStudyDuration(defaultStudyDuration);
        return programType;
    }
    
    public static ProgramTypeBuilder aProgramType(QualificationInstitution institution) {
        return new ProgramTypeBuilder().id(ProgramTypeId.RESEARCH_DEGREE).defaultStudyDuration(36);
    }
}
