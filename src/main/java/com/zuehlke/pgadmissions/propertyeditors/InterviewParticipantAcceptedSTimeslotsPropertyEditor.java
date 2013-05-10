package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.InterviewTimeslotDAO;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;

@Component
public class InterviewParticipantAcceptedSTimeslotsPropertyEditor extends PropertyEditorSupport {

    @Autowired
    private InterviewTimeslotDAO timeslotDAO;
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }
        int id = Integer.parseInt(text);
        InterviewTimeslot timeslot = timeslotDAO.getTimeslotById(id);
        setValue(timeslot);
    }
}
