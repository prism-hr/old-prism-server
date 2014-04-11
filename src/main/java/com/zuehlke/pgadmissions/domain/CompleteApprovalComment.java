package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "COMPLETE_APPROVAL_STAGE")
public class CompleteApprovalComment extends Comment {

    private static final long serialVersionUID = 2184172372328153404L;

}
