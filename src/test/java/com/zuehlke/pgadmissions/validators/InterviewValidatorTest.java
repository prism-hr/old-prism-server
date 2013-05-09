package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewTimeslotBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class InterviewValidatorTest {

    @Autowired
    private Validator validator;

    private Interview interview;

    private InterviewValidator interviewValidator;

    @Test
    public void shouldSupportRefereeValidator() {
        assertTrue(interviewValidator.supports(Interview.class));
    }

    @Test
    public void shouldRejectIfDueDateIsEmpty() {
        interview.setInterviewDueDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewDueDate");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("interviewDueDate").getCode());
    }

    @Test
    public void shouldRejectIfInterviewScheduledInPast() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        interview.setStage(InterviewStage.SCHEDULED);
        interview.setInterviewDueDate(calendar.getTime());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewDueDate");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldError("interviewDueDate").getCode());
    }

    @Test
    public void shouldRejectIfInterviewTookPlaceInFuture() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        interview.setStage(InterviewStage.SCHEDULED);
        interview.setTakenPlace(true);
        interview.setInterviewDueDate(calendar.getTime());
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewDueDate");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("interviewDueDate").getCode());
    }

    @Test
    public void shouldRejectIfInterviewStageNotSpecified() {
        interview.setStage(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interview");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("stage").getCode());
    }

    @Test
    public void shouldRejectIfInterviewStageIsInitial() {
        interview.setStage(InterviewStage.INITIAL);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interview");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("stage").getCode());
    }

    @Test
    public void shouldRejectIfDurationNotProvided() {
        interview.setDuration(null);
        interview.setStage(InterviewStage.SCHEDULED);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interview");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("duration").getCode());
    }

    @Test
    public void shouldNotRejectIfDueDateToday() {
        Calendar calendar = Calendar.getInstance();

        interview.setInterviewDueDate(DateUtils.truncate(calendar.getTime(), Calendar.DATE));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewDueDate");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfTimeHoursIsEmpty() {
        interview.setInterviewTime(":45");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewTime");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("timeHours").getCode());
    }

    @Test
    public void shouldRejectIfTimeMinutesIsEmpty() {
        interview.setInterviewTime("12:");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewTime");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("timeMinutes").getCode());
    }

    @Test
    public void shouldRejectOnlyHoursIfBothAreEmpty() {
        interview.setInterviewTime(":");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewTime");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("timeHours").getCode());
    }

    @Test
    public void shouldRejectIfInterviewersListIsEmpty() {
        interview.getInterviewers().clear();
        interview.setDuration(0);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewers");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("interviewers").getCode());
    }

     @Test
     public void shouldRejectIfNoTimezone() {
     interview.setTimeZone(null);
    
     DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "timeZone");
     interviewValidator.validate(interview, mappingResult);
     Assert.assertEquals(1, mappingResult.getErrorCount());
     Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("timeZone").getCode());
     }

    @Test
    public void shouldRejectIfNoAvailableDatesAndTimesWereProvided() {
        interview.setStage(InterviewStage.SCHEDULING);
        interview.getTimeslots().clear();
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "timeslots");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("datepicker.field.mustselectdate", mappingResult.getFieldError("timeslots").getCode());
    }
    
    @Test
    public void shouldRejectIfAnyDateIsInThePast() {
        interview.setStage(InterviewStage.SCHEDULING);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        
        InterviewTimeslot timeslot = interview.getTimeslots().get(0);
        timeslot.setDueDate(calendar.getTime());
        timeslot.setStartTime("10:00");
        
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "timeslots");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("datepicker.field.mustselectdatetimesinfuture", mappingResult.getFieldError("timeslots").getCode());
    }
    
    @Test
    public void shouldRejectIfDateIsPresentDayButTimeIsInThePast() {
        interview.setStage(InterviewStage.SCHEDULING);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
        
        InterviewTimeslot timeslot = interview.getTimeslots().get(0);
        timeslot.setDueDate(calendar.getTime());
        timeslot.setStartTime("10:00");
        
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "timeslots");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("datepicker.field.mustselectdatetimesinfuture", mappingResult.getFieldError("timeslots").getCode());
    }
    
    @Test
    public void shouldRejectIfAnyTimeIsNotProperlyFormatted() {
        interview.setStage(InterviewStage.SCHEDULING);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        
        InterviewTimeslot timeslot = interview.getTimeslots().get(0);
        timeslot.setDueDate(calendar.getTime());
        timeslot.setStartTime("29:10");
        
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "timeslots");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("time.field.invalid", mappingResult.getFieldError("timeslots").getCode());
    }

    @Test
    public void shouldRejectIfTakenPlaceFlagNotSpecified() {
        interview.setTakenPlace(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "timeslots");
        interviewValidator.validate(interview, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("stage").getCode());
    }

    @Before
    public void setup() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        interview = new InterviewBuilder().stage(InterviewStage.SCHEDULED).takenPlace(false).interviewTime("09:00")
                .application(new ApplicationFormBuilder().id(2).build()).dueDate(calendar.getTime()).furtherDetails("at 9 pm")
                .locationURL("http://www.ucl.ac.uk").interviewers(new InterviewerBuilder().id(4).build()).duration(120).build();

        List<InterviewTimeslot> timeslots = new ArrayList<InterviewTimeslot>();
        timeslots.add(new InterviewTimeslotBuilder().build());
        interview.setTimeslots(timeslots);

        interviewValidator = new InterviewValidator();
        interviewValidator.setValidator((javax.validation.Validator) validator);
    }
}
