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
@RequestMapping(value = { "/application/feedback" })
public class ApproverController {

	private static final String APPLICATION_FEEDBACK_ERROR_VIEW_NAME = "applicationFeedbackError";
	private static final String APPROVER_FEEDBACK_SUBMITTED_VIEW_NAME = "approverFeedbackSubmitted";
	private final ApplicationFormDAO applicationFormDAO;

	public ApproverController() {
		this(null);
	}

	@Autowired
	public ApproverController(ApplicationFormDAO applicationFormDAO) {
		this.applicationFormDAO = applicationFormDAO;
	}

	@RequestMapping(value = { "/submit" }, method = RequestMethod.POST)
	@Transactional
	public String getSubmittedFeedbackPage(HttpServletRequest request,
			ModelMap modelMap) {
		SecurityContext context = SecurityContextHolder.getContext();
		String id = request.getParameter("id");
		ApplicationForm application = applicationFormDAO.get(Integer
				.parseInt(id));
		modelMap.addAttribute("application", application);
		RegisteredUser user = (RegisteredUser) context.getAuthentication()
				.getDetails();
		modelMap.addAttribute("user", user);
		if (application.getApproved() != null) {
			modelMap.addAttribute("message",
					"The application has been already approved by approver with username: "
							+ user.getUsername());
			return APPLICATION_FEEDBACK_ERROR_VIEW_NAME;
		}
		String asBoolean = feedbackToBoolean(request);
		if (asBoolean.equals("")) {
			modelMap.addAttribute("message",
					"You did not specify a feedback. Please approve or reject before saving. ");
			return APPLICATION_FEEDBACK_ERROR_VIEW_NAME;
		}
		application.setApprover(user);
		application.setApproved(asBoolean);
		applicationFormDAO.save(application);
		return APPROVER_FEEDBACK_SUBMITTED_VIEW_NAME;
	}

	private String feedbackToBoolean(HttpServletRequest request) {
		String asBoolean = "";
		if (request.getParameter("feedback") != null) {
			if (request.getParameter("feedback").equals("approve")) {
				asBoolean = "1";
			}
			if (request.getParameter("feedback").equals("reject")) {
				asBoolean = "0";
			}
		}
		return asBoolean;
	}
}
