package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.pagemodels.MainPageModel;

@Controller
@RequestMapping(value = { "/projects" })
public class ProjectController {
	
	private static final String PROJECTS_VIEW_NAME = "private/pgStudents/projects/project_list_page";

	ProjectController() {
		this(null);
	}

	private final ProjectDAO projectDAO;

	@Autowired
	public ProjectController(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}
	

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getProjectsPage() {
		
		MainPageModel mainPageModel = new MainPageModel();
		mainPageModel.setProjects(projectDAO.getAllProjects());
		return new ModelAndView(PROJECTS_VIEW_NAME, "model", mainPageModel);
	}

}
