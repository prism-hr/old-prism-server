package com.zuehlke.pgadmissions.rest.dto.advert;

import org.joda.time.LocalDate;

public class AdvertClosingDateDTO {

    private LocalDate closingDate;

    public final LocalDate getClosingDate() {
        return closingDate;
    }

    public final void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

}
