package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "CONFIRM_PRIMARY_SUPERVISION")
public class SupervisionConfirmationComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

}
