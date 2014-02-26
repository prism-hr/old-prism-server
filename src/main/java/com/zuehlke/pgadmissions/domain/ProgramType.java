package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;

@Entity(name = "PROGRAM_TYPE")
public class ProgramType implements Serializable {

    private static final long serialVersionUID = 6250099599688273995L;

    @Id
    @Enumerated(EnumType.STRING)
    private ProgramTypeId id;

    @Column(name = "default_study_duration")
    private Integer defaultStudyDuration;

    public ProgramType(ProgramTypeId id, Integer defaultStudyDuration) {
        this.id = id;
        this.defaultStudyDuration = defaultStudyDuration;
    }

    public ProgramTypeId getId() {
        return id;
    }

    public void setId(ProgramTypeId id) {
        this.id = id;
    }

    public Integer getDefaultStudyDuration() {
        return defaultStudyDuration;
    }

    public void setDefaultStudyDuration(Integer defaultStudyDuration) {
        this.defaultStudyDuration = defaultStudyDuration;
    }

    public String getDisplayValue() {
        return id.getDisplayValue();
    }

}
