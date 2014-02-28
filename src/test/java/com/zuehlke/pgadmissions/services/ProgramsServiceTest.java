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

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Domicile;
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
    private ProgramsService programsService;

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
        RegisteredUser userMock = EasyMockUnitils.createMock(RegisteredUser.class);

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
        Project project = new ProjectBuilder().advert(new AdvertBuilder().build()).build();
        expect(programDAOMock.getById(1)).andReturn(project);
        programDAOMock.save(project);

        replay();
        programsService.removeAdvert(1);
        verify();

        assertFalse(project.isEnabled());
        assertFalse(project.isActive());
    }

    @Test
    public void shouldReturnAllProjectsForSuperAdministrator() {
        Program program = EasyMock.createMock(Program.class);
        RegisteredUser superAdmin = EasyMockUnitils.createMock(RegisteredUser.class);
        expect(superAdmin.isInRole(superAdmin, Authority.SUPERADMINISTRATOR)).andReturn(true);
        List<Project> allProjects = Collections.emptyList();
        expect(programDAOMock.getProjectsForProgram(program)).andReturn(allProjects);

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
        expect(programDAOMock.getProjectsForProgram(program)).andReturn(allProjects);

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
        expect(programDAOMock.getProjectsForProgramOfWhichAuthor(program, user)).andReturn(allProjects);

        replay();
        List<Project> loadedProjects = programsService.listProjects(user, program);
        verify();

        assertSame(allProjects, loadedProjects);
    }

    @Test
    public void shouldCreateNewCustomProgram() {
        Domicile domicile = new Domicile();
        ProgramsService thisBean = EasyMockUnitils.createMock(ProgramsService.class);

        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, domicile).otherInstitution("other_name").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().build();

        expect(applicationContext.getBean(ProgramsService.class)).andReturn(thisBean);
        expect(qualificationInstitutionService.getOrCreateCustomInstitution("AGH", domicile, "other_name")).andReturn(institution);
        Capture<Program> programCapture = new Capture<Program>();
        expect(thisBean.generateNextProgramCode(institution)).andReturn("AAA_00000");
        programDAOMock.save(capture(programCapture));
        
        replay();
        Program program = programsService.createOrGetProgram(opportunityRequest);
        verify();
        
        assertSame(programCapture.getValue(), program);
        assertEquals(opportunityRequest.getAtasRequired(), program.getAtasRequired());
        assertSame(institution, program.getInstitution());
        assertEquals(opportunityRequest.getProgramTitle(), program.getTitle());
        assertEquals("AAA_00000", program.getCode());
    }

    @Test
    public void shouldGetCustomProgram() {
        ProgramsService thisBean = EasyMockUnitils.createMock(ProgramsService.class);
        Program program = new ProgramBuilder().institution(new QualificationInstitutionBuilder().code("any_inst").build()).build();
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, null).institutionCode("any_inst").atasRequired(true)
                .sourceProgram(program).build();

        expect(applicationContext.getBean(ProgramsService.class)).andReturn(thisBean);
        expect(programDAOMock.merge(program)).andReturn(program);
        programDAOMock.save(program);

        replay();
        Program returned = programsService.createOrGetProgram(opportunityRequest);
        verify();

        assertTrue(returned.getAtasRequired());
        assertEquals(opportunityRequest.getProgramTitle(), program.getTitle());
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
    
    @Test(expected = CannotApplyException.class) 
    public void shouldThrowCannotApplyExceptionIfProgramAndProjectAreNull() {
        replay();
        programsService.getValidProgramProjectAdvert(null, null);
        verify();
    }
    
    @Test(expected = CannotApplyException.class) 
    public void shouldThrowCannotApplyExceptionIfProgramAndProjectAreNotActive() {
        String programCode = "test";
        Integer advertId = 0;
        EasyMock.expect(programDAOMock.getAcceptingApplicationsById(advertId)).andReturn(null);
        EasyMock.expect(programDAOMock.getProgamAcceptingApplicationsByCode(programCode)).andReturn(null);
        replay();
        programsService.getValidProgramProjectAdvert(programCode, advertId);
        verify();
    }
    
    @Test 
    public void shouldReturnAdvertByProgramCodeIfProgramIsActive() {
        Program program = new ProgramBuilder().code("test").build();
        EasyMock.expect(programDAOMock.getProgamAcceptingApplicationsByCode(program.getCode())).andReturn(program);
        replay();
        Advert advert = programsService.getValidProgramProjectAdvert(program.getCode(), null);
        verify();
        assertEquals(advert.getProgram(), program);
    }
    
    @Test 
    public void shouldReturnAdvertByProgramIdIfProgramIsActive() {
        Program program = new ProgramBuilder().id(1).build();
        EasyMock.expect(programDAOMock.getAcceptingApplicationsById(program.getId())).andReturn(program);
        replay();
        Advert advert = programsService.getValidProgramProjectAdvert(null, program.getId());
        verify();
        assertEquals(advert.getProgram(), program);
    }
    
    @Test 
    public void shouldReturnAdvertByProjectIdIfProjectIsActive() {
        Project project = new ProjectBuilder().id(1).build();
        EasyMock.expect(programDAOMock.getAcceptingApplicationsById(project.getId())).andReturn(project);
        replay();
        Advert advert = programsService.getValidProgramProjectAdvert(null, project.getId());
        verify();
        assertEquals(advert.getProject(), project);
    }
    
    @Test 
    public void shouldReturnAdvertByProgramIdInPreferenceOfProgramByProgramCode() {
        Program program = new ProgramBuilder().id(1).code("one").build();
        Program otherProgram = new ProgramBuilder().id(2).code("two").build();
        EasyMock.expect(programDAOMock.getAcceptingApplicationsById(program.getId())).andReturn(program);
        replay();
        Advert advert = programsService.getValidProgramProjectAdvert(otherProgram.getCode(), program.getId());
        verify();
        assertEquals(advert.getProgram(), program);
    }

}
