package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ProjectDAO;

@Controller
@RequestMapping(value = { "/projects" })
public class ProjectController {
	
	ProjectController() {
		this(null);
	}

	private final ProjectDAO projectDAO;

	@Autowired
	public ProjectController(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.GET)
	public String getProjectsPage(ModelMap modelMap) {
		modelMap.addAttribute("projects", projectDAO.getAllProjects());
		return "projects";
	}

}
