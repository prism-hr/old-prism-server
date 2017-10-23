package uk.co.alumeni.prism.rest.representation.resource.application;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;

import java.util.List;
import java.util.Set;

import static uk.co.alumeni.prism.PrismConstants.COMMA;
import static uk.co.alumeni.prism.PrismConstants.SPACE;

public class ApplicationProgramDetailRepresentation extends ApplicationSectionRepresentation {

    private String preferredFlag;

    private PrismStudyOption studyOption;

    private LocalDate startDate;

    private List<ApplicationThemeRepresentation> themes;

    private List<ApplicationLocationRepresentation> locations;

    public PrismStudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public List<ApplicationThemeRepresentation> getThemes() {
        return themes;
    }

    public void setThemes(List<ApplicationThemeRepresentation> themes) {
        this.themes = themes;
    }

    public List<ApplicationLocationRepresentation> getLocations() {
        return locations;
    }

    public void setLocations(List<ApplicationLocationRepresentation> locations) {
        this.locations = locations;
    }

    public ApplicationProgramDetailRepresentation withPreferredFlag(String preferredFlag) {
        this.preferredFlag = preferredFlag;
        return this;
    }

    public ApplicationProgramDetailRepresentation withStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ApplicationProgramDetailRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationProgramDetailRepresentation withThemes(List<ApplicationThemeRepresentation> themes) {
        this.themes = themes;
        return this;
    }

    public ApplicationProgramDetailRepresentation withLocations(List<ApplicationLocationRepresentation> locations) {
        this.locations = locations;
        return this;
    }

    public ApplicationProgramDetailRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

    public String getThemesDisplay() {
        return getTagsDisplay(themes);
    }

    public String getLocationsDisplay() {
        return getTagsDisplay(locations);
    }

    private <T extends ApplicationTagSectionRepresentation> String getTagsDisplay(List<T> tags) {
        if (CollectionUtils.isNotEmpty(tags)) {
            String preferredTag = null;
            Set<String> secondaryTags = Sets.newTreeSet();

            for (T tag : tags) {
                if (BooleanUtils.isTrue(tag.getPreference())) {
                    preferredTag = tag.toString();
                } else {
                    secondaryTags.add(tag.toString());
                }
            }

            String tagContent = preferredTag + " (" + preferredFlag + ")";
            if (CollectionUtils.isNotEmpty(secondaryTags)) {
                String separator = COMMA + SPACE;
                tagContent = tagContent + separator + Joiner.on(separator).join(secondaryTags);
            }

            return tagContent;
        }

        return null;
    }

}
