package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Controller
@RequestMapping(value={"application/assignReviewer"})
public class AssignReviewerController {

	private static final String REVIEWER_VIEW_NAME = "assignReviewer";

    private final ApplicationFormDAO applicationDAO;
    private final UserDAO userDAO;
	
    AssignReviewerController(){
    	this(null, null);
    }
    
    @Autowired
	public AssignReviewerController(ApplicationFormDAO applicationDAO,
										UserDAO userDAO) {
		this.applicationDAO = applicationDAO;
		this.userDAO = userDAO;
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public String assignReviewerView(ModelMap modelMap) {	
		return REVIEWER_VIEW_NAME;
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	public String submitReviewer(String username, Integer appId,
									ModelMap model) {
		RegisteredUser reviewer = userDAO.getUserByUsername(username);
		ApplicationForm application = applicationDAO.get(appId);
		application.setReviewer(reviewer);
		
		applicationDAO.save(application);
		model.addAttribute("application", application);
		return "reviewerAssigned";
	}
}
