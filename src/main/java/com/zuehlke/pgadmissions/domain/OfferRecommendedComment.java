package com.zuehlke.pgadmissions.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "CONFIRM_OFFER_RECOMMENDATION")
public class OfferRecommendedComment extends Comment {

    private static final long serialVersionUID = 2184172372328153404L;

}
