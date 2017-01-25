package uk.co.alumeni.prism.rest.dto.application;

import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ApplicationProgramDetailDTO {

    private PrismOpportunityType opportunityType;

    private PrismOpportunityCategory opportunityCategory;

    @NotNull
    private PrismStudyOption studyOption;

    @NotNull
    private LocalDate startDate;

    private List<ApplicationThemeDTO> themes;

    private List<ApplicationLocationDTO> locations;

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

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

    public List<ApplicationThemeDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<ApplicationThemeDTO> themes) {
        this.themes = themes;
    }

    public List<ApplicationLocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<ApplicationLocationDTO> locations) {
        this.locations = locations;
    }

}
