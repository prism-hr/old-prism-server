package uk.co.alumeni.prism.dto;

public class AdvertOpportunityFilterDTO extends EntityOpportunityFilterDTO {

    private Boolean recommended;

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

}
