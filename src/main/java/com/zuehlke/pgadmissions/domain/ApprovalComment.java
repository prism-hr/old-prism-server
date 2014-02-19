package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "COMPLETE_APPROVAL_STAGE")
public class ApprovalComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

}
