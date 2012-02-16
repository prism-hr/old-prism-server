package com.zuehlke.pgadmissions.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;


@Controller
@RequestMapping(value={"/application/feedback"})
public class ApproverController {
	
	private static final String APPLICATION_ALREADY_APPROVED_VIEW_NAME = "applicationAlreadyApproved";
	private static final String APPROVER_FEEDBACK_SUBMITTED_VIEW_NAME = "approverFeedbackSubmitted";
	private final ApplicationFormDAO applicationFormDAO;
	public ApproverController() {
		this(null);
	}
	
	@Autowired
	public ApproverController(ApplicationFormDAO applicationFormDAO){
		this.applicationFormDAO = applicationFormDAO;
	}
	
	@RequestMapping(value={"/submit"},method = RequestMethod.POST)
	@Transactional
	public String getSubmittedFeedbackPage(HttpServletRequest request, ModelMap modelMap){
		SecurityContext context = SecurityContextHolder.getContext();
		String id = request.getParameter("id");
		ApplicationForm application = applicationFormDAO.get(Integer.parseInt(id));
		application.setApprover((RegisteredUser)context.getAuthentication().getDetails());
		application.setApproved(isApproved(request));
		
		applicationFormDAO.save(application);
		modelMap.addAttribute("application", application);
		
//		if(applicationFormDAO.checkIfApplicationIsAlreadyApproved(Integer.parseInt(id)).size()>0){
//			return APPLICATION_ALREADY_APPROVED_VIEW_NAME;
//		}
		
		return APPROVER_FEEDBACK_SUBMITTED_VIEW_NAME;
	}
	
	private String isApproved(HttpServletRequest request){
		if(request.getParameter("approve")!= null){
			return "1";
		}
		if(request.getParameter("reject") != null){
			return "0";
		}
		return "0";
	}
}
