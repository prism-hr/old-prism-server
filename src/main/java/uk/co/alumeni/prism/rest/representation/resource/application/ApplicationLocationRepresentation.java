package uk.co.alumeni.prism.rest.representation.resource.application;

import java.util.List;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;

import com.google.common.base.Joiner;

public class ApplicationLocationRepresentation extends ApplicationTagSectionRepresentation {

    private ResourceRepresentationRelation resource;

    private String description;

    private LocalDate descriptionDate;

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

    public LocalDate getDescriptionDate() {
        return descriptionDate;
    }

    public void setDescriptionDate(LocalDate descriptionDate) {
        this.descriptionDate = descriptionDate;
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
        setPreference(preference);
        return this;
    }

    public ApplicationLocationRepresentation withLastUpdateTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

    @Override
    public String toString() {
        String displayName = resource.getDisplayName();
        if (!(description == null || descriptionDate == null)) {
            List<String> suffixParts = Lists.newLinkedList();
            if (description != null) {
                suffixParts.add(description);
            }

            if (descriptionDate != null) {
                suffixParts.add("" + descriptionDate.getMonthOfYear() + '/' + descriptionDate.getYear());
            }

            if (CollectionUtils.isNotEmpty(suffixParts)) {
                displayName = displayName + " - " + Joiner.on(" - ").join(suffixParts);
            }
        }

        return displayName;
    }

}
