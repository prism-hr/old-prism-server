package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class ProgramDTO {

    private Integer institution;

    @NotNull
    private PrismProgramType programType;

    @NotEmpty
    @Size(max = 255)
    private String title;

    @Size(min = 1)
    private PrismStudyOption[] studyOptions;

    @NotNull
    private Boolean requireProjectDefinition;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private AdvertDTO advert;

    public Integer getInstitution() {
        return institution;
    }

    public void setInstitution(Integer institution) {
        this.institution = institution;
    }

    public PrismProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PrismStudyOption[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(PrismStudyOption[] studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public AdvertDTO getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
    }
}
