package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.InterviewTimeslot;

public class InterviewTimeslotsPropertyEditorTest {

    private InterviewTimeslotsPropertyEditor editor;
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateTimeslotListForSampleJson() {
        String json = "[{dueDate: '14/08/1989', startTime: '11:11'}, {dueDate: '16/08/1989', startTime: '12:12'}]";
        editor.setAsText(json);
        
        List<InterviewTimeslot> timeslots = (List<InterviewTimeslot>) editor.getValue();
        assertEquals(2, timeslots.size());
        InterviewTimeslot timeslot1 = timeslots.get(0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1989, 7, 14);
        assertEquals(DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH), timeslot1.getDueDate());
        assertEquals("11:11", timeslot1.getStartTime());
        
        InterviewTimeslot timeslot2 = timeslots.get(1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(1989, 7, 16);
        assertEquals(DateUtils.truncate(calendar2.getTime(), Calendar.DAY_OF_MONTH), timeslot2.getDueDate());
        assertEquals("12:12", timeslot2.getStartTime());
    }
    
    @Test
    public void shouldCreateEmptyTimeslotList() {
        String json = "[]";
        editor.setAsText(json);
        
        List<InterviewTimeslot> timeslots = (List<InterviewTimeslot>) editor.getValue();
        assertEquals(0, timeslots.size());
    }
    
    @Before
    public void prepare(){
        editor = new InterviewTimeslotsPropertyEditor();
    }

}
