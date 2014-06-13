package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.PrismProgramType;

@Entity
@Table(name = "PROGRAM_TYPE")
public class ProgramType {

    @Id
    @Enumerated(EnumType.STRING)
    private PrismProgramType id;
    
    @OneToMany(mappedBy = "programType")
    private Set<ProgramTypeStudyDuration> defaultStudyDurations = Sets.newHashSet();

    public PrismProgramType getId() {
        return id;
    }

    public void setId(PrismProgramType id) {
        this.id = id;
    }

    public Set<ProgramTypeStudyDuration> getDefaultStudyDurations() {
        return defaultStudyDurations;
    }

    public String getDisplayValue() {
        return id.getDisplayValue();
    }

}
