package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(value = "PROVIDE_INTERVIEW_FEEDBACK")
public class InterviewComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

    @Transient
    private String alert;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

}
