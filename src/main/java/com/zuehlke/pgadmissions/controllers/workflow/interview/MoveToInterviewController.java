package com.zuehlke.pgadmissions.controllers.workflow.interview;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.InterviewerPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/interview")
public class MoveToInterviewController extends InterviewController {

	MoveToInterviewController() {
		this(null, null, null, null, null, null, null,  null);
	}

	@Autowired
	public MoveToInterviewController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator,
			 MessageSource messageSource, InterviewService interviewService, InterviewValidator interviewValidator,
			DatePropertyEditor datePropertyEditor, InterviewerPropertyEditor interviewerPropertyEditor) {
		super(applicationsService, userService, validator, messageSource, interviewService, interviewValidator, datePropertyEditor, interviewerPropertyEditor);

	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToInterview")
	public String getInterviewDetailsPage(ModelMap modelMap) {
		modelMap.put("assignOnly", false);
		return INTERVIEW_DETAILS_VIEW_NAME;
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST)
	public String moveToInterview(@RequestParam String applicationId, @Valid @ModelAttribute("interview") Interview interview, BindingResult bindingResult) {

		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (bindingResult.hasErrors()) {
			return INTERVIEW_DETAILS_VIEW_NAME;
		}
		interviewService.moveApplicationToInterview(interview, applicationForm);
		

		return "redirect:/applications";
	}
	
	
	@ModelAttribute("interview")
	public Interview getInterview(@RequestParam Object applicationId) {
		return new Interview();
	}


}
