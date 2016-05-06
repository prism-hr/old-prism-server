package uk.co.alumeni.prism.rest.dto.application;

import javax.validation.constraints.NotNull;

public class ApplicationThemeDTO {

    @NotNull
    private Integer id;

    private Boolean preference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

}
