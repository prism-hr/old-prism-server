package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

public class ProjectDAOTest extends AutomaticRollbackTestCase{
	
	@Test 
	public void shouldGetAllProjects(){
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

		sessionFactory.getCurrentSession().createSQLQuery("delete from PROJECT").executeUpdate();
		
		save(program, projectOne, projectTwo);
		
		flushAndClearSession();
		
		List<Project> projects = projectDAO.getAllProjects();
		
		assertEquals(2, projects.size());
		assertTrue(projects.containsAll(Arrays.asList(projectOne, projectTwo)));
	}
	

}
