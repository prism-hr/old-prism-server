package uk.co.alumeni.prism.rest.dto.advert;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AdvertVisibilityDTO {

    @NotNull
    private Boolean globallyVisible;

    private List<Integer> customTargets;

    public Boolean getGloballyVisible() {
        return globallyVisible;
    }

    public void setGloballyVisible(Boolean globallyVisible) {
        this.globallyVisible = globallyVisible;
    }

    public List<Integer> getCustomTargets() {
        return customTargets;
    }

    public void setCustomTargets(List<Integer> customTargets) {
        this.customTargets = customTargets;
    }
}
