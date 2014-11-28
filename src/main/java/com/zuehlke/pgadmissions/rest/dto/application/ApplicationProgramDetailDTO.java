package com.zuehlke.pgadmissions.rest.dto.application;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class ApplicationProgramDetailDTO {

    @Size(max = 255)
    private String studyLocation;

    @Size(max = 255)
    private String studyDivision;

    @Size(max = 255)
    private String studyArea;

    @NotNull
    private PrismStudyOption studyOption;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Integer referralSource;

    private List<String> primaryThemes = Lists.newArrayList();

    private List<String> secondaryThemes = Lists.newArrayList();

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

    public final List<String> getPrimaryThemes() {
        return primaryThemes;
    }

    public final void setPrimaryThemes(List<String> primaryThemes) {
        this.primaryThemes = primaryThemes;
    }

    public final List<String> getSecondaryThemes() {
        return secondaryThemes;
    }

    public final void setSecondaryThemes(List<String> secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
    }

    public final String getStudyLocation() {
        return studyLocation;
    }

    public final void setStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
    }

    public final String getStudyDivision() {
        return studyDivision;
    }

    public final void setStudyDivision(String studyDivision) {
        this.studyDivision = studyDivision;
    }

    public final String getStudyArea() {
        return studyArea;
    }

    public final void setStudyArea(String studyArea) {
        this.studyArea = studyArea;
    }

}
