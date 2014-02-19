package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "COMPLETE_REVIEW_STAGE")
public class ReviewEvaluationComment extends Comment {

    private static final long serialVersionUID = 2184172372328153404L;

}
