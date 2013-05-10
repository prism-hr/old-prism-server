package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

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
        
        Set<InterviewTimeslot> acceptedTimeslots = new HashSet<InterviewTimeslot>();
        
        String[] ids = text.split(",");
        
        for (String id : ids) {
            int parsedId = Integer.parseInt(id);
            
            InterviewTimeslot timeslot = timeslotDAO.getTimeslotById(parsedId);
            
            if (timeslot != null) {
                acceptedTimeslots.add(timeslot);
            }
        }
        
        setValue(acceptedTimeslots);
    }
}
