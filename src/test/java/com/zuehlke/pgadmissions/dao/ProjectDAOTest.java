package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ProjectDAOTest extends AutomaticRollbackTestCase {

    private ProjectDAO projectDAO;
    private Project project;

    @Before
    public void setUp() {
        projectDAO = new ProjectDAO(sessionFactory);
        createDomainObjects();
    }

    private void createDomainObjects() {
        RegisteredUser author = new RegisteredUserBuilder().username("author").firstName("author").lastName("project").email("author.project@email.test")
                .build();
        Date closingDate = DateUtils.addDays(new Date(), 10);
        RegisteredUser primarySupervisor = new RegisteredUserBuilder().username("primary").firstName("primary").lastName("supervisor")
                .email("primary.supervisor@email.test").build();
        RegisteredUser secondarySupervisor = new RegisteredUserBuilder().username("secondary").firstName("secondary").lastName("supervisor")
                .email("secondary.supervisor@email.test").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("ProjectProgram").institution(institution).build();
        save(author, primarySupervisor, secondarySupervisor, institution, program);
        Advert advert = new AdvertBuilder().description("desc").funding("fund").studyDuration(1).title("title").build();
        project = new ProjectBuilder().advert(advert).author(author).closingDate(closingDate).primarySupervisor(primarySupervisor).program(program)
                .secondarySupervisor(secondarySupervisor).build();
        flushAndClearSession();
    }

    @Test
    public void shouldSaveProjectAndAdvert() {
        projectDAO.save(project);
        flushAndClearSession();
        assertNotNull(project.getId());
        assertNotNull(project.getAdvert().getId());
    }

    @Test
    public void shouldLoadProjectAndAdvert() {
        projectDAO.save(project);
        flushAndClearSession();

        Project loadedProject = projectDAO.getProjectById(project.getId());
        assertNotNull(loadedProject);
        assertNotNull(loadedProject.getAdvert());
        assertEquals(loadedProject.getId(), project.getId());
        assertEquals(loadedProject.getAdvert().getId(), project.getAdvert().getId());
        assertEquals(loadedProject.getAuthor().getId(), project.getAuthor().getId());
        assertEquals(loadedProject.getPrimarySupervisor().getId(), project.getPrimarySupervisor().getId());
        assertEquals(loadedProject.getSecondarySupervisor().getId(), project.getSecondarySupervisor().getId());
        assertEquals(loadedProject.getClosingDate(), DateUtils.truncate(loadedProject.getClosingDate(), Calendar.DAY_OF_MONTH));
        assertEquals(loadedProject.getProgram().getId(), project.getProgram().getId());
        assertEquals(loadedProject.isAcceptingApplications(), project.isAcceptingApplications());
        assertEquals(loadedProject.isDisabled(), project.isDisabled());
    }

    @Test
    public void shouldGetEnabledProjectsForProgram() {
        projectDAO.save(project);
        flushAndClearSession();

        List<Project> projects = projectDAO.getProjectsForProgram(project.getProgram());
        assertTrue(projects.size() >= 1);
        assertProjectIncluded(true, projects, project);
    }

    @Test
    public void shouldNotGetDisabledProjectsForProgram() {
        project.setDisabled(true);
        project.getAdvert().setActive(false);
        projectDAO.save(project);
        flushAndClearSession();

        List<Project> projects = projectDAO.getProjectsForProgram(project.getProgram());
        assertProjectIncluded(false, projects, project);
    }

    @Test
    public void shouldGetProjectsForProgramAndAuthor() {
        projectDAO.save(project);
        flushAndClearSession();

        List<Project> projects = projectDAO.getProjectsForProgramOfWhichAuthor(project.getProgram(), project.getAuthor());
        assertProjectIncluded(true, projects, project);
    }

    private void assertProjectIncluded(final boolean expected, List<Project> projects, Project expectedProject) {
        assertTrue(projects.size() >= (expected ? 1 : 0));
        boolean projectIncluded = false;
        for (Project loadedProject : projects) {
            assertFalse(loadedProject.isDisabled());
            if (expectedProject.getId().equals(loadedProject.getId())) {
                projectIncluded = true;
            }
        }
        assertEquals(expected, projectIncluded);
    }

}
