package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity  
@DiscriminatorValue(value="COMPLETE_VALIDATION_STAGE")
public class ValidationComment extends Comment {

    private static final long serialVersionUID = 1545465975465291005L;

}


