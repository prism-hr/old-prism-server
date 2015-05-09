package com.zuehlke.pgadmissions.rest.dto;

public class InstitutionPartnerDTO {

    private Integer partnerId;

    private InstitutionDTO partner;

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public InstitutionDTO getPartner() {
        return partner;
    }

    public void setPartner(InstitutionDTO partner) {
        this.partner = partner;
    }

    public boolean isEmpty() {
        return partnerId == null && partner == null;
    }

}
