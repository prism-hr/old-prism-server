package uk.co.alumeni.prism.rest.dto.advert;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.utils.validation.DateNotPast;

public class AdvertDTO {

    @URL
    @Size(max = 2048)
    private String applyHomepage;

    @Valid
    private AddressDTO address;

    @DateNotPast
    private LocalDate closingDate;

    @Valid
    private AdvertCategoriesDTO categories;

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public AdvertCategoriesDTO getCategories() {
        return categories;
    }

    public void setCategories(AdvertCategoriesDTO categories) {
        this.categories = categories;
    }

}
