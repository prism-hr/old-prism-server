package uk.co.alumeni.prism.rest.dto.resource;

import uk.co.alumeni.prism.rest.dto.DocumentDTO;

public class InstitutionDTO extends ResourceParentDTO {

    private DocumentDTO logoImage;

    private String currency;

    private Integer businessYearStartMonth;

    public DocumentDTO getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(DocumentDTO logoImage) {
        this.logoImage = logoImage;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getBusinessYearStartMonth() {
        return businessYearStartMonth;
    }

    public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
    }

}
