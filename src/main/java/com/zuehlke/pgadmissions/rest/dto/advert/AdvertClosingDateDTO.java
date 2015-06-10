package com.zuehlke.pgadmissions.rest.dto.advert;

import org.joda.time.LocalDate;

public class AdvertClosingDateDTO {

    private LocalDate closingDate;

    private Integer studyPlaces;

    public final LocalDate getClosingDate() {
        return closingDate;
    }

    public final void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public final Integer getStudyPlaces() {
        return studyPlaces;
    }

    public final void setStudyPlaces(Integer studyPlaces) {
        this.studyPlaces = studyPlaces;
    }

}
