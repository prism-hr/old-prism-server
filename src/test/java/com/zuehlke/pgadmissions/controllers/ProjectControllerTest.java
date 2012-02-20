package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.MainPageModel;
import com.zuehlke.pgadmissions.domain.Project;

public class ProjectControllerTest {
	
	MockHttpServletRequest request;
	ProjectController projectController;
	ProjectDAO projectDAOMock;
	MainPageModel model;
	List<Project> projects;
	
	Map<String, MainPageModel> modelMap;
	ModelAndView modelAndView;
	
	@Test
	public void getProjectsViewName(){
		assertEquals("projects", projectController.getProjectsPage().getViewName());
	}
	
	@Test
	public void shouldShowAllProjects(){
		EasyMock.expect(projectDAOMock.getAllProjects()).andReturn(projects);
		EasyMock.replay(projectDAOMock);
		
		projectController.getProjectsPage();
		assertSame(model, modelAndView.getModel().get("model"));
	}
	
	@Before
	public void setUp(){
		projectDAOMock = EasyMock.createMock(ProjectDAO.class);
		projects = new ArrayList<Project>();
		
		model = new MainPageModel();
		
		modelMap = new HashMap<String, MainPageModel>();
		modelMap.put("model", model);
		
		modelAndView = new ModelAndView();
		modelAndView.addAllObjects(modelMap);
		
		projectController = new ProjectController(projectDAOMock);
	}
	
}
