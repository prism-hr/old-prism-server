package uk.co.alumeni.prism.rest.dto.application;

import javax.validation.constraints.NotNull;

public class ApplicationThemeDTO {

    @NotNull
    private Integer themeId;

    private Boolean preference;

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

}
