package com.zuehlke.pgadmissions.rest.dto.application;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ApplicationProgramDetailDTO {

    @NotNull
    private Boolean previousApplication;

    @NotNull
    private PrismStudyOption studyOption;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Integer referralSource;

    private List<String> primaryThemes = Lists.newArrayList();

    private List<String> secondaryThemes = Lists.newArrayList();

    public Boolean getPreviousApplication() {
        return previousApplication;
    }

    public void setPreviousApplication(Boolean previousApplication) {
        this.previousApplication = previousApplication;
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

    public Integer getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(Integer referralSource) {
        this.referralSource = referralSource;
    }

    public List<String> getPrimaryThemes() {
        return primaryThemes;
    }

    public void setPrimaryThemes(List<String> primaryThemes) {
        this.primaryThemes = primaryThemes;
    }

    public List<String> getSecondaryThemes() {
        return secondaryThemes;
    }

    public void setSecondaryThemes(List<String> secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
    }

}
