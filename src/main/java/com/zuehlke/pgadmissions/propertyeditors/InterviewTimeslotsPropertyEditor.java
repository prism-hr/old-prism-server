package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zuehlke.pgadmissions.domain.AppointmentTimeslot;

@Component
public class InterviewTimeslotsPropertyEditor extends PropertyEditorSupport {

    private Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }
        final Type scoresListType = new TypeToken<List<AppointmentTimeslot>>() {
        }.getType();

        final List<AppointmentTimeslot> scores = gson.fromJson(text, scoresListType);
        setValue(scores);
    }

}
