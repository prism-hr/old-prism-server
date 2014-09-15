package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

public class ProjectDTO {

    @NotNull
    private Integer programId;

    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private AdvertDTO advert;

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
