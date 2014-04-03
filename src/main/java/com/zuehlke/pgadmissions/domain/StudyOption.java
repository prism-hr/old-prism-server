package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "STUDY_OPTION")
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
    
}
