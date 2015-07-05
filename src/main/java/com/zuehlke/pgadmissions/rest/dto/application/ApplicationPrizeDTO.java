package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.utils.validation.DateNotFuture;

public class ApplicationPrizeDTO {

    private Integer id;

    @NotEmpty
    private String provider;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotNull
    @DateNotFuture
    private LocalDate awardDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final String getProvider() {
        return provider;
    }

    public final void setProvider(String provider) {
        this.provider = provider;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final LocalDate getAwardDate() {
        return awardDate;
    }

    public final void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

}
