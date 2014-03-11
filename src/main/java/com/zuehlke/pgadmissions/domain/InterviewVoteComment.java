package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "PROVIDE_INTERVIEW_AVAILABILITY")
public class InterviewVoteComment extends Comment {

    private static final long serialVersionUID = -3138212534729565852L;

}
