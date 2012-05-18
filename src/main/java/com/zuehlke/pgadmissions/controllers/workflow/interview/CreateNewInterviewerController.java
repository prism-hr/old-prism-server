package com.zuehlke.pgadmissions.controllers.workflow.interview;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.InterviewService;
import com.zuehlke.pgadmissions.services.InterviewerService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.InterviewValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/interview")
public class CreateNewInterviewerController extends InterviewController {
	
	
	 CreateNewInterviewerController() {
		 this(null, null, null, null,null,null,null, null);
	
	}

	 @Autowired
	public CreateNewInterviewerController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator,
			InterviewerService interviewerService, MessageSource messageSource, InterviewService interviewService, InterviewValidator interviewValidator,
			DatePropertyEditor datePropertyEditor) {
		super(applicationsService, userService, validator, interviewerService, messageSource, interviewService, interviewValidator, datePropertyEditor);
	}


	@RequestMapping(value = "/createInterviewer", method = RequestMethod.POST)
	public ModelAndView createInterviewerForNewInterview(@Valid @ModelAttribute("interviewer") RegisteredUser interviewer, BindingResult bindingResult,
			@ModelAttribute("applicationForm") ApplicationForm applicationForm,
			@ModelAttribute("pendingInterviewers") List<RegisteredUser> pendingInterviewers,
			@ModelAttribute("previousInterviewers") List<RegisteredUser> previousInterviewers) {
		return createNewInterviewer(interviewer, bindingResult, applicationForm, pendingInterviewers, previousInterviewers, "redirect:/interview/moveToInterview");
	}
	
	@RequestMapping(value = "/assignNewInterviewer", method = RequestMethod.POST)
	public ModelAndView createInterviewerForExistingInterview(@Valid @ModelAttribute("interviewer") RegisteredUser interviewer, BindingResult bindingResult,
			@ModelAttribute("applicationForm") ApplicationForm applicationForm,
			@ModelAttribute("pendingInterviewers") List<RegisteredUser> pendingInterviewers,
			@ModelAttribute("previousInterviewers") List<RegisteredUser> previousInterviewers) {
		
		return createNewInterviewer(interviewer, bindingResult, applicationForm, pendingInterviewers, previousInterviewers, "redirect:/interview/assignInterviewers");
	}
	
	private ModelAndView createNewInterviewer(RegisteredUser interviewer, BindingResult bindingResult, ApplicationForm applicationForm,
			List<RegisteredUser> pendingInterviewers, List<RegisteredUser> previousInterviewers, String viewName) {
		if(bindingResult.hasErrors()){
			return new ModelAndView(INTERVIEW_DETAILS_VIEW_NAME);
		}
		List<Integer> newUserIds = new ArrayList<Integer>();
		for (RegisteredUser registeredUser : pendingInterviewers) {
			newUserIds.add(registeredUser.getId());
		}

		RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(interviewer.getEmail());
		if (existingUser != null) {

			if (existingUser.isInterviewerOfApplicationForm(applicationForm)) {
				return getCreateInterviewerModelAndView(applicationForm, newUserIds,
						getCreateInterviewerMessage("assignInterviewer.user.alreadyExistsInTheApplication", existingUser), viewName);
			}

			if (pendingInterviewers.contains(existingUser)) {
				return getCreateInterviewerModelAndView(applicationForm, newUserIds,
						getCreateInterviewerMessage("assignInterviewer.user.pending", existingUser), viewName);
			}

			if (previousInterviewers.contains(existingUser)) {
				newUserIds.add(existingUser.getId());
				return getCreateInterviewerModelAndView(applicationForm, newUserIds,
						getCreateInterviewerMessage("assignInterviewer.user.previous", existingUser), viewName);
			}

			if (applicationForm.getProgram().getInterviewers().contains(existingUser)) {
				newUserIds.add(existingUser.getId());
				return getCreateInterviewerModelAndView(applicationForm, newUserIds,
						getCreateInterviewerMessage("assignInterviewer.user.alreadyInProgramme", existingUser), viewName);
			}

			newUserIds.add(existingUser.getId());
			return getCreateInterviewerModelAndView(applicationForm, newUserIds, getCreateInterviewerMessage("assignInterviewer.user.added", existingUser), viewName);

		}

		RegisteredUser newUser = userService.createNewUserInRole(interviewer.getFirstName(), interviewer.getLastName(), interviewer.getEmail(),
				Authority.INTERVIEWER);
		newUserIds.add(newUser.getId());
		return getCreateInterviewerModelAndView(applicationForm, newUserIds, getCreateInterviewerMessage("assignInterviewer.user.created", newUser), viewName);
	}

	
	private ModelAndView getCreateInterviewerModelAndView(ApplicationForm applicationForm, List<Integer> newUserIds, String message, String viewName) {

		ModelAndView modelAndView = new ModelAndView(viewName);
		modelAndView.getModel().put("applicationId", applicationForm.getId());
		modelAndView.getModel().put("pendingInterviewer", newUserIds);
		modelAndView.getModel().put("message", message);
		return modelAndView;
	}


	@Override
	public Interview getInterview(Integer applicationId) {
	
		return null;
	}
	

}
