package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgramsServiceTest {

    @Mock
    @InjectIntoByType
    private ProgramDAO programDAOMock;

    @Mock
    @InjectIntoByType
    private InstitutionService qualificationInstitutionService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @TestedObject
    private ProgramService programsService;

    @Test
    public void shouldGetAllPrograms() {
        Program programOne = EasyMock.createMock(Program.class);
        Program programTwo = EasyMock.createMock(Program.class);
        EasyMock.expect(programDAOMock.getAllEnabledPrograms()).andReturn(Arrays.asList(programOne, programTwo));

        replay();
        List<Program> allPrograms = programsService.getAllEnabledPrograms();
        verify();

        assertEquals(2, allPrograms.size());
        assertTrue(allPrograms.contains(programOne));
        assertTrue(allPrograms.contains(programTwo));
    }

    @Test
    public void shouldGetProgramById() {
        Program program = EasyMock.createMock(Program.class);
        program.setId(2);
        EasyMock.expect(programDAOMock.getById(2)).andReturn(program);

        replay();
        assertEquals(program, programsService.getById(2));
        verify();
    }

    @Test
    public void shouldGetProgramByCode() {
        Program program = EasyMock.createMock(Program.class);
        EasyMock.expect(programDAOMock.getProgramByCode("code")).andReturn(program);

        replay();
        assertEquals(program, programsService.getProgramByCode("code"));
        verify();
    }

    @Test
    public void shouldDelegateSaveToDAO() {
        Program program = EasyMock.createMock(Program.class);
        programDAOMock.save(program);

        replay();
        programsService.save(program);
        verify();
    }

    @Test
    public void shouldGetProgramsForWhichCanManageProjects() {
        User userMock = EasyMockUnitils.createMock(User.class);

        List<Program> programs = Collections.emptyList();
        EasyMock.expect(programDAOMock.getProgramsForWhichUserCanManageProjects(userMock)).andReturn(programs);

        replay();
        List<Program> returnedPrograms = programsService.getProgramsForWhichCanManageProjects(userMock);
        verify();

        assertSame(programs, returnedPrograms);
    }

    @Test
    public void shouldApplyScoringDefinition() {
        Program program = new Program();

        EasyMock.expect(programDAOMock.getProgramByCode("any_code")).andReturn(program);

        replay();
        programsService.applyScoringDefinition("any_code", ScoringStage.REFERENCE, "Siala baba mak");
        verify();

        ScoringDefinition definition = program.getScoringDefinitions().get(ScoringStage.REFERENCE);
        assertNotNull(definition);
        assertEquals("Siala baba mak", definition.getContent());
    }

    @Test
    public void shouldGetProjectById() {
        Project project = EasyMock.createMock(Project.class);
        expect(programDAOMock.getById(1)).andReturn(project);

        replay();
        assertEquals(project, programsService.getById(1));
        verify();
    }

    @Test
    public void shouldDelegateSaveProjectToDAO() {
        Project project = EasyMock.createMock(Project.class);
        programDAOMock.save(project);

        replay();
        programsService.save(project);
        verify();
    }

    @Test
    public void shouldDisableProjectAndAdvertOnRemoveProject() {
        Project project = new Project();
        expect(programDAOMock.getById(1)).andReturn(project);
        programDAOMock.save(project);

        replay();
        programsService.removeProject(1);
        verify();

        assertEquals(PrismState.PROJECT_DISABLED, project.getState().getId());
    }

    @Test
    public void shouldGenerateNextProgramCode() {
        Institution institution = new Institution();
        Program lastCustomProgram = new Program().withCode("00018");

        expect(programDAOMock.getLastCustomProgram(institution)).andReturn(lastCustomProgram);

        replay();
        String nextCode = programsService.generateNextProgramCode(institution);
        verify();

        assertEquals("00019", nextCode);
    }

    @Test
    public void shouldUpdateClosingDate() {
        Program program = new Program().withCode("AAA_00018").withDescription("program").withStudyDuration(12).withState(new State().withId(PrismState.PROGRAM_APPROVED));
        AdvertClosingDate closingDate = new AdvertClosingDate().withClosingDate(new LocalDate()).withAdvert(program);
        programDAOMock.updateClosingDate(closingDate);
        replay();
        programsService.updateClosingDate(closingDate);
        verify();
    }

    @Test
    public void shouldAddClosingDateToProgram() {
        Program program = new Program().withCode("AAA_00018").withDescription("program").withStudyDuration(12).withState(new State().withId(PrismState.PROGRAM_APPROVED));
        AdvertClosingDate closingDate = new AdvertClosingDate().withClosingDate(new LocalDate()).withAdvert(program);
        programDAOMock.save(program);
        replay();
        programsService.addClosingDateToProgram(program, closingDate);
        verify();
    }

    @Test
    public void shouldDeleteClosingDateById() {
        AdvertClosingDate closingDate = new AdvertClosingDate().withClosingDate(new LocalDate());
        expect(programDAOMock.getClosingDateById(closingDate.getId())).andReturn(closingDate);
        programDAOMock.deleteClosingDate(closingDate);
        replay();
        programsService.deleteClosingDateById(closingDate.getId());
        verify();
    }

    @Test
    public void shouldInitialProgramCode() {
        Institution institution = new Institution();

        expect(programDAOMock.getLastCustomProgram(institution)).andReturn(null);

        replay();
        String nextCode = programsService.generateNextProgramCode(institution);
        verify();

        assertEquals("00000", nextCode);
    }

    @Test(expected = CannotApplyException.class)
    public void shouldThrowCannotApplyExceptionIfProgramAndProjectAreNull() {
        replay();
        programsService.getValidProgramProjectAdvert(null);
        verify();
    }

    @Test(expected = CannotApplyException.class)
    public void shouldThrowCannotApplyExceptionIfAdvertNotActive() {
        EasyMock.expect(programDAOMock.getAcceptingApplicationsById(0)).andReturn(null);
        replay();
        programsService.getValidProgramProjectAdvert(0);
        verify();
    }

    @Test
    public void shouldReturnAdvertByProjectIdIfProjectIsActive() {
        Project project = new Project();
        EasyMock.expect(programDAOMock.getAcceptingApplicationsById(8)).andReturn(project);

        replay();
        Advert advert = programsService.getValidProgramProjectAdvert(8);

        assertEquals(advert.getProject(), project);
    }

}
