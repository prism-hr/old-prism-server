package uk.co.alumeni.prism.rest.dto.advert;

import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AdvertVisibilityDTO {

    @NotNull
    private Boolean globallyVisible;

    private List<Integer> customTargets;

    private LocalDate closingDate;

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

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }
}
