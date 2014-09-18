package com.zuehlke.pgadmissions.rest.dto;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProjectDTO {

    @NotNull
    private Integer programId;

    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotNull
    private LocalDate dueDate;

    @NotEmpty
    @Size(max = 1000)
    private String summary;

    @Min(1)
    private Integer studyDurationMinimum;

    @Min(1)
    private Integer studyDurationMaximum;

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getStudyDurationMinimum() {
        return studyDurationMinimum;
    }

    public void setStudyDurationMinimum(Integer studyDurationMinimum) {
        this.studyDurationMinimum = studyDurationMinimum;
    }

    public Integer getStudyDurationMaximum() {
        return studyDurationMaximum;
    }

    public void setStudyDurationMaximum(Integer studyDurationMaximum) {
        this.studyDurationMaximum = studyDurationMaximum;
    }
}
