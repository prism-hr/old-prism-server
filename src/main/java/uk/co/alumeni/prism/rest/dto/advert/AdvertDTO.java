package uk.co.alumeni.prism.rest.dto.advert;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.utils.validation.DateNotPast;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class AdvertDTO {

    private List<PrismOpportunityType> targetOpportunityTypes;

    @Size(max = 340)
    private String summary;

    @URL
    @Size(max = 2048)
    private String homepage;

    @URL
    @Size(max = 2048)
    private String applyHomepage;

    @PhoneNumber
    private String telephone;

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

    public List<PrismOpportunityType> getTargetOpportunityTypes() {
        return targetOpportunityTypes;
    }

    public void setTargetOpportunityTypes(List<PrismOpportunityType> targetOpportunityTypes) {
        this.targetOpportunityTypes = targetOpportunityTypes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
