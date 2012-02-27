package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

public class ProjectMappingTest extends AutomaticRollbackTestCase{
	
	@Test
	public void shouldSaveAndLoadProject(){
	
		Project project = new Project();
		project.setCode("01234");
		project.setDescription("I am a project :)");
		project.setTitle("Project's Title");
		Program program = new Program();
		program.setCode("1524");
		program.setDescription("description");
		program.setTitle("title");
		
		sessionFactory.getCurrentSession().save(program);
		project.setProgram(program);
		
		sessionFactory.getCurrentSession().save(project);
		assertNotNull(project.getId());
		Project reloadedProject = (Project)sessionFactory.getCurrentSession().get(Project.class, project.getId());
		assertSame(project, reloadedProject);
		
		flushAndClearSession();
		
		reloadedProject = (Project)sessionFactory.getCurrentSession().get(Project.class, project.getId());
		assertNotSame(project, reloadedProject);
		assertEquals(project, reloadedProject);
		assertEquals("Project's Title", reloadedProject.getTitle());
		assertEquals("I am a project :)", reloadedProject.getDescription());
		assertEquals("01234", reloadedProject.getCode());
		assertEquals(program, project.getProgram());
		
	}
}
