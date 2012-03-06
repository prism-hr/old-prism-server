package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.pagemodels.MainPageModel;
import com.zuehlke.pgadmissions.domain.Project;

public class ProjectControllerTest {
	
	MockHttpServletRequest request;
	ProjectController projectController;
	ProjectDAO projectDAOMock;
	List<Project> projects;
	
	Map<String, MainPageModel> modelMap;
	ModelAndView modelAndView;
	
	@Test
	public void getProjectsViewName(){
		assertEquals("private/pgStudents/projects/project_list_page", projectController.getProjectsPage().getViewName());
	}
	
	@Test
	public void shouldShowAllProjects(){
		EasyMock.expect(projectDAOMock.getAllProjects()).andReturn(projects);
		EasyMock.replay(projectDAOMock);
		
		ModelAndView modelAndView = projectController.getProjectsPage();
		MainPageModel model = (MainPageModel) modelAndView.getModel().get("model");
		assertNotNull(model);
		assertSame(projects, model.getProjects());
	}
	
	@Before
	public void setUp(){
		projectDAOMock = EasyMock.createMock(ProjectDAO.class);
		projects = new ArrayList<Project>();
		projectController = new ProjectController(projectDAOMock);
	}
	
}
