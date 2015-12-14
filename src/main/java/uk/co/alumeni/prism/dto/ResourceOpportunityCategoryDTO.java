package uk.co.alumeni.prism.dto;

public class ResourceOpportunityCategoryDTO extends EntityOpportunityCategoryDTO<ResourceOpportunityCategoryDTO> {

    private Boolean onlyAsPartner;

    public Boolean getOnlyAsPartner() {
        return onlyAsPartner;
    }

    public void setOnlyAsPartner(Boolean onlyAsPartner) {
        this.onlyAsPartner = onlyAsPartner;
    }

}
