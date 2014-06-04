package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;

@Entity
@Table(name = "PROGRAM_TYPE")
public class ProgramType implements Serializable {

    private static final long serialVersionUID = 6250099599688273995L;

    @Id
    @Enumerated(EnumType.STRING)
    private ProgramTypeId id;

    public ProgramTypeId getId() {
        return id;
    }

    public void setId(ProgramTypeId id) {
        this.id = id;
    }

    public String getDisplayValue() {
        return id.getDisplayValue();
    }

}
