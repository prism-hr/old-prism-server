package uk.co.alumeni.prism.rest.dto.advert;

import org.hibernate.validator.constraints.URL;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.utils.validation.DateNotPast;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

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

    @Valid
    private ResourceRelationCreationDTO target;

    private List<Integer> customTargets;

    private Boolean globallyVisible;

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

    public ResourceRelationCreationDTO getTarget() {
        return target;
    }

    public void setTarget(ResourceRelationCreationDTO target) {
        this.target = target;
    }

    public List<Integer> getCustomTargets() {
        return customTargets;
    }

    public void setCustomTargets(List<Integer> customTargets) {
        this.customTargets = customTargets;
    }

    public Boolean getGloballyVisible() {
        return globallyVisible;
    }

    public void setGloballyVisible(Boolean globallyVisible) {
        this.globallyVisible = globallyVisible;
    }

}
