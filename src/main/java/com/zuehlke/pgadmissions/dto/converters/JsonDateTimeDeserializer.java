package com.zuehlke.pgadmissions.dto.converters;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateTimeDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        String jsonDateTime = parser.getValueAsString();
        String[] dateParts = jsonDateTime.substring(0, 10).split("-");
        String[] timeParts = jsonDateTime.substring(11, 19).split(":");
        return new DateTime().withYear(Integer.parseInt(dateParts[0])).withMonthOfYear(Integer.parseInt(dateParts[1]))
                .withDayOfMonth(Integer.parseInt(dateParts[2])).withHourOfDay(Integer.parseInt(timeParts[0])).withMinuteOfHour(Integer.parseInt(timeParts[1]))
                .withSecondOfMinute(Integer.parseInt(timeParts[2]));
    }

}
