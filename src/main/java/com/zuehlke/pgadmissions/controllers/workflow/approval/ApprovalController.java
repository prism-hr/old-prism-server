package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public abstract class ApprovalController {
	protected final String APROVAL_DETAILS_VIEW_NAME = "/private/staff/supervisors/approval_details";
	protected final ApplicationsService applicationsService;
	protected final UserService userService;
	protected final NewUserByAdminValidator supervisorValidator;
	protected final MessageSource messageSource;
	protected final ApprovalRoundValidator approvalroundValidator;
	protected final ApprovalService approvalService;
	protected final SupervisorPropertyEditor supervisorPropertyEditor;
	protected final EncryptionHelper encryptionHelper;

	ApprovalController() {
		this(null, null, null, null, null, null, null, null);
	}

	@Autowired
	public ApprovalController(ApplicationsService applicationsService, UserService userService, NewUserByAdminValidator validator,
			ApprovalRoundValidator approvalroundValidator, ApprovalService approvalService, MessageSource messageSource,
			SupervisorPropertyEditor supervisorPropertyEditor, EncryptionHelper encryptionHelper) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.supervisorValidator = validator;
		this.approvalroundValidator = approvalroundValidator;
		this.approvalService = approvalService;

		this.messageSource = messageSource;
		this.supervisorPropertyEditor = supervisorPropertyEditor;
		this.encryptionHelper = encryptionHelper;
	}

	@InitBinder(value = "supervisor")
	public void registerSupervisorValidators(WebDataBinder binder) {
		binder.setValidator(supervisorValidator);
	}

	@InitBinder(value = "approvalRound")
	public void registerApprovalRoundValidator(WebDataBinder binder) {
		binder.setValidator(approvalroundValidator);
		binder.registerCustomEditor(Supervisor.class, supervisorPropertyEditor);
	}

	@ModelAttribute("supervisor")
	public RegisteredUser getSupervisor() {
		return new RegisteredUser();
	}

	@ModelAttribute("programmeSupervisors")
	public List<RegisteredUser> getProgrammeSupervisors(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingSupervisors) {
		ApplicationForm application = getApplicationForm(applicationId);
		Program program = application.getProgram();
		List<RegisteredUser> availableSupervisors = new ArrayList<RegisteredUser>();
		List<RegisteredUser> programmeSupervisors = program.getSupervisors();
		for (RegisteredUser registeredUser : programmeSupervisors) {
			if (!registeredUser.isSupervisorOfApplicationForm(application)) {
				availableSupervisors.add(registeredUser);
			}
		}
		for (RegisteredUser registeredUser : getPendingSupervisors(pendingSupervisors, applicationId)) {
			if (availableSupervisors.contains(registeredUser)) {
				availableSupervisors.remove(registeredUser);
			}
		}

		return availableSupervisors;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("applicationSupervisors")
	public Set<RegisteredUser> getApplicationSupervisorsAsUsers(@RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		Set<RegisteredUser> existingSupervisors = new HashSet<RegisteredUser>();
		ApprovalRound latestRound = applicationForm.getLatestApprovalRound();
		if (latestRound != null) {
			for (Supervisor supervisor : latestRound.getSupervisors()) {
				existingSupervisors.add(supervisor.getUser());
			}
		}
		return existingSupervisors;
	}

	protected String getCreateSupervisorMessage(String code, RegisteredUser user) {
		return getMessage(code, new Object[] { user.getFirstName() + " " + user.getLastName(), user.getEmail() });
	}

	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		RegisteredUser currentUser = userService.getCurrentUser();
		if (application == null
				|| (!currentUser.hasAdminRightsOnApplication(application) && !currentUser.isSupervisorOfApplicationForm(application) && !currentUser
						.isInRoleInProgram(Authority.APPROVER, application.getProgram()))) {
			throw new ResourceNotFoundException();
		}
		return application;
	}

	public abstract ApprovalRound getApprovalRound(@RequestParam String applicationId);

	@ModelAttribute("pendingSupervisors")
	public List<RegisteredUser> getPendingSupervisors(@RequestParam(required = false) List<String> pendingSupervisors, @RequestParam String applicationId) {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> newUsers = new ArrayList<RegisteredUser>();
		if (pendingSupervisors != null) {
			for (String encryptedSupervisorId : pendingSupervisors) {
				RegisteredUser user = userService.getUser(encryptionHelper.decryptToInteger(encryptedSupervisorId));
				if (!user.isSupervisorOfApplicationForm(applicationForm)) {
					newUsers.add(user);
				}
			}
		
		}
		return newUsers;
	}

	@ModelAttribute("willingToSuperviseUsers")
	public List<RegisteredUser> getWillingToSuperviseusers(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingSupervisors)  {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> availableInterviewsWillingToSupervise = new ArrayList<RegisteredUser>();
		List<RegisteredUser> interviewsWillingToSupervise = applicationForm.getUsersWillingToSupervise();
		List<RegisteredUser> pendingSupervisor = getPendingSupervisors(pendingSupervisors, applicationId);
		for (RegisteredUser registeredUser : interviewsWillingToSupervise) {
			if(!pendingSupervisor.contains(registeredUser) && !registeredUser.isSupervisorOfApplicationForm(applicationForm)){
				availableInterviewsWillingToSupervise.add(registeredUser);
			}
		}
		return availableInterviewsWillingToSupervise;
	}

	@ModelAttribute("previousSupervisors")
	public List<RegisteredUser> getPreviousSupervisors(@RequestParam String applicationId, @RequestParam(required = false) List<String> pendingSupervisors) {
		List<RegisteredUser> availablePreviousSupervisors = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		List<RegisteredUser> previousSupervisorsOfProgram = userService.getAllPreviousSupervisorsOfProgram(applicationForm.getProgram());

		List<RegisteredUser> pendingSupervisorsList = getPendingSupervisors(pendingSupervisors, applicationId);

		for (RegisteredUser registeredUser : previousSupervisorsOfProgram) {
			if (!registeredUser.isSupervisorOfApplicationForm(applicationForm) && !pendingSupervisorsList.contains(registeredUser)
					&& !applicationForm.getProgram().getSupervisors().contains(registeredUser)) {
				availablePreviousSupervisors.add(registeredUser);
			}
		}

		return availablePreviousSupervisors;
	}

}
