package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "ASSIGN_REVIEWERS")
public class AssignReviewersComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

}
