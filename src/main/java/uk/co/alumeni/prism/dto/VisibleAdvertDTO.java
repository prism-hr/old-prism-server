package uk.co.alumeni.prism.dto;

import java.util.Set;

public class VisibleAdvertDTO {

    private Set<AdvertOpportunityCategoryDTO> visible;

    private Set<AdvertOpportunityCategoryDTO> invisible;

    public Set<AdvertOpportunityCategoryDTO> getVisible() {
        return visible;
    }

    public void setVisible(Set<AdvertOpportunityCategoryDTO> visible) {
        this.visible = visible;
    }

    public Set<AdvertOpportunityCategoryDTO> getInvisible() {
        return invisible;
    }

    public void setInvisible(Set<AdvertOpportunityCategoryDTO> invisible) {
        this.invisible = invisible;
    }

    public VisibleAdvertDTO withVisible(Set<AdvertOpportunityCategoryDTO> visible) {
        this.visible = visible;
        return this;
    }

    public VisibleAdvertDTO withInvisible(Set<AdvertOpportunityCategoryDTO> invisible) {
        this.invisible = invisible;
        return this;
    }

}
