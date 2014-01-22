package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgramInstanceServiceTest {

    @TestedObject
    private ProgramInstanceService service;

    @Mock
    @InjectIntoByType
    private ProgramInstanceDAO programInstanceDAO;

    @Mock
    @InjectIntoByType
    private ThrottleService throttleService;

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfLargestEndDateIsBeforeToday() {
        DateTime instance1StartDate = new DateTime(2011, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2011, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2012, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2012, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(true).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(true).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(true).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the largest possible end date is " + instance2EndDate.toString() + " which is before today",
                service.isProgrammeStillAvailable(applicationForm));
    }

    @Test
    public void shouldReturnTrueForIsProgrammeStillAvailableIfLargestEndDateIsAfterToday() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(true).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(true).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(true).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertTrue("Should have returned true because the largest possible end date is " + instance2EndDate.toString() + " which is after today",
                service.isProgrammeStillAvailable(applicationForm));
    }

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfProgrammeIsNotEnabled() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(true).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(true).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(false).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the programme is not enabled", service.isProgrammeStillAvailable(applicationForm));
    }

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfNoInstancesAreEnabled() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(false).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(false).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(false).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("F+++++", "Full-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the programme instances are not enabled", service.isProgrammeStillAvailable(applicationForm));
    }

    @Test
    public void shouldReturnFalseForIsProgrammeStillAvailableIfStudyOptionsDoNotMatch() {
        DateTime instance1StartDate = new DateTime(2013, 1, 1, 8, 0);
        DateTime instance1EndDate = new DateTime(2013, 8, 1, 8, 0);

        DateTime instance2StartDate = new DateTime(2014, 10, 1, 8, 0);
        DateTime instance2EndDate = new DateTime(2014, 6, 1, 8, 0);

        ProgramInstance instance1 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance1StartDate.toDate())
                .enabled(false).applicationDeadline(instance1EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        ProgramInstance instance2 = new ProgramInstanceBuilder().id(Integer.MAX_VALUE).academicYear("2013").applicationStartDate(instance2StartDate.toDate())
                .enabled(false).applicationDeadline(instance2EndDate.toDate()).studyOption("F+++++", "Full-time").identifier("0009").build();

        Program program = new ProgramBuilder().id(Integer.MAX_VALUE).code("TMRMBISING99").enabled(false).instances(instance1, instance2)
                .title("MRes Medical and Biomedical Imaging").build();

        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder().id(Integer.MAX_VALUE).programmeName("MRes Medical and Biomedical Imaging")
                .projectName("Project Title").startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1)).studyOption("H+++++", "Part-time")
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(Integer.MAX_VALUE).program(program).programmeDetails(programDetails).build();

        assertFalse("Should have returned false because the study options do not match the programme instances",
                service.isProgrammeStillAvailable(applicationForm));
    }

    @Test
    public void shouldReturnTrueForIsActiveIfProgramInstanceIsEnabled() {
        ProgramInstance programInstance = new ProgramInstanceBuilder().enabled(true).build();
        assertTrue(service.isActive(programInstance));
    }

    @Test
    public void shouldReturnTrueForIsActiveIfProgramInstanceIsDisabledForLessThanAMonth() {
        DateTime weekAgo = new DateTime().minusWeeks(1);
        ProgramInstance programInstance = new ProgramInstanceBuilder().enabled(false).disabledDate(weekAgo.toDate()).build();

        EasyMock.expect(throttleService.getProcessingDelayInDays()).andReturn(30);

        replay();
        boolean isActive = service.isActive(programInstance);
        verify();
        assertTrue(isActive);
    }

    @Test
    public void shouldReturnFalseForIsActiveIfProgramInstanceIsDisabledForMoreThanAMonth() {
        DateTime weekAgo = new DateTime().minusWeeks(5);
        ProgramInstance programInstance = new ProgramInstanceBuilder().enabled(false).disabledDate(weekAgo.toDate()).build();

        EasyMock.expect(throttleService.getProcessingDelayInDays()).andReturn(30);

        replay();
        boolean isActive = service.isActive(programInstance);
        verify();
        assertFalse(isActive);
    }

    @Test
    public void shouldGetDistinctStudyOptions() {
        List<String[]> options = Lists.newArrayList(new String[] { "code1", "option1" }, new String[] { "code2", "option2" });

        expect(programInstanceDAO.getDistinctStudyOptions()).andReturn(options);

        replay();
        List<StudyOption> studyOptions = service.getDistinctStudyOptions();
        verify();

        assertThat(studyOptions, containsInAnyOrder(new StudyOption("code1", "option1"), new StudyOption("code2", "option2")));

    }

}
