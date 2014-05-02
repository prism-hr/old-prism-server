package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
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
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.AdvertClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.enums.ProgramState;
import com.zuehlke.pgadmissions.domain.enums.ProjectState;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgramsServiceTest {

    @Mock
    @InjectIntoByType
    private ProgramDAO programDAOMock;

    @Mock
    @InjectIntoByType
    private QualificationInstitutionService qualificationInstitutionService;

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
        Program program = new ProgramBuilder().build();

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

        assertEquals(ProjectState.PROJECT_DISABLED, project.getState());
    }

    @Test
    public void shouldCreateNewCustomProgram() {
        Domicile domicile = new Domicile();
        ProgramService thisBean = EasyMockUnitils.createMock(ProgramService.class);

        User requestAuthor = new User();
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(requestAuthor, domicile).otherInstitution("other_name").build();
        Institution institution = new Institution();

        expect(applicationContext.getBean(ProgramService.class)).andReturn(thisBean);
        expect(qualificationInstitutionService.getOrCreate("AGH", domicile, "other_name")).andReturn(institution);
        Capture<Program> programCapture = new Capture<Program>();
        expect(thisBean.generateNextProgramCode(institution)).andReturn("AAA_00000");
        programDAOMock.save(capture(programCapture));

        replay();
        Program program = programsService.createOrGetProgram(opportunityRequest);
        verify();

        assertSame(program.getContactUser(), requestAuthor);
        assertSame(programCapture.getValue(), program);
        assertEquals(opportunityRequest.getAtasRequired(), program.getAtasRequired());
        assertSame(institution, program.getInstitution());
        assertEquals(opportunityRequest.getProgramTitle(), program.getTitle());
        assertEquals("AAA_00000", program.getCode());
    }

    @Test
    public void shouldGetCustomProgram() {
        ProgramService thisBean = EasyMockUnitils.createMock(ProgramService.class);
        Program program = new ProgramBuilder().institution(new Institution().withCode("any_inst")).build();
        User requestAuthor = new User();
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(requestAuthor, null).institutionCode("any_inst")
                .atasRequired(true).sourceProgram(program).acceptingApplications(true).build();

        expect(applicationContext.getBean(ProgramService.class)).andReturn(thisBean);
        expect(thisBean.getContactUserForProgram(program, requestAuthor)).andReturn(requestAuthor);
        programDAOMock.merge(program);
        programDAOMock.save(program);

        replay();
        Program returned = programsService.createOrGetProgram(opportunityRequest);
        verify();

        assertTrue(returned.getAtasRequired());
        assertEquals(program.getTitle(), opportunityRequest.getProgramTitle());
        assertEquals(program.getDescription(), opportunityRequest.getProgramDescription());
        assertEquals(program.getAtasRequired(), opportunityRequest.getAtasRequired());
        assertEquals(program.getStudyDuration(), opportunityRequest.getStudyDuration());
        assertEquals(program.getFunding(), opportunityRequest.getFunding());
        assertEquals(ProgramState.PROGRAM_APPROVED, program.getState());
        assertSame(program.getProgramType(), opportunityRequest.getProgramType());
        assertSame(program.getContactUser(), requestAuthor);
    }

    @Test
    public void shouldGenerateNextProgramCode() {
        Institution institution = new Institution().withCode("AAA");
        Program lastCustomProgram = new ProgramBuilder().code("AAA_00018").build();

        expect(programDAOMock.getLastCustomProgram(institution)).andReturn(lastCustomProgram);

        replay();
        String nextCode = programsService.generateNextProgramCode(institution);
        verify();

        assertEquals("AAA_00019", nextCode);
    }

    @Test
    public void shouldUpdateClosingDate() {
        Program program = new ProgramBuilder().code("AAA_00018").description("program").studyDuration(12).state(ProgramState.PROGRAM_APPROVED).build();
        AdvertClosingDate closingDate = new AdvertClosingDateBuilder().closingDate(new Date()).advert(program).build();
        programDAOMock.updateClosingDate(closingDate);
        replay();
        programsService.updateClosingDate(closingDate);
        verify();
    }

    @Test
    public void shouldAddClosingDateToProgram() {
        Program program = new ProgramBuilder().code("AAA_00018").description("program").studyDuration(12).state(ProgramState.PROGRAM_APPROVED).build();
        AdvertClosingDate closingDate = new AdvertClosingDateBuilder().closingDate(new Date()).advert(program).build();
        programDAOMock.save(program);
        replay();
        programsService.addClosingDateToProgram(program, closingDate);
        verify();
    }

    @Test
    public void shouldDeleteClosingDateById() {
        AdvertClosingDate closingDate = new AdvertClosingDateBuilder().closingDate(new Date()).build();
        expect(programDAOMock.getClosingDateById(closingDate.getId())).andReturn(closingDate);
        programDAOMock.deleteClosingDate(closingDate);
        replay();
        programsService.deleteClosingDateById(closingDate.getId());
        verify();
    }

    @Test
    public void shouldInitialProgramCode() {
        Institution institution = new Institution().withCode("AAA");

        expect(programDAOMock.getLastCustomProgram(institution)).andReturn(null);

        replay();
        String nextCode = programsService.generateNextProgramCode(institution);
        verify();

        assertEquals("AAA_00000", nextCode);
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
        Project project = new ProjectBuilder().build();
        EasyMock.expect(programDAOMock.getAcceptingApplicationsById(8)).andReturn(project);

        replay();
        Advert advert = programsService.getValidProgramProjectAdvert(8);

        assertEquals(advert.getProject(), project);
    }

}
