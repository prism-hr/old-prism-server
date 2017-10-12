package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;

import static com.google.common.base.Objects.equal;

public class AdvertCategoryDTO {

    private Integer advert;

    private String opportunityCategories;

    private PrismRole role;

    public Integer getAdvert() {
        return advert;
    }

    public void setAdvert(Integer advert) {
        this.advert = advert;
    }

    public String getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(String opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(advert);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        AdvertCategoryDTO other = (AdvertCategoryDTO) object;
        return equal(advert, other.getAdvert());
    }

}
