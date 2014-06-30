package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.CommentAppointmentTimeslot;

@Component
public class AcceptedTimeslotsPropertyEditor extends PropertyEditorSupport {

    @Autowired
    private EntityDAO entityDAO;

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }

        Set<CommentAppointmentTimeslot> acceptedTimeslots = new HashSet<CommentAppointmentTimeslot>();

        String[] ids = text.split(",");

        for (String id : ids) {
            int parsedId = Integer.parseInt(id);

            CommentAppointmentTimeslot timeslot = entityDAO.getById(CommentAppointmentTimeslot.class, parsedId);

            if (timeslot != null) {
                acceptedTimeslots.add(timeslot);
            }
        }

        setValue(acceptedTimeslots);
    }
}
