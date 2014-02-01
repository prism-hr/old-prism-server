package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgramsServiceTest {

    @Mock
    @InjectIntoByType
    private ProgramDAO programDAOMock;

    @Mock
    @InjectIntoByType
    private AdvertDAO advertDAOMock;

    @Mock
    @InjectIntoByType
    private ProjectDAO projectDAOMock;

    @Mock
    @InjectIntoByType
    private QualificationInstitutionService qualificationInstitutionService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @TestedObject
    private ProgramsService programsService;

    @Test
    public void shouldGetAllPrograms() {
        Program programOne = EasyMock.createMock(Program.class);
        Program programTwo = EasyMock.createMock(Program.class);
        EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));

        replay();
        List<Program> allPrograms = programsService.getAllPrograms();
        verify();

        assertEquals(2, allPrograms.size());
        assertTrue(allPrograms.contains(programOne));
        assertTrue(allPrograms.contains(programTwo));
    }

    @Test
    public void shouldGetProgramById() {
        Program program = EasyMock.createMock(Program.class);
        program.setId(2);
        EasyMock.expect(programDAOMock.getProgramById(2)).andReturn(program);

        replay();
        assertEquals(program, programsService.getProgramById(2));
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
    public void shouldGetProgramsForWhichCanManageProjectsIfAdmin() {
        RegisteredUser userMock = EasyMockUnitils.createMock(RegisteredUser.class);

        List<Program> programs = Collections.emptyList();
        EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(programs);
        EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);

        replay();
        List<Program> returnedPrograms = programsService.getProgramsForWhichCanManageProjects(userMock);
        verify();

        assertSame(programs, returnedPrograms);
    }

    @Test
    public void shouldGetProgramsForWhichCanManageProjectsIfNotAdmin() {
        RegisteredUser userMock = EasyMockUnitils.createMock(RegisteredUser.class);

        EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        EasyMock.expect(userMock.getProgramsOfWhichAdministrator()).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(userMock.getProgramsOfWhichApprover()).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(programDAOMock.getProgramsOfWhichPreviousReviewer(userMock)).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(programDAOMock.getProgramsOfWhichPreviousInterviewer(userMock)).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(programDAOMock.getProgramsOfWhichPreviousSupervisor(userMock)).andReturn(Collections.<Program> emptyList());

        replay();
        programsService.getProgramsForWhichCanManageProjects(userMock);
        verify();
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
        expect(projectDAOMock.getProjectById(1)).andReturn(project);

        replay();
        assertEquals(project, programsService.getProject(1));
        verify();
    }

    @Test
    public void shouldDelegateSaveProjectToDAO() {
        Project project = EasyMock.createMock(Project.class);
        projectDAOMock.save(project);

        replay();
        programsService.saveProject(project);
        verify();
    }

    @Test
    public void shouldDisableProjectAndAdvertOnRemoveProject() {
        Project project = new ProjectBuilder().advert(new AdvertBuilder().build()).build();
        expect(projectDAOMock.getProjectById(1)).andReturn(project);
        projectDAOMock.save(project);

        replay();
        programsService.removeProject(1);
        verify();

        assertTrue(project.isDisabled());
        assertFalse(project.getAdvert().getActive());
    }

    @Test
    public void shouldReturnAllProjectsForSuperAdministrator() {
        Program program = EasyMock.createMock(Program.class);
        RegisteredUser superAdmin = EasyMockUnitils.createMock(RegisteredUser.class);
        expect(superAdmin.isInRole(superAdmin, Authority.SUPERADMINISTRATOR)).andReturn(true);
        List<Project> allProjects = Collections.emptyList();
        expect(projectDAOMock.getProjectsForProgram(program)).andReturn(allProjects);

        replay();
        List<Project> loadedProjects = programsService.listProjects(superAdmin, program);
        verify();

        assertSame(allProjects, loadedProjects);
    }

    @Test
    public void shouldReturnAllProjectsForProgramAdministrator() {
        Program program = EasyMock.createMock(Program.class);
        RegisteredUser admin = EasyMockUnitils.createMock(RegisteredUser.class);
        expect(admin.isInRole(admin, Authority.SUPERADMINISTRATOR)).andReturn(false);
        expect(admin.isAdminInProgramme(program)).andReturn(true);
        List<Project> allProjects = Collections.emptyList();
        expect(projectDAOMock.getProjectsForProgram(program)).andReturn(allProjects);

        replay();
        List<Project> loadedProjects = programsService.listProjects(admin, program);
        verify();

        assertSame(allProjects, loadedProjects);
    }

    @Test
    public void shouldReturnProjectsForProgramOfAuthor() {
        Program program = EasyMockUnitils.createMock(Program.class);
        RegisteredUser user = EasyMockUnitils.createMock(RegisteredUser.class);
        expect(user.isInRole(user, Authority.SUPERADMINISTRATOR)).andReturn(false);
        expect(user.isAdminInProgramme(program)).andReturn(false);
        List<Project> allProjects = Collections.emptyList();
        expect(projectDAOMock.getProjectsForProgramOfWhichAuthor(program, user)).andReturn(allProjects);

        replay();
        List<Project> loadedProjects = programsService.listProjects(user, program);
        verify();

        assertSame(allProjects, loadedProjects);
    }

    @Test
    public void shouldCreateNewCustomProgram() {
        ProgramsService thisBean = EasyMockUnitils.createMock(ProgramsService.class);

        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, null).build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().build();

        expect(applicationContext.getBean(ProgramsService.class)).andReturn(thisBean);
        expect(qualificationInstitutionService.getOrCreateCustomInstitution(opportunityRequest)).andReturn(institution);
        Capture<Program> programCapture = new Capture<Program>();
        programDAOMock.save(capture(programCapture));
        expect(thisBean.generateNextProgramCode(institution)).andReturn("AAA_00000");

        replay();
        Program program = programsService.createNewCustomProgram(opportunityRequest);
        verify();

        Advert advert = program.getAdvert();

        assertTrue(advert.getActive());
        assertEquals(opportunityRequest.getProgramDescription(), advert.getDescription());
        assertEquals(opportunityRequest.getStudyDuration(), advert.getStudyDuration());

        assertSame(programCapture.getValue(), program);
        assertSame(advert, program.getAdvert());
        assertEquals(opportunityRequest.getAtasRequired(), program.getAtasRequired());
        assertSame(institution, program.getInstitution());
        assertEquals(opportunityRequest.getProgramTitle(), program.getTitle());
        assertEquals("AAA_00000", program.getCode());
    }

    @Test
    public void shouldGenerateNextProgramCode() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("AAA").build();
        Program lastCustomProgram = new ProgramBuilder().code("AAA_00018").build();

        expect(programDAOMock.getLastCustomProgram(institution)).andReturn(lastCustomProgram);

        replay();
        String nextCode = programsService.generateNextProgramCode(institution);
        verify();

        assertEquals("AAA_00019", nextCode);
    }
    
    @Test
    public void shouldUpdateClosingDate() {
        Advert advert = new AdvertBuilder().description("program").studyDuration(12).active(true).build();
        Program program = new ProgramBuilder().code("AAA_00018").advert(advert).build();
        ProgramClosingDate closingDate = new ProgramClosingDateBuilder().closingDate(new Date()).program(program).build();
        programDAOMock.updateClosingDate(closingDate);
        replay();
        programsService.updateClosingDate(closingDate);
        verify();
    }
    
    @Test
    public void shouldAddClosingDateToProgram() {
        Advert advert = new AdvertBuilder().description("program").studyDuration(12).active(true).build();
        Program program = new ProgramBuilder().code("AAA_00018").advert(advert).build();
        ProgramClosingDate closingDate = new ProgramClosingDateBuilder().closingDate(new Date()).program(program).build();
        programDAOMock.save(program);
        replay();
        programsService.addClosingDateToProgram(program, closingDate);
        verify();
    }
    
    @Test
    public void shouldDeleteClosingDateById() {
        ProgramClosingDate closingDate = new ProgramClosingDateBuilder().closingDate(new Date()).build();
        expect(programDAOMock.getClosingDateById(closingDate.getId())).andReturn(closingDate);
        programDAOMock.deleteClosingDate(closingDate);
        replay();
        programsService.deleteClosingDateById(closingDate.getId());
        verify();
    }

    @Test
    public void shouldInitialProgramCode() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("AAA").build();

        expect(programDAOMock.getLastCustomProgram(institution)).andReturn(null);

        replay();
        String nextCode = programsService.generateNextProgramCode(institution);
        verify();

        assertEquals("AAA_00000", nextCode);
    }

}
