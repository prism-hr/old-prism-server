package com.zuehlke.pgadmissions.rest.dto.advert;

import java.math.BigDecimal;

public class AdvertSponsorshipDTO {

    private String sponsorshipPurpose;
    
    private BigDecimal sponsorshipTarget;
    
    public String getSponsorshipPurpose() {
        return sponsorshipPurpose;
    }

    public void setSponsorshipPurpose(String sponsorshipPurpose) {
        this.sponsorshipPurpose = sponsorshipPurpose;
    }

    public BigDecimal getSponsorshipTarget() {
        return sponsorshipTarget;
    }

    public void setSponsorshipTarget(BigDecimal sponsorshipTarget) {
        this.sponsorshipTarget = sponsorshipTarget;
    }

}
