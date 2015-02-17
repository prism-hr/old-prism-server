package com.zuehlke.pgadmissions.rest.dto.application;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class ApplicationProgramDetailDTO {

    @NotNull
    private Boolean previousApplication;
    
    @Valid
    private ApplicationStudyDetailDTO studyDetail;

    @NotNull
    private PrismStudyOption studyOption;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Integer referralSource;

    private List<String> primaryThemes = Lists.newArrayList();

    private List<String> secondaryThemes = Lists.newArrayList();
    
    public final Boolean getPreviousApplication() {
        return previousApplication;
    }

    public final void setPreviousApplication(Boolean previousApplication) {
        this.previousApplication = previousApplication;
    }

    public final ApplicationStudyDetailDTO getStudyDetail() {
        return studyDetail;
    }

    public final void setStudyDetail(ApplicationStudyDetailDTO studyDetail) {
        this.studyDetail = studyDetail;
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

}
