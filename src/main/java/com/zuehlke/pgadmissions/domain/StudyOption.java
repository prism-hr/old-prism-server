package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "STUDY_OPTION")
public class StudyOption implements Serializable {

    private static final long serialVersionUID = 2838801786303182881L;

    @Id
    private String id;

    @Column(name = "display_name")
    private String displayName;

    public StudyOption(String id, String displayName) {
        setId(id);
        setDisplayName(displayName);
    }

    public StudyOption() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public StudyOption withId(String id) {
        this.id = id;
        return this;
    }

    public StudyOption withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, displayName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StudyOption other = (StudyOption) obj;
        return Objects.equal(this.id, other.id) && Objects.equal(this.displayName, other.displayName);
    }

}
