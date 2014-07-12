package com.zuehlke.pgadmissions.rest.converter;

import java.util.Date;

import org.dozer.DozerConverter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class LocalDateConverter extends DozerConverter<LocalDate, LocalDate> {

    public LocalDateConverter() {
        super(LocalDate.class, LocalDate.class);
    }

    @Override
    public LocalDate convertTo(LocalDate source, LocalDate destination) {
        return source;
    }

    @Override
    public LocalDate convertFrom(LocalDate source, LocalDate destination) {
        return source;
    }
}
