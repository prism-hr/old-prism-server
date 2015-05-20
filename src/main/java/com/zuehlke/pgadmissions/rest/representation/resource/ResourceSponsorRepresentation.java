package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;

public class ResourceSponsorRepresentation {

    private Integer sponsorId;

    private String sponsorTitle;

    private Integer sponsorLogoId;

    private Double sponsorshipProvided;

    public Integer getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Integer sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getSponsorTitle() {
        return sponsorTitle;
    }

    public void setSponsorTitle(String sponsorTitle) {
        this.sponsorTitle = sponsorTitle;
    }

    public Integer getSponsorLogoId() {
        return sponsorLogoId;
    }

    public void setSponsorLogoId(Integer sponsorLogoId) {
        this.sponsorLogoId = sponsorLogoId;
    }

    public BigDecimal getSponsorshipProvided() {
        return BigDecimal.valueOf(sponsorshipProvided);
    }

    public void setSponsorshipProvided(Double sponsorshipProvided) {
        this.sponsorshipProvided = sponsorshipProvided;
    }

}
