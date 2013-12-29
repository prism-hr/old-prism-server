package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.MainPageModel;

@Controller
@RequestMapping(value = { "/programs" })
public class ProgramController {
	
	private static final String PROJECTS_VIEW_NAME = "private/pgStudents/programs/program_list_page";

	private final ProgramDAO programDAO;

	@Value("${programController.enabled}") 
	private boolean enabled; 
	
	ProgramController() {
		this(null);
	}

    @Autowired
	public ProgramController(ProgramDAO programDAO) {
		this.programDAO = programDAO;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getProgramsPage() {
		if (enabled) {
		    MainPageModel mainPageModel = new MainPageModel();
		    mainPageModel.setPrograms(programDAO.getAllPrograms());
		    return new ModelAndView(PROJECTS_VIEW_NAME, "model", mainPageModel);
		} else {
		    throw new ResourceNotFoundException();
		}
	}
	
	public boolean isEnabled() {
	    return enabled;
	}
	
	public void setEnabled(boolean enabled) {
	    this.enabled = enabled;
	}
	
}