package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;

public class ResourceSponsorRepresentation {

    private Integer sponsorId;

    private String sponsorTitle;

    private Integer sponsorLogoId;

    private BigDecimal sponsorshipProvided;

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
        return sponsorshipProvided;
    }

    public void setSponsorshipProvided(BigDecimal sponsorshipProvided) {
        this.sponsorshipProvided = sponsorshipProvided;
    }

    public ResourceSponsorRepresentation withSponsorId(Integer sponsorId) {
        this.sponsorId = sponsorId;
        return this;
    }

    public ResourceSponsorRepresentation withSponsorTitle(String sponsorTitle) {
        this.sponsorTitle = sponsorTitle;
        return this;
    }

    public ResourceSponsorRepresentation withSponsorLogoId(Integer sponsorLogoId) {
        this.sponsorLogoId = sponsorLogoId;
        return this;
    }

    public ResourceSponsorRepresentation withSponsorshipProvided(BigDecimal sponsorshipProvided) {
        this.sponsorshipProvided = sponsorshipProvided;
        return this;
    }

}
