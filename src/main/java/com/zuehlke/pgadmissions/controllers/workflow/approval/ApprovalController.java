package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;

@Controller
@RequestMapping("/approval")
public class ApprovalController {

	
	private static final String SUPERVISORS_SECTION = "/private/staff/supervisors/supervisors_section";
	private static final String APPROVAL_PAGE = "/private/staff/supervisors/approval_details";
	private final ApplicationsService applicationsService;
	private final UserService userService;
	private final ApprovalRoundValidator approvalRoundValidator;
	private final SupervisorPropertyEditor supervisorPropertyEditor;
	private final ApprovalService approvalService;

	ApprovalController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ApprovalController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService,
			ApprovalRoundValidator approvalRoundValidator, SupervisorPropertyEditor supervisorPropertyEditor) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.approvalService = approvalService;
		this.approvalRoundValidator = approvalRoundValidator;
		this.supervisorPropertyEditor = supervisorPropertyEditor;
	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToApproval")
	public String getMoveToApprovalPage() {
		return APPROVAL_PAGE;
	}

	@RequestMapping(method = RequestMethod.GET, value = "supervisors_section")
	public String getSupervisorSection() {
		return SUPERVISORS_SECTION;

	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {

		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (application == null//
				|| (!userService.getCurrentUser().hasAdminRightsOnApplication(application) && !userService.getCurrentUser()//
						.isInRoleInProgram(Authority.APPROVER, application.getProgram()))) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@ModelAttribute("programmeSupervisors")
	public List<RegisteredUser> getProgrammeSupervisors(@RequestParam String applicationId) {
		return getApplicationForm(applicationId).getProgram().getSupervisors();
	}

	@ModelAttribute("previousSupervisors")
	public List<RegisteredUser> getPreviousSupervisorsAndInterviewersWillingToSupervise(@RequestParam String applicationId) {
		List<RegisteredUser> availablePreviousSupervisors = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> previousSupervisorsOfProgram = userService.getAllPreviousSupervisorsOfProgram(applicationForm.getProgram());
		
		for (RegisteredUser registeredUser : previousSupervisorsOfProgram) {
			if (!applicationForm.getProgram().getSupervisors().contains(registeredUser)) {
				availablePreviousSupervisors.add(registeredUser);
			}
		}
		List<RegisteredUser> interviewersWillingToSupervise = applicationForm.getUsersWillingToSupervise();
		for (RegisteredUser registeredUser : interviewersWillingToSupervise) {
			if (!applicationForm.getProgram().getSupervisors().contains(registeredUser) && !availablePreviousSupervisors.contains(registeredUser)) {
				availablePreviousSupervisors.add(registeredUser);
			}
		}
		return availablePreviousSupervisors;

	}

	@ModelAttribute("approvalRound")
	public ApprovalRound getApprovalRound(@RequestParam String applicationId) {
		ApprovalRound approvalRound = new ApprovalRound();
		ApplicationForm applicationForm = getApplicationForm((String) applicationId);
		ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
		if (latestApprovalRound != null) {
			approvalRound.setSupervisors(latestApprovalRound.getSupervisors());
		}
		List<RegisteredUser> interviewersWillingToSupervise = applicationForm.getUsersWillingToSupervise();
		for (RegisteredUser registeredUser : interviewersWillingToSupervise) {
			if(!registeredUser.isSupervisorInApprovalRound(approvalRound)){
				Supervisor supervisor = new Supervisor();
				supervisor.setUser(registeredUser);
				approvalRound.getSupervisors().add(supervisor);
			}
		}
		return approvalRound;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@InitBinder("approvalRound")
	public void registerValidatorAndPropertyEditor(WebDataBinder binder) {
		binder.setValidator(approvalRoundValidator);
		binder.registerCustomEditor(Supervisor.class, supervisorPropertyEditor);
		
	}

	@RequestMapping(value = "/move", method = RequestMethod.POST)
	public String moveToApproval(@RequestParam String applicationId, @Valid @ModelAttribute("approvalRound") ApprovalRound approvalRound, BindingResult bindingResult) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		if (bindingResult.hasErrors()) {
			return SUPERVISORS_SECTION;
		}
		approvalService.moveApplicationToApproval(applicationForm, approvalRound);
		return "/private/common/ajax_OK";
	}
	
}
