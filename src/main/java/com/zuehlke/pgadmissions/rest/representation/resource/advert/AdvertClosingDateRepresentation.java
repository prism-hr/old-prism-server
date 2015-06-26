package com.zuehlke.pgadmissions.rest.representation.resource.advert;

import org.joda.time.LocalDate;

public class AdvertClosingDateRepresentation {

    private Integer id;

    private LocalDate closingDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public AdvertClosingDateRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertClosingDateRepresentation withClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
        return this;
    }

}
