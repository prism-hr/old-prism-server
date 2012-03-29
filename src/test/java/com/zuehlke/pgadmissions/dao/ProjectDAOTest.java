package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

public class ProjectDAOTest extends AutomaticRollbackTestCase {

	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		ProjectDAO projectDAO = new ProjectDAO();
		projectDAO.getAllProjects();
	}

	
	@Test
	public void shouldGetAllProjects() {
		ProjectDAO projectDAO = new ProjectDAO(sessionFactory);

		Project projectOne = new Project();
		projectOne.setCode("01234");
		projectOne.setDescription("I am a project :)");
		projectOne.setTitle("Project's Title");
		Program program = new Program();
		program.setCode("1524");
		program.setDescription("description");
		program.setTitle("title");
		projectOne.setProgram(program);

		Project projectTwo = new Project();
		projectTwo.setCode("012345");
		projectTwo.setDescription("I am a project two:)");
		projectTwo.setTitle("Project's Two Title");
		projectTwo.setProgram(program);

		BigInteger numberOfExistingProject = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from PROJECT").uniqueResult();

		save(program, projectOne, projectTwo);

		flushAndClearSession();

		List<Project> projects = projectDAO.getAllProjects();

		assertEquals(numberOfExistingProject.intValue() + 2, projects.size());
		assertTrue(projects.containsAll(Arrays.asList(projectOne, projectTwo)));
	}

	@Test
	public void shouldGetProjectById() {
		ProjectDAO projectDAO = new ProjectDAO(sessionFactory);

		Project projectOne = new Project();
		projectOne.setCode("01234");
		projectOne.setDescription("I am a project :)");
		projectOne.setTitle("Project's Title");
		Program program = new Program();
		program.setCode("1524");
		program.setDescription("description");
		program.setTitle("title");
		projectOne.setProgram(program);

		Project projectTwo = new Project();
		projectTwo.setCode("012345");
		projectTwo.setDescription("I am a project two:)");
		projectTwo.setTitle("Project's Two Title");
		projectTwo.setProgram(program);

		save(program, projectOne, projectTwo);

		flushAndClearSession();

		Project loadedProject = projectDAO.getProjectById(projectOne.getId());

		assertEquals(projectOne, loadedProject);
	}

	
	
}
