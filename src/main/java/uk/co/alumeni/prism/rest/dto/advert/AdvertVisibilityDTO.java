package uk.co.alumeni.prism.rest.dto.advert;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

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
