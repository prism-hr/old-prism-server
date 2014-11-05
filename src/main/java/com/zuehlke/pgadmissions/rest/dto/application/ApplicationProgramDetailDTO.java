package com.zuehlke.pgadmissions.rest.dto.application;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class ApplicationProgramDetailDTO {

    @NotNull
    private PrismStudyOption studyOption;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Integer referralSource;

    private List<String> themes = Lists.newArrayList();
    
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
    
    public final List<String> getThemes() {
        return themes;
    }

    public final void setAdvertThemes(List<String> themes) {
        this.themes = themes;
    }

}
