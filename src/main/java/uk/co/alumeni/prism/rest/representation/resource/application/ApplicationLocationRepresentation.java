package uk.co.alumeni.prism.rest.representation.resource.application;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Joiner;

import jersey.repackaged.com.google.common.collect.Lists;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;

public class ApplicationLocationRepresentation extends ApplicationSectionRepresentation {

    private ResourceRepresentationRelation resource;

    private String description;

    private LocalDate descriptionDate;

    private Boolean preference;

    public ResourceRepresentationRelation getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationRelation resource) {
        this.resource = resource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

    public ApplicationLocationRepresentation withResource(ResourceRepresentationRelation resource) {
        this.resource = resource;
        return this;
    }

    public ApplicationLocationRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public ApplicationLocationRepresentation withDescriptionDate(LocalDate descriptionDate) {
        this.descriptionDate = descriptionDate;
        return this;
    }

    public ApplicationLocationRepresentation withPreference(Boolean preference) {
        this.preference = preference;
        return this;
    }

    public ApplicationLocationRepresentation withLastUpdateTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

    public String getDisplayName() {
        String displayName = resource.getDisplayName();
        if (!(description == null || descriptionDate == null)) {
            List<String> suffixParts = Lists.newLinkedList();
            if (description != null) {
                suffixParts.add(description);
            }

            if (descriptionDate != null) {
                suffixParts.add("" + descriptionDate.getMonthOfYear() + '/' + descriptionDate.getYear());
            }

            displayName = displayName + " - " + Joiner.on(" - ").join(suffixParts);
        }

        return displayName;
    }

}
