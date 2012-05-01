package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="VALIDATION_COMMENT")
@DiscriminatorValue("VALIDATION")
public class ValidationComment extends Comment {

}
