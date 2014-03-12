package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SubmitApplicationFomServiceTest {

    @Mock
    @InjectIntoByType
    private StageDurationService stageDurationServiceMock;

    @TestedObject
    private SubmitApplicationFormService service;

    @Test
    public void shouldSetValidationDateAfterOneWorkingDayOfBatchDeadlineIfBatchDeadlineIsSetAndValidationStageDurationIsOneDay() throws ParseException {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/12/12")).build();
        StageDuration stageDurationMock = EasyMockUnitils.createMock(StageDuration.class);
        expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDurationMock);
        expect(stageDurationMock.getDurationInMinutes()).andReturn(1440);

        EasyMockUnitils.replay();
        service.assignValidationDueDate(applicationForm);

        Date oneDayMore = new SimpleDateFormat("yyyy/MM/dd").parse("2012/12/14");
        Assert.assertEquals(String.format("Dates are not the same [%s] [%s]", oneDayMore, applicationForm.getDueDate()), oneDayMore,
                applicationForm.getDueDate());
    }

    @Test
    public void shouldSetValidationDateToCurrentDatePlusValidationStageIntervalWorkingDayIfBatchDeadlineIsNotSet() throws ParseException {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.UNSUBMITTED).build();
        StageDuration stageDurationMock = createMock(StageDuration.class);
        expect(stageDurationServiceMock.getByStatus(ApplicationFormStatus.VALIDATION)).andReturn(stageDurationMock);
        expect(stageDurationMock.getDurationInMinutes()).andReturn(1440);

        replay();
        service.assignValidationDueDate(applicationForm);

        Date dayAfterTomorrow = com.zuehlke.pgadmissions.utils.DateUtils.addWorkingDaysInMinutes(new Date(), 1440);
        Assert.assertTrue(String.format("Dates are not on the same day [%s] [%s]", dayAfterTomorrow, applicationForm.getDueDate()),
                DateUtils.isSameDay(dayAfterTomorrow, applicationForm.getDueDate()));
    }
}
