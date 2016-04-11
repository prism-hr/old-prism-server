package uk.co.alumeni.prism.rest.dto.resource;

import javax.validation.Valid;

import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;

public class InstitutionDTO extends ResourceParentDTO {

    @Valid
    private DocumentDTO logoImage;

    private String currency;

    private Integer businessYearStartMonth;

    @Valid
    private AddressDTO address;

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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}
