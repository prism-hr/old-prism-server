package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@DiscriminatorValue(value = "CONFIRM_ELIGIBILITY")
public class AdmitterComment extends Comment {

    private static final long serialVersionUID = 8991440051685308411L;

}
