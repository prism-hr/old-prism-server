package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramsServiceTest {

    private ProgramDAO programDAOMock;

    private AdvertDAO advertDAOMock;

    private ProjectDAO projectDAOMock;

    private ProgramsService programsService;

    @Test
    public void shouldGetAllPrograms() {
        Program programOne = EasyMock.createMock(Program.class);
        Program programTwo = EasyMock.createMock(Program.class);
        EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
        EasyMock.replay(programOne, programTwo, programDAOMock);

        List<Program> allPrograms = programsService.getAllPrograms();
        assertEquals(2, allPrograms.size());
        assertTrue(allPrograms.contains(programOne));
        assertTrue(allPrograms.contains(programTwo));
    }

    @Test
    public void shouldGetProgramById() {
        Program program = EasyMock.createMock(Program.class);
        program.setId(2);
        EasyMock.expect(programDAOMock.getProgramById(2)).andReturn(program);
        EasyMock.replay(program, programDAOMock);
        assertEquals(program, programsService.getProgramById(2));
    }

    @Test
    public void shouldGetProgramByCode() {
        Program program = EasyMock.createMock(Program.class);
        EasyMock.expect(programDAOMock.getProgramByCode("code")).andReturn(program);
        EasyMock.replay(program, programDAOMock);
        assertEquals(program, programsService.getProgramByCode("code"));
    }

    @Test
    public void shouldDelegateSaveToDAO() {
        Program program = EasyMock.createMock(Program.class);
        programDAOMock.save(program);
        EasyMock.replay(programDAOMock);
        programsService.save(program);
        EasyMock.verify(programDAOMock);
    }

    @Test
    public void shouldGetProgramsForWhichCanManageProjectsIfAdmin() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);

        List<Program> programs = Collections.emptyList();
        EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(programs);
        EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true);

        EasyMock.replay(userMock, programDAOMock);
        List<Program> returnedPrograms = programsService.getProgramsForWhichCanManageProjects(userMock);
        EasyMock.verify(userMock, programDAOMock);

        assertSame(programs, returnedPrograms);
    }

    @Test
    public void shouldGetProgramsForWhichCanManageProjectsIfNotAdmin() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);

        EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
        EasyMock.expect(userMock.getProgramsOfWhichAdministrator()).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(userMock.getProgramsOfWhichApprover()).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(programDAOMock.getProgramsOfWhichPreviousReviewer(userMock)).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(programDAOMock.getProgramsOfWhichPreviousInterviewer(userMock)).andReturn(Collections.<Program> emptyList());
        EasyMock.expect(programDAOMock.getProgramsOfWhichPreviousSupervisor(userMock)).andReturn(Collections.<Program> emptyList());

        EasyMock.replay(userMock, programDAOMock);
        programsService.getProgramsForWhichCanManageProjects(userMock);
        EasyMock.verify(userMock, programDAOMock);
    }

    @Test
    public void shouldApplyScoringDefinition() {
        Program program = new ProgramBuilder().build();

        EasyMock.expect(programDAOMock.getProgramByCode("any_code")).andReturn(program);

        EasyMock.replay(programDAOMock);
        programsService.applyScoringDefinition("any_code", ScoringStage.REFERENCE, "Siala baba mak");
        EasyMock.verify(programDAOMock);

        ScoringDefinition definition = program.getScoringDefinitions().get(ScoringStage.REFERENCE);
        assertNotNull(definition);
        assertEquals("Siala baba mak", definition.getContent());
    }

    @Test
    public void shouldReturnClosingDatesMap() {
        Program program1 = new ProgramBuilder().code("p1").id(1).build();
        Program program2 = new ProgramBuilder().code("p2").id(2).build();
        expect(programDAOMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));

        Capture<Date> dateCaptor = new Capture<Date>();
        expect(programDAOMock.getNextClosingDateForProgram(eq(program1), EasyMock.capture(dateCaptor))).andReturn(new DateTime(2013, 2, 15, 00, 15).toDate());
        expect(programDAOMock.getNextClosingDateForProgram(eq(program2), EasyMock.capture(dateCaptor))).andReturn(new DateTime(2013, 2, 13, 13, 15).toDate());

        replay(programDAOMock);
        Map<String, String> result = programsService.getDefaultClosingDates();
        verify(programDAOMock);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("15 Feb 2013", result.get("p1"));
        Assert.assertEquals("13 Feb 2013", result.get("p2"));
    }
    
    @Test
    public void shouldGetProjectById() {
        Project project = EasyMock.createMock(Project.class);
        expect(projectDAOMock.getProjectById(1)).andReturn(project);
        replay(project, projectDAOMock);
        assertEquals(project, programsService.getProject(1));
        verify(projectDAOMock);
    }
    
    @Test
    public void shouldDelegateSaveProjectToDAO() {
        Project project = EasyMock.createMock(Project.class);
        projectDAOMock.save(project);
        EasyMock.replay(projectDAOMock);
        programsService.saveProject(project);
        EasyMock.verify(projectDAOMock);
    }
    
    @Test
    public void shouldDisableProjectAndAdvertOnRemoveProject(){
        Project project = new ProjectBuilder().advert(new AdvertBuilder().build()).build();
        expect(projectDAOMock.getProjectById(1)).andReturn(project);
        projectDAOMock.save(project);
        EasyMock.replay(projectDAOMock);
        
        programsService.removeProject(1);
        
        EasyMock.verify(projectDAOMock);
        assertTrue(project.isDisabled());
        assertFalse(project.getAdvert().getActive());
    }
    
   
    
    @Test
    public void shouldReturnAllProjectsForSuperAdministrator(){
    	Program program = EasyMock.createMock(Program.class);
    	RegisteredUser superAdmin = EasyMock.createMock(RegisteredUser.class);
    	expect(superAdmin.isInRole(superAdmin, Authority.SUPERADMINISTRATOR)).andReturn(true);
		List<Project> allProjects = Collections.emptyList();
		expect(projectDAOMock.getProjectsForProgram(program)).andReturn(allProjects);
		replay(projectDAOMock, superAdmin, program);
		List<Project> loadedProjects = programsService.listProjects(superAdmin, program);
		assertSame(allProjects, loadedProjects);
    }

    @Test
    public void shouldReturnAllProjectsForProgramAdministrator(){
    	Program program = EasyMock.createMock(Program.class);
    	RegisteredUser admin = EasyMock.createMock(RegisteredUser.class);
    	expect(admin.isInRole(admin, Authority.SUPERADMINISTRATOR)).andReturn(false);
    	expect(admin.isAdminInProgramme(program)).andReturn(true);
		List<Project> allProjects = Collections.emptyList();
		expect(projectDAOMock.getProjectsForProgram(program)).andReturn(allProjects);
		replay(projectDAOMock, admin, program);
		List<Project> loadedProjects = programsService.listProjects(admin, program);
		assertSame(allProjects, loadedProjects);
    }

    @Test
    public void shouldReturnProjectsForProgramOfAuthor(){
    	Program program = EasyMock.createMock(Program.class);
    	RegisteredUser user = EasyMock.createMock(RegisteredUser.class);
    	expect(user.isInRole(user, Authority.SUPERADMINISTRATOR)).andReturn(false);
    	expect(user.isAdminInProgramme(program)).andReturn(false);
		List<Project> allProjects = Collections.emptyList();
		expect(projectDAOMock.getProjectsForProgramOfWhichAuthor(program,user)).andReturn(allProjects);
		replay(projectDAOMock, user, program);
		List<Project> loadedProjects = programsService.listProjects(user, program);
		assertSame(allProjects, loadedProjects);
    }


    @Before
    public void setUp() {
        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        advertDAOMock = EasyMock.createMock(AdvertDAO.class);
        projectDAOMock = EasyMock.createMock(ProjectDAO.class);
        programsService = new ProgramsService(programDAOMock, advertDAOMock, projectDAOMock);
    }
}
