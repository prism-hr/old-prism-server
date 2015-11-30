package uk.co.alumeni.prism.rest.representation.resource.application;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;

public class ApplicationProgramDetailRepresentation extends ApplicationSectionRepresentation {

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

}
