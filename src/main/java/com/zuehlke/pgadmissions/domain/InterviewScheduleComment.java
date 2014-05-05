package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@DiscriminatorValue(value = "CONFIRM_INTERVIEW_ARRANGEMENTS")
public class InterviewScheduleComment extends Comment {

    private static final long serialVersionUID = -3138212534729565852L;

}
