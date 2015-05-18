package com.zuehlke.pgadmissions.rest.dto.advert;

import java.math.BigDecimal;

public class AdvertSponsorshipDTO {

    private BigDecimal sponsorshipTarget;

    public BigDecimal getSponsorshipTarget() {
        return sponsorshipTarget;
    }

    public void setSponsorshipTarget(BigDecimal sponsorshipTarget) {
        this.sponsorshipTarget = sponsorshipTarget;
    }
}
