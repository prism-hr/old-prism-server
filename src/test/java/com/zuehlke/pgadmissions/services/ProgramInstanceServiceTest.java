package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgramInstanceServiceTest {

    @TestedObject
    private ProgramInstanceService service;

    @Mock
    @InjectIntoByType
    private ProgramService programService;

    @Mock
    @InjectIntoByType
    private ApplicationExportConfigurationService throttleService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @Mock
    @InjectIntoByType
    private ProgramDAO programDAO;

    @Test
    public void shouldCreateNewCustomProgramInstances() {
        ProgramInstanceService thisBean = EasyMockUnitils.createMock(ProgramInstanceService.class);
        Institution institution = new Institution();
        Program program = new Program();
        StudyOption fullTimeOption = new StudyOption().withInstitution(institution).withCode("F").withName("Full-time").withEnabled(true);
        StudyOption partTimeOption = new StudyOption().withInstitution(institution).withCode("P").withName("Full-time").withEnabled(true);

        expect(applicationContext.getBean(ProgramInstanceService.class)).andReturn(thisBean);
        expect(thisBean.getStudyOptions("F,P")).andReturn(Lists.newArrayList(fullTimeOption, partTimeOption));
        expect(thisBean.getFirstProgramInstanceStartYear(isA(LocalDate.class))).andReturn(2013);
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
        LocalDate now = new LocalDate(2014, 1, 22);
        int startYear = service.getFirstProgramInstanceStartYear(now);
        assertEquals(2013, startYear);
    }

    @Test
    public void shouldGetCustomProgramInstanceStartYearWhenAfterSeptemberMonday() {
        LocalDate now = new LocalDate(2014, 9, 22);
        int startYear = service.getFirstProgramInstanceStartYear(now);
        assertEquals(2014, startYear);
    }

    @Test
    public void shouldFindPenultimateSeptemberMonday() {
        assertEquals(new DateTime(2013, 9, 23, 0, 0), service.findPenultimateSeptemberMonday(2013));
        assertEquals(new DateTime(2014, 9, 22, 0, 0), service.findPenultimateSeptemberMonday(2014));
        assertEquals(new DateTime(2015, 9, 21, 0, 0), service.findPenultimateSeptemberMonday(2015));
    }

}
