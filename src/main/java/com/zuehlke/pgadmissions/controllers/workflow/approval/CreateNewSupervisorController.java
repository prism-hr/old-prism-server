package com.zuehlke.pgadmissions.controllers.workflow.approval;

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
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@Controller
@RequestMapping("/approval")
public class CreateNewSupervisorController extends ApprovalController {
	
	
	private static final String REDIRECT_APPROVAL_ASSIGN_SUPERVISORS = "redirect:/approval/assignSupervisors";
	private static final String REDIRECT_APPROVAL_MOVE_TO_APPROVAL = "redirect:/approval/moveToApproval";


	CreateNewSupervisorController() {
		 this(null, null, null, null, null, null, null);
	
	}

	@Autowired
	public CreateNewSupervisorController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator reviewerValidator, ApprovalRoundValidator approvalRoundValidator,
			ApprovalService approvalService, MessageSource messageSource, SupervisorPropertyEditor supervisorPropertyEditor) {
		super(applicationsService, userService, reviewerValidator, approvalRoundValidator, approvalService, messageSource, supervisorPropertyEditor);

	}


	 @RequestMapping(value = "/createSupervisor", method = RequestMethod.POST)
		public ModelAndView createSupervisorForNewApprovalRound(@Valid @ModelAttribute("supervisor") RegisteredUser supervisor, BindingResult bindingResult,
				@ModelAttribute("applicationForm") ApplicationForm applicationForm, @ModelAttribute("pendingSupervisors") List<RegisteredUser> pendingSupervisors,
				@ModelAttribute("previousSupervisors") List<RegisteredUser> previousSupervisors) {
			return createNewSupervisor(supervisor, bindingResult, applicationForm, pendingSupervisors, previousSupervisors, REDIRECT_APPROVAL_MOVE_TO_APPROVAL);
		}

		@RequestMapping(value = "/assignNewSupervisor", method = RequestMethod.POST)
		public ModelAndView createReviewerForExistingReviewRound(@Valid @ModelAttribute("supervisor") RegisteredUser supervisor, BindingResult bindingResult,
				@ModelAttribute("applicationForm") ApplicationForm applicationForm, @ModelAttribute("pendingSupervisors") List<RegisteredUser> pendingSupervisors,
				@ModelAttribute("previousSupervisors") List<RegisteredUser> previousSupervisors) {
			return createNewSupervisor(supervisor, bindingResult, applicationForm, pendingSupervisors, previousSupervisors, REDIRECT_APPROVAL_ASSIGN_SUPERVISORS);
		}
		

		private ModelAndView createNewSupervisor(RegisteredUser supervisor, BindingResult bindingResult, ApplicationForm applicationForm,
				List<RegisteredUser> pendingSupervisors, List<RegisteredUser> previousSupervisors, String viewName) {
			if(bindingResult.hasErrors()){
				ModelAndView modelAndView = new ModelAndView(APROVAL_DETAILS_VIEW_NAME);
				if(REDIRECT_APPROVAL_MOVE_TO_APPROVAL.equals(viewName) ){
					modelAndView.getModel().put("assignOnly", false);
				}else{
					modelAndView.getModel().put("assignOnly", true);
				}
				
				return modelAndView;
			}
			List<Integer> newUserIds = new ArrayList<Integer>();
			for (RegisteredUser registeredUser : pendingSupervisors) {
				newUserIds.add(registeredUser.getId());
			}

			RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(supervisor.getEmail());
			if (existingUser != null) {

				if (existingUser.isSupervisorOfApplicationForm(applicationForm)) {
					return getCreateSupervisorModelAndView(applicationForm, newUserIds,
							getCreateSupervisorMessage("assignSupervisor.user.alreadyExistsInTheApplication", existingUser), viewName);
				}

				if (pendingSupervisors.contains(existingUser)) {
					return getCreateSupervisorModelAndView(applicationForm, newUserIds,
							getCreateSupervisorMessage("assignSupervisor.user.pending", existingUser), viewName);
				}

				if (previousSupervisors.contains(existingUser)) {
					newUserIds.add(existingUser.getId());
					return getCreateSupervisorModelAndView(applicationForm, newUserIds,
							getCreateSupervisorMessage("assignSupervisor.user.previous", existingUser), viewName);
				}

				if (applicationForm.getProgram().getSupervisors().contains(existingUser)) {
					newUserIds.add(existingUser.getId());
					return getCreateSupervisorModelAndView(applicationForm, newUserIds,
							getCreateSupervisorMessage("assignSupervisor.user.alreadyInProgramme", existingUser), viewName);
				}

				newUserIds.add(existingUser.getId());
				return getCreateSupervisorModelAndView(applicationForm, newUserIds, getCreateSupervisorMessage("assignSupervisor.user.added", existingUser), viewName);

			}

			RegisteredUser newUser = userService.createNewUserInRole(supervisor.getFirstName(), supervisor.getLastName(), supervisor.getEmail(), Authority.SUPERVISOR, null, null);
			newUserIds.add(newUser.getId());
			return getCreateSupervisorModelAndView(applicationForm, newUserIds, getCreateSupervisorMessage("assignSupervisor.user.created", newUser), viewName);
		}

	
		private ModelAndView getCreateSupervisorModelAndView(ApplicationForm applicationForm, List<Integer> newUserIds, String message, String viewName) {

			ModelAndView modelAndView = new ModelAndView(viewName);
			modelAndView.getModel().put("applicationId", applicationForm.getApplicationNumber());
			modelAndView.getModel().put("pendingSupervisors", newUserIds);
			modelAndView.getModel().put("message", message);
			return modelAndView;
		}


	@Override
	@ModelAttribute("approvalRound")
	public ApprovalRound getApprovalRound(String applicationId) {	
		return new ApprovalRound();
	}
	

}
