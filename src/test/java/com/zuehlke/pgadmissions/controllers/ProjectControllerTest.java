package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Project;

public class ProjectControllerTest {
	

	MockHttpServletRequest request;
	ProjectController projectController;
	ProjectDAO projectDAOMock;
	List<Project> projects;
	
	@Test
	public void getProjectsViewName(){
		assertEquals("projects", projectController.getProjectsPage(new ModelMap()));
	}
	
	@Test
	public void shouldShowAllProjects(){
		EasyMock.expect(projectDAOMock.getAllProjects()).andReturn(projects);
		EasyMock.replay(projectDAOMock);
		ModelMap modelMap = new ModelMap();
		projectController.getProjectsPage(modelMap);
		assertSame(projects, modelMap.get("projects"));
	}
	
	@Before
	public void setUp(){
		projectDAOMock = EasyMock.createMock(ProjectDAO.class);
		projects = new ArrayList<Project>();
		
		projectController = new ProjectController(projectDAOMock);
	}
	
}
