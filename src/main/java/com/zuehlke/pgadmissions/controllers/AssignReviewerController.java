package com.zuehlke.pgadmissions.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ReviewerJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/assignReviewers")
public class AssignReviewerController {
	private static final String ASSIGN_REVIEWERS_TO_APPLICATION_VIEW = "private/staff/admin/assign_reviewers_to_appl_page";
	

	private final ApplicationsService applicationService;
	private final ReviewService reviewService;
	private final UserService userService;
	private final MessageSource messageSource;

	private final NewUserByAdminValidator userValidator;
	private final ReviewerJSONPropertyEditor userPropertyEditor;

	AssignReviewerController() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public AssignReviewerController(ApplicationsService applicationServiceMock, ReviewService reviewService,// 
			UserService userService, NewUserByAdminValidator validator, MessageSource msgSource,  ReviewerJSONPropertyEditor userPropertyEditor) {
		this.applicationService = applicationServiceMock;
		this.reviewService = reviewService;
		this.userService = userService;
		this.userValidator = validator;
		messageSource = msgSource;
		this.userPropertyEditor = userPropertyEditor;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getAssignReviewerPage() {
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}
//	
	@InitBinder(value="uiReviewer")
	public void registerValidators(WebDataBinder binder) {
		binder.setValidator(userValidator);
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Reviewer.class, userPropertyEditor);
	}
	

	@RequestMapping(value = "/moveApplicationToReview", method = RequestMethod.POST)
	public String moveApplicationToReviewState(@ModelAttribute("applicationForm") ApplicationForm application) {
		checkApplicationStatus(application);
		checkAdminPermission(application.getProgram());
//		try {
			applicationService.save(application);
			reviewService.moveApplicationToReview(application);
//		} catch (Exception e) {
//			throw new ResourceNotFoundException(e.getMessage());
//		}
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}

	@ModelAttribute("uiReviewer")
	public RegisteredUser getUiReviewer() {
		RegisteredUser uiReviewer = new RegisteredUser();
		return uiReviewer;
	}

	
	
	@RequestMapping(value = "/createReviewer", method = RequestMethod.POST)
	public String createReviewer(@ModelAttribute("programme") Program programme,@RequestParam(value = "assignedReviewers", required = false) List<RegisteredUser> assignedReviewers, @ModelAttribute("applicationForm") ApplicationForm form, @Valid @ModelAttribute("uiReviewer") RegisteredUser uiReviewer,// 
			 BindingResult bindingResult, ModelMap modelMap) {
		
		checkAdminPermission(programme);

		if(bindingResult.hasErrors()) {
				return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
		} else {
				RegisteredUser reviewer = userService.getUserByEmailIncludingDisabledAccounts(uiReviewer.getEmail());
				if (reviewer == null) {
					reviewer = reviewService.createNewReviewerForProgramme(programme,// 
							uiReviewer.getFirstName(), uiReviewer.getLastName(), uiReviewer.getEmail());
					modelMap.put("message", getMessage("assignReviewer.newReviewer.created", reviewer.getUsername(), reviewer.getEmail()));
				} else{
					if (reviewer.isReviewerOfApplicationForm(form)) {
						modelMap.put("message", getMessage("assignReviewer.reviewer.alreadyExistsInTheApplication", reviewer.getUsername(), reviewer.getEmail()));
					}
					else if (!programme.getProgramReviewers().contains(reviewer)) {
						reviewService.addUserToProgramme(programme, reviewer);
						modelMap.put("message", getMessage("assignReviewer.newReviewer.addedToProgramme", reviewer.getUsername(), reviewer.getEmail()));
					} else {
						modelMap.put("message", getMessage("assignReviewer.newReviewer.alreadyInProgramme", reviewer.getUsername(), reviewer.getEmail()));
					}
					
				}
		}
		if(assignedReviewers!=null){
			List<RegisteredUser> unsavedReviewers = new ArrayList<RegisteredUser>();
			System.out.println("assigned size " + assignedReviewers.size());
			for (RegisteredUser rev : assignedReviewers) {
				System.out.println("all name" + rev.getEmail());
				if(!form.getReviewers().contains(rev)){
					System.out.println("to unsaved: " + rev.getEmail());
					unsavedReviewers.add(rev);
				}
			}
			unsavedReviewers.addAll(assignedReviewers);
			modelMap.put("unsavedApplicationReviewers", unsavedReviewers);
		}
		return ASSIGN_REVIEWERS_TO_APPLICATION_VIEW;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {
		ApplicationForm application = applicationService.getApplicationById(applicationId);
		checkPermissionForApplication(application);
		checkApplicationStatus(application);
		return application;
	}

	@ModelAttribute("programme")
	public Program getProgrammeForApplication(@ModelAttribute("applicationForm") ApplicationForm application) {
		checkPermissionForApplication(application);
		return application.getProgram();
	}
	@ModelAttribute("availableReviewers")
	public List<RegisteredUser> getAvailableReviewers(//
			@ModelAttribute("programme") Program program,//
			@ModelAttribute("applicationForm") ApplicationForm application) {

		checkPermissionForApplication(application);
		List<RegisteredUser> availableReviewers = new ArrayList<RegisteredUser>();
		List<RegisteredUser> programmeReviewers = program.getProgramReviewers();
		for (RegisteredUser registeredUser : programmeReviewers) {
			if(!registeredUser.isReviewerOfApplicationForm(application)){
				availableReviewers.add(registeredUser);
			}
		}
				
		return availableReviewers;
	}


	@ModelAttribute("applicationReviewers")
	public List<RegisteredUser> getApplicationReviewers(@ModelAttribute("applicationForm") ApplicationForm application) {
		checkPermissionForApplication(application);
		List<RegisteredUser> existingReviewers = new ArrayList<RegisteredUser>();
		for (Reviewer reviewer : application.getReviewers()) {
			if(!existingReviewers.contains(reviewer.getUser())){
				existingReviewers.add(reviewer.getUser());
			}
		}
		return existingReviewers;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return getCurrentUser();
	}


	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	private void checkPermissionForApplication(ApplicationForm application) {
		if (application == null || !getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
	}

	private void checkAdminPermission(Program programme) {
		RegisteredUser currentUser = getCurrentUser();
		if (!(programme.getAdministrators().contains(currentUser) || //
				currentUser.isInRole(Authority.SUPERADMINISTRATOR) || //
		programme.getProgramReviewers().contains(currentUser))) {
			throw new ResourceNotFoundException();
		}
	}

	private void checkApplicationStatus(ApplicationForm application) {
		switch (application.getStatus()) {
		case REVIEW:
		case VALIDATION:
			break;
		default:
			throw new CannotUpdateApplicationException();
		}
	}

	

	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}
}
