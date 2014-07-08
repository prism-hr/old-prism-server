package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;
import org.joda.time.DateTime;

import java.util.Date;

public class JodaTimeConverter extends DozerConverter<Date, DateTime> {

    public JodaTimeConverter() {
        super(Date.class, DateTime.class);
    }


    @Override
    public DateTime convertTo(Date source, DateTime destination) {
        return new DateTime(source);
    }

    @Override
    public Date convertFrom(DateTime source, Date destination) {
        return source.toDate();
    }
}
