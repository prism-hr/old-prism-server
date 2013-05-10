package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Type;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;

@Component
public class InterviewParticipantAcceptedSTimeslotsPropertyEditor extends PropertyEditorSupport {
    private Gson gson = new GsonBuilder().create();

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }
        final Type scoresListType = new TypeToken<Set<InterviewTimeslot>>() {
        }.getType();

        final Set<InterviewTimeslot> acceptedTimeslots = gson.fromJson(text, scoresListType);
        setValue(acceptedTimeslots);
    }
}
