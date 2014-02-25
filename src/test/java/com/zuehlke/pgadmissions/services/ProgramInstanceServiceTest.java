package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
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
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
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

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

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
        List<Object[]> options = Lists.newArrayList(new Object[] { "code1", "option1" }, new Object[] { "code2", "option2" });

        expect(programInstanceDAO.getDistinctStudyOptions()).andReturn(options);

        replay();
        List<StudyOption> studyOptions = service.getDistinctStudyOptions();
        verify();

        assertThat(studyOptions, containsInAnyOrder(new StudyOption("code1", "option1"), new StudyOption("code2", "option2")));
    }

    @Test
    public void shouldCreateNewCustomProgramInstances() {
        ProgramInstanceService thisBean = EasyMockUnitils.createMock(ProgramInstanceService.class);
        Program program = new Program();
        StudyOption fullTimeOption = new StudyOption("F", "Full-time");
        StudyOption partTimeOption = new StudyOption("P", "Part-time");

        expect(applicationContext.getBean(ProgramInstanceService.class)).andReturn(thisBean);
        expect(thisBean.getStudyOptions("F,P")).andReturn(Lists.newArrayList(fullTimeOption, partTimeOption));
        expect(thisBean.getFirstProgramInstanceStartYear(isA(DateTime.class))).andReturn(2013);
        expect(thisBean.createOrUpdateProgramInstance(program, 2013, fullTimeOption)).andReturn(new ProgramInstance());
        expect(thisBean.createOrUpdateProgramInstance(program, 2013, partTimeOption)).andReturn(new ProgramInstance());
        expect(thisBean.createOrUpdateProgramInstance(program, 2014, fullTimeOption)).andReturn(new ProgramInstance());
        expect(thisBean.createOrUpdateProgramInstance(program, 2014, partTimeOption)).andReturn(new ProgramInstance());
        expect(thisBean.createOrUpdateProgramInstance(program, 2015, fullTimeOption)).andReturn(new ProgramInstance());
        expect(thisBean.createOrUpdateProgramInstance(program, 2015, partTimeOption)).andReturn(new ProgramInstance());

        replay();
        List<ProgramInstance> instances = service.createRemoveProgramInstances(program, "F,P", 2016);
        verify();

        assertEquals(6, instances.size());
    }

    @Test
    public void shouldGetCustomProgramInstanceStartYearWhenBeforeSeptemberMonday() {
        DateTime now = new DateTime(2014, 1, 22, 0, 0);
        int startYear = service.getFirstProgramInstanceStartYear(now);
        assertEquals(2013, startYear);
    }

    @Test
    public void shouldGetCustomProgramInstanceStartYearWhenAfterSeptemberMonday() {
        DateTime now = new DateTime(2014, 9, 22, 0, 0);
        int startYear = service.getFirstProgramInstanceStartYear(now);
        assertEquals(2014, startYear);
    }

    @Test
    public void shouldFindPenultimateSeptemberMonday() {
        assertEquals(new DateTime(2013, 9, 23, 0, 0), service.findPenultimateSeptemberMonday(2013));
        assertEquals(new DateTime(2014, 9, 22, 0, 0), service.findPenultimateSeptemberMonday(2014));
        assertEquals(new DateTime(2015, 9, 21, 0, 0), service.findPenultimateSeptemberMonday(2015));
    }

    @Test
    public void shouldGetStudyOptions() {
        ProgramInstanceService thisBean = EasyMockUnitils.createMock(ProgramInstanceService.class);

        expect(applicationContext.getBean(ProgramInstanceService.class)).andReturn(thisBean);
        StudyOption partOption = new StudyOption("P+++", "Part-time");
        StudyOption fullOption = new StudyOption("F+++", "Fart-time");
        StudyOption modularOption = new StudyOption("B+++", "Modular");
        expect(thisBean.getDistinctStudyOptions()).andReturn(Lists.newArrayList(partOption, fullOption, modularOption));

        replay();
        List<StudyOption> studyOptions = service.getStudyOptions("P+++,F+++");
        verify();

        assertThat(studyOptions, containsInAnyOrder(partOption, fullOption));
    }

    @Test
    public void souldDisableLapsedInstances() {
        Program program1 = new ProgramBuilder().id(1).enabled(true).build();
        Program program2 = new ProgramBuilder().id(2).enabled(true).build();
        
        ProgramInstance instance1 = new ProgramInstanceBuilder().enabled(true).program(program1).build();
        ProgramInstance instance2 = new ProgramInstanceBuilder().enabled(true).program(program2).build();
        
        expect(programInstanceDAO.getLapsedInstances()).andReturn(Lists.newArrayList(instance1, instance2));
        expect(programInstanceDAO.getActiveProgramInstances(program1)).andReturn(Lists.newArrayList(new ProgramInstance()));
        expect(programInstanceDAO.getActiveProgramInstances(program2)).andReturn(Lists.<ProgramInstance>newArrayList());

        replay();
        service.disableLapsedInstances();
        verify();

        assertFalse(instance1.getEnabled());
        assertFalse(instance2.getEnabled());
        
        assertTrue(program1.isEnabled());
        assertFalse(program2.isEnabled());
    }

    @Test
    public void shouldUpdateProgramInstance() {
        ProgramInstanceService thisBean = EasyMockUnitils.createMock(ProgramInstanceService.class);
        Program program = new Program();
        DateTime monday2013 = new DateTime(2013, 9, 23, 0, 0);
        DateTime monday2014 = new DateTime(2014, 9, 22, 0, 0);
        StudyOption fullTimeOption = new StudyOption("F", "Full-time");
        ProgramInstance programInstance = new ProgramInstanceBuilder().program(program).build();

        expect(applicationContext.getBean(ProgramInstanceService.class)).andReturn(thisBean);
        expect(thisBean.findPenultimateSeptemberMonday(2013)).andReturn(monday2013);
        expect(thisBean.findPenultimateSeptemberMonday(2014)).andReturn(monday2014);
        expect(programInstanceDAO.getProgramInstance(program, fullTimeOption, monday2013.toDate())).andReturn(programInstance);
        programInstanceDAO.save(programInstance);

        replay();
        ProgramInstance returned = service.createOrUpdateProgramInstance(program, 2013, fullTimeOption);
        verify();

        assertSame(programInstance, returned);
        assertProgramInstance(programInstance, program, monday2013, "2013", monday2014, fullTimeOption);
    }

    @Test
    public void shouldCreateProgramInstance() {
        ProgramInstanceService thisBean = EasyMockUnitils.createMock(ProgramInstanceService.class);
        Program program = new Program();
        DateTime monday2013 = new DateTime(2013, 9, 23, 0, 0);
        DateTime monday2014 = new DateTime(2014, 9, 22, 0, 0);
        StudyOption fullTimeOption = new StudyOption("F", "Full-time");

        expect(applicationContext.getBean(ProgramInstanceService.class)).andReturn(thisBean);
        expect(thisBean.findPenultimateSeptemberMonday(2013)).andReturn(monday2013);
        expect(thisBean.findPenultimateSeptemberMonday(2014)).andReturn(monday2014);
        expect(programInstanceDAO.getProgramInstance(program, fullTimeOption, monday2013.toDate())).andReturn(null);
        programInstanceDAO.save(isA(ProgramInstance.class));

        replay();
        ProgramInstance returned = service.createOrUpdateProgramInstance(program, 2013, fullTimeOption);
        verify();

        assertProgramInstance(returned, program, monday2013, "2013", monday2014, fullTimeOption);
        assertThat(program.getInstances(), contains(returned));
    }

    private void assertProgramInstance(ProgramInstance programInstance, Program program, DateTime startDate, String academicYear, DateTime deadline,
            StudyOption studyOption) {
        assertEquals(startDate.toDate(), programInstance.getApplicationStartDate());
        assertEquals(academicYear, programInstance.getAcademic_year());
        assertEquals(deadline.toDate(), programInstance.getApplicationDeadline());
        assertEquals(deadline.minusMonths(1).toDate(), programInstance.getDisabledDate());
        assertTrue(programInstance.getEnabled());
        assertEquals("CUSTOM", programInstance.getIdentifier());
        assertSame(program, programInstance.getProgram());
        assertEquals(studyOption.getName(), programInstance.getStudyOption());
        assertEquals(studyOption.getId(), programInstance.getStudyOptionCode());
    }

}
