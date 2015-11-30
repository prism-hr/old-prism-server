package uk.co.alumeni.prism.rest.representation.resource.application;

import org.joda.time.DateTime;

public class ApplicationThemeRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private String name;

    private Boolean preference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

    public ApplicationThemeRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationThemeRepresentation withName(String name) {
        this.name = name;
        return this;
    }

    public ApplicationThemeRepresentation withPreference(Boolean preference) {
        this.preference = preference;
        return this;
    }

    public ApplicationThemeRepresentation withLastUpdateTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}
