package com.zuehlke.pgadmissions.controllers.workflow.interview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;
@Controller
@RequestMapping("/interview")
public class AssignInterviewerController extends InterviewController {

	AssignInterviewerController() {
		this(null, null, null, null, null, null, null, null);
	}

	@Autowired
	public AssignInterviewerController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator,
			InterviewerService interviewerService, MessageSource messageSource, InterviewService interviewService, InterviewValidator interviewValidator,
			DatePropertyEditor datePropertyEditor) {
		super(applicationsService, userService, validator, interviewerService, messageSource, interviewService, interviewValidator, datePropertyEditor);
	}

	@RequestMapping(method = RequestMethod.GET, value = "assignInterviewers")
	public String getAssignInterviewersPage(ModelMap modelMap) {
		modelMap.put("assignOnly", true);
		return INTERVIEW_DETAILS_VIEW_NAME;
	}


	@Override
	@ModelAttribute("interview")
	public Interview getInterview(@RequestParam Integer applicationId) {
		return getApplicationForm(applicationId).getLatestInterview();
		
	}

}
