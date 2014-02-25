package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
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
    private RoleService roleService;

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
        EasyMock.expect(programDAOMock.getAllEnabledPrograms()).andReturn(programs);
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
        Domicile domicile = new Domicile();
        ProgramsService thisBean = EasyMockUnitils.createMock(ProgramsService.class);

        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, domicile).otherInstitution("other_name").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().build();

        expect(applicationContext.getBean(ProgramsService.class)).andReturn(thisBean);
        expect(qualificationInstitutionService.getOrCreateCustomInstitution("AGH", domicile, "other_name")).andReturn(institution);
        Capture<Program> programCapture = new Capture<Program>();
        programDAOMock.save(capture(programCapture));
        expect(thisBean.generateNextProgramCode(institution)).andReturn("AAA_00000");

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
    }

    @Test
    public void shouldGetBuiltinProgram() {
        ProgramsService thisBean = EasyMockUnitils.createMock(ProgramsService.class);
        Program program = new ProgramBuilder().programFeed(new ProgramFeed()).build();
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, null).sourceProgram(program).build();

        expect(applicationContext.getBean(ProgramsService.class)).andReturn(thisBean);

        replay();
        programsService.createOrGetProgram(opportunityRequest);
        verify();
    }

    @Test
    public void shouldSaveProgramOpportunity() {
        ProgramsService thisBean = EasyMockUnitils.createMock(ProgramsService.class);
        RegisteredUser author = new RegisteredUser();
        OpportunityRequest opportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(author, null).build();
        Program program = new Program();

        expect(applicationContext.getBean(ProgramsService.class)).andReturn(thisBean);
        expect(thisBean.createOrGetProgram(opportunityRequest)).andReturn(program);
        expect(programInstanceService.createRemoveProgramInstances(program, "B+++++,F+++++", 2014)).andReturn(null);
        thisBean.grantAdminPermissionsForProgram(author, program);

        replay();
        Program returned = programsService.saveProgramOpportunity(opportunityRequest);
        verify();

        Advert advert = program.getAdvert();

        assertSame(program, returned);
        assertTrue(advert.getActive());
        assertEquals(opportunityRequest.getProgramDescription(), advert.getDescription());
        assertEquals(opportunityRequest.getStudyDuration(), advert.getStudyDuration());

        assertSame(advert, program.getAdvert());
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

    @Test
    public void shouldGrantAdminPermissionsForProgram() {
        Role administratorRole = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
        RegisteredUser user = new RegisteredUser();
        QualificationInstitution institution = new QualificationInstitution();
        Program program = new ProgramBuilder().institution(institution).build();

        expect(roleService.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(administratorRole);

        replay();
        programsService.grantAdminPermissionsForProgram(user, program);
        verify();

        assertThat(user.getInstitutions(), contains(institution));
        assertThat(user.getProgramsOfWhichAdministrator(), contains(program));
        assertThat(user.getRoles(), contains(administratorRole));
    }

    @Test
    public void shouldNotDisableProgramIfNotCustom() {
        Program program = new ProgramBuilder().enabled(true).programFeed(new ProgramFeed()).build();
        expect(programDAOMock.getProgramByCode("prrr")).andReturn(program);

        replay();
        programsService.disableProgram("prrr");
        verify();

        assertTrue(program.isEnabled());
    }

    @Test
    public void shouldDisableProgram() {
        Project project1 = new ProjectBuilder().disabled(false).build();
        Project project2 = new ProjectBuilder().disabled(false).build();
        ProgramInstance programInstance1 = new ProgramInstanceBuilder().enabled(true).build();
        ProgramInstance programInstance2 = new ProgramInstanceBuilder().enabled(true).build();
        Program program = new ProgramBuilder().enabled(true).projects(project1, project2).instances(programInstance1, programInstance2).build();

        expect(programDAOMock.getProgramByCode("prrr")).andReturn(program);

        replay();
        programsService.disableProgram("prrr");
        verify();

        assertFalse(program.isEnabled());
        assertFalse(programInstance1.getEnabled());
        assertFalse(programInstance2.getEnabled());
        assertTrue(project1.isDisabled());
        assertTrue(project2.isDisabled());
    }

}
