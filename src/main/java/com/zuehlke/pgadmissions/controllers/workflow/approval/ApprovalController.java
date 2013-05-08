package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;
import com.zuehlke.pgadmissions.validators.SendToPorticoDataDTOValidator;

@SessionAttributes("approvalRound")
@Controller
@RequestMapping("/approval")
public class ApprovalController {

	private static final String REQUEST_RESTART_APPROVE_PAGE = "/private/staff/approver/request_restart_approve_page";
	private static final String SUPERVISORS_SECTION = "/private/staff/supervisors/supervisors_section";
	private static final String PORTICO_VALIDATION_SECTION = "/private/staff/supervisors/portico_validation_section";
	private static final String APPROVAL_PAGE = "/private/staff/supervisors/approval_details";
	private static final String QUALIFICATION_SECTION = "/private/staff/supervisors/components/qualification_portico_validation";
	private static final String REFERENCE_SECTION = "/private/staff/supervisors/components/reference_portico_validation";

	private final ApplicationsService applicationsService;

	private final UserService userService;

	private final ApprovalRoundValidator approvalRoundValidator;

	private final SupervisorPropertyEditor supervisorPropertyEditor;

	private final ApprovalService approvalService;

	private final DocumentPropertyEditor documentPropertyEditor;

	private final GenericCommentValidator commentValidator;

	private final RefereesAdminEditDTOValidator refereesAdminEditDTOValidator;

	private final QualificationService qualificationService;

	private final RefereeService refereeService;

	private final EncryptionHelper encryptionHelper;

	private final SendToPorticoDataDTOEditor sendToPorticoDataDTOEditor;

	private final SendToPorticoDataDTOValidator sendToPorticoDataDTOValidator;

	private final DatePropertyEditor datePropertyEditor;

	private final CountryService countryService;

	private final CountryPropertyEditor countryPropertyEditor;

	private final ScoringDefinitionParser scoringDefinitionParser;

	private final ScoresPropertyEditor scoresPropertyEditor;

	private final ScoreFactory scoreFactory;

	public ApprovalController() {
		this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public ApprovalController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService,
	                ApprovalRoundValidator approvalRoundValidator, SupervisorPropertyEditor supervisorPropertyEditor,
	                DocumentPropertyEditor documentPropertyEditor, GenericCommentValidator commentValidator,
	                RefereesAdminEditDTOValidator refereesAdminEditDTOValidator, QualificationService qualificationService, RefereeService refereeService,
	                EncryptionHelper encryptionHelper, SendToPorticoDataDTOEditor sendToPorticoDataDTOEditor,
	                SendToPorticoDataDTOValidator sendToPorticoDataDTOValidator, DatePropertyEditor datePropertyEditor, CountryService countryService,
	                CountryPropertyEditor countryPropertyEditor, ScoringDefinitionParser scoringDefinitionParser, ScoresPropertyEditor scoresPropertyEditor,
	                ScoreFactory scoreFactory) {
		this.applicationsService = applicationsService;
		this.userService = userService;
		this.approvalService = approvalService;
		this.approvalRoundValidator = approvalRoundValidator;
		this.supervisorPropertyEditor = supervisorPropertyEditor;
		this.documentPropertyEditor = documentPropertyEditor;
		this.commentValidator = commentValidator;
		this.refereesAdminEditDTOValidator = refereesAdminEditDTOValidator;
		this.qualificationService = qualificationService;
		this.refereeService = refereeService;
		this.encryptionHelper = encryptionHelper;
		this.sendToPorticoDataDTOEditor = sendToPorticoDataDTOEditor;
		this.sendToPorticoDataDTOValidator = sendToPorticoDataDTOValidator;
		this.datePropertyEditor = datePropertyEditor;
		this.countryService = countryService;
		this.countryPropertyEditor = countryPropertyEditor;
		this.scoringDefinitionParser = scoringDefinitionParser;
		this.scoresPropertyEditor = scoresPropertyEditor;
		this.scoreFactory = scoreFactory;
	}

	@InitBinder(value = "refereesAdminEditDTO")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(refereesAdminEditDTOValidator);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
		binder.registerCustomEditor(Country.class, countryPropertyEditor);
		binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
		binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor());
		binder.registerCustomEditor(null, "scores", scoresPropertyEditor);
	}

	@ModelAttribute(value = "refereesAdminEditDTO")
	public RefereesAdminEditDTO getRefereesAdminEditDTO(@RequestParam String applicationId) throws ScoringDefinitionParseException {
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
		ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
		if (scoringDefinition != null) {
			CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
			List<Score> scores = scoreFactory.createScores(customQuestion.getQuestion());
			refereesAdminEditDTO.getScores().addAll(scores);
			refereesAdminEditDTO.setAlert(customQuestion.getAlert());
		}
		return refereesAdminEditDTO;
	}

	@RequestMapping(method = RequestMethod.GET, value = "moveToApproval")
	public String getMoveToApprovalPage(@RequestParam String applicationId, Model model) {
		model.addAttribute("approvalRound", getApprovalRound(applicationId));
		return APPROVAL_PAGE;
	}

	@RequestMapping(method = RequestMethod.GET, value = "supervisors_section")
	public String getSupervisorSection() {
		return SUPERVISORS_SECTION;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		RegisteredUser currentUser = userService.getCurrentUser();
		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
		if (application == null) {
			throw new MissingApplicationFormException(applicationId);
		}
		if (!currentUser.hasAdminRightsOnApplication(application) && !currentUser.isInRoleInProgram(Authority.APPROVER, application.getProgram())) {
			throw new InsufficientApplicationFormPrivilegesException(applicationId);
		}
		return application;
	}

	@ModelAttribute("actionsDefinition")
	public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId) {
		ApplicationForm application = getApplicationForm(applicationId);
		return applicationsService.getActionsDefinition(getUser(), application);
	}

	@ModelAttribute("nominatedSupervisors")
	public List<RegisteredUser> getNominatedSupervisors(@RequestParam String applicationId) {
		ArrayList<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
		if (latestApprovalRound == null) {
			List<SuggestedSupervisor> suggestedSupervisors = getApplicationForm(applicationId).getProgrammeDetails().getSuggestedSupervisors();
			for (SuggestedSupervisor suggestedSupervisor : suggestedSupervisors) {
				nominatedSupervisors.add(findOrCreateRegisterUserFromSuggestedSupervisorForForm(suggestedSupervisor, applicationForm));
			}
		}
		return nominatedSupervisors;
	}

	@ModelAttribute("programmeSupervisors")
	public List<RegisteredUser> getProgrammeSupervisors(@RequestParam String applicationId) {
		List<RegisteredUser> programmeSupervisors = getApplicationForm(applicationId).getProgram().getSupervisors();
		programmeSupervisors.removeAll(getNominatedSupervisors(applicationId));
		return programmeSupervisors;
	}

	@ModelAttribute("previousSupervisors")
	public List<RegisteredUser> getPreviousSupervisorsAndInterviewersWillingToSupervise(@RequestParam String applicationId) {
		List<RegisteredUser> availablePreviousSupervisors = new ArrayList<RegisteredUser>();
		ApplicationForm applicationForm = getApplicationForm(applicationId);
		availablePreviousSupervisors.addAll(userService.getAllPreviousSupervisorsOfProgram(applicationForm.getProgram()));
		
		List<RegisteredUser> interviewersWillingToSupervise = applicationForm.getUsersWillingToSupervise();
		for (RegisteredUser registeredUser : interviewersWillingToSupervise) {
			if (!listContainsId(registeredUser, applicationForm.getProgram().getSupervisors()) && !listContainsId(registeredUser, availablePreviousSupervisors)) {
				availablePreviousSupervisors.add(registeredUser);
			}
		}
		
		availablePreviousSupervisors.removeAll(getNominatedSupervisors(applicationId));
		availablePreviousSupervisors.removeAll(getProgrammeSupervisors(applicationId));
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
			if (!registeredUser.isSupervisorInApprovalRound(approvalRound)) {
				Supervisor supervisor = new Supervisor();
				supervisor.setUser(registeredUser);
				approvalRound.getSupervisors().add(supervisor);
			}
		}

		Date startDate = applicationForm.getProgrammeDetails().getStartDate();
		if (!applicationForm.isPrefferedStartDateWithinBounds()) {
			startDate = applicationForm.getEarliestPossibleStartDate();
		}
		approvalRound.setRecommendedStartDate(startDate);
		return approvalRound;
	}

	@ModelAttribute("explanation")
	public String getExplanation() {
		return "";
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@InitBinder("approvalRound")
	public void registerValidatorAndPropertyEditorForApprovalRound(WebDataBinder binder) {
		binder.setValidator(approvalRoundValidator);
		binder.registerCustomEditor(Supervisor.class, supervisorPropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
	}

	public StringTrimmerEditor newStringTrimmerEditor() {
		return new StringTrimmerEditor(false);
	}

	@InitBinder("comment")
	public void registerValidatorAndPropertyEditorForComment(WebDataBinder binder) {
		binder.setValidator(commentValidator);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
	}

	@InitBinder(value = "sendToPorticoData")
	public void registerSendToPorticoDataBinder(WebDataBinder binder) {
		binder.setValidator(sendToPorticoDataDTOValidator);
		binder.registerCustomEditor(List.class, sendToPorticoDataDTOEditor);
	}

	@ModelAttribute("sendToPorticoData")
	public SendToPorticoDataDTO getSendToPorticoDataDTO() {
		return new SendToPorticoDataDTO();
	}

	@RequestMapping(value = "/assignSupervisors", method = RequestMethod.POST)
	public String assignSupervisors(@ModelAttribute ApplicationForm applicationForm, @Valid @ModelAttribute("approvalRound") ApprovalRound approvalRound,
	                BindingResult bindingResult, SessionStatus sessionStatus) {
		if (bindingResult.hasErrors()) {
			return SUPERVISORS_SECTION;
		}

		approvalService.moveApplicationToApproval(applicationForm, approvalRound);
		sessionStatus.setComplete();
		return "/private/common/ajax_OK";
	}

	@RequestMapping(value = "/applyPorticoData", method = RequestMethod.POST)
	public String applySendToPorticoData(@ModelAttribute ApplicationForm applicationForm, @ModelAttribute("approvalRound") ApprovalRound approvalRound,
	                @Valid @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, BindingResult result) {
		if (sendToPorticoData.getQualificationsSendToPortico() == null || sendToPorticoData.getRefereesSendToPortico() == null) {
			throw new ResourceNotFoundException();
		}

		qualificationService.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
		refereeService.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());

		if (result.hasErrors()) {
			return PORTICO_VALIDATION_SECTION;
		}

		approvalRound.setMissingQualificationExplanation(sendToPorticoData.getEmptyQualificationsExplanation());
		return SUPERVISORS_SECTION;
	}

	@RequestMapping(value = "/postQualificationsData", method = RequestMethod.POST)
	public String submitQualificationsData(@ModelAttribute ApplicationForm applicationForm,
	                @Valid @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, BindingResult result) {
		if (sendToPorticoData.getQualificationsSendToPortico() == null) {
			throw new ResourceNotFoundException();
		}

		qualificationService.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());

		return QUALIFICATION_SECTION;
	}

	@RequestMapping(value = "/postRefereesData", method = RequestMethod.POST)
	public String submitRefereesData(@ModelAttribute ApplicationForm applicationForm,
	                @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, BindingResult porticoResult,
	                @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO, BindingResult referenceResult,
	                @RequestParam(required = false) Boolean forceSavingReference, Model model) throws ScoringDefinitionParseException {

		String editedRefereeId = refereesAdminEditDTO.getEditedRefereeId();
		model.addAttribute("editedRefereeId", editedRefereeId);

		// save "send to UCL" data first
		List<Integer> refereesSendToPortico = sendToPorticoData.getRefereesSendToPortico();
		if (refereesSendToPortico != null) {
			refereeService.selectForSendingToPortico(applicationForm, refereesSendToPortico);
		}

		if (!"newReferee".equals(editedRefereeId)) {
			Integer decryptedId = encryptionHelper.decryptToInteger(editedRefereeId);
			Referee referee = refereeService.getRefereeById(decryptedId);
			if (referee.getReference() != null) {
				return REFERENCE_SECTION;
			}
		}

		createScoresWithQuestion(applicationForm, refereesAdminEditDTO);
		if (BooleanUtils.isTrue(forceSavingReference) || refereesAdminEditDTO.hasUserStartedTyping()) {
			refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, referenceResult);

			if (referenceResult.hasErrors()) {
				return REFERENCE_SECTION;
			}

			ReferenceComment newComment = refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
			Referee referee = newComment.getReferee();
			applicationsService.refresh(applicationForm);
			refereeService.refresh(referee);

			String newRefereeId = encryptionHelper.encrypt(referee.getId());
			model.addAttribute("editedRefereeId", newRefereeId);

			// update referees send to Portico in order to validate it
			if (refereesSendToPortico != null && !refereesSendToPortico.contains(referee.getId())) {
				refereesSendToPortico.add(referee.getId());
			}
		}

		sendToPorticoDataDTOValidator.validate(sendToPorticoData, porticoResult);

		return REFERENCE_SECTION;
	}

	@ModelAttribute("comment")
	public RequestRestartComment getRequestRestartComment(@RequestParam String applicationId) {
		RequestRestartComment comment = new RequestRestartComment();
		comment.setApplication(getApplicationForm(applicationId));
		comment.setUser(getUser());
		return comment;
	}

	@ModelAttribute("countries")
	public List<Country> getAllCountries() {
		return countryService.getAllCountries();
	}

	@RequestMapping(value = "submitRequestRestart", method = RequestMethod.POST)
	public String requestRestart(@ModelAttribute("applicationForm") ApplicationForm applicationForm,
	                @Valid @ModelAttribute("comment") RequestRestartComment comment, BindingResult result) {
		if (result.hasErrors()) {
			return REQUEST_RESTART_APPROVE_PAGE;
		}
		approvalService.requestApprovalRestart(applicationForm, getUser(), comment);
		return "redirect:/applications?messageCode=request.approval.restart&application=" + applicationForm.getApplicationNumber();
	}

	private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
		for (RegisteredUser entry : users) {
			if (entry.getId().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}

	private void createScoresWithQuestion(ApplicationForm applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) throws ScoringDefinitionParseException {
		List<Score> scores = refereesAdminEditDTO.getScores();
		if (scores != null) {
			List<Question> questions = getCustomQuestions(applicationForm);
			for (int i = 0; i < scores.size(); i++) {
				Score score = scores.get(i);
				score.setOriginalQuestion(questions.get(i));
			}
		}
	}

	private RegisteredUser findOrCreateRegisterUserFromSuggestedSupervisorForForm(SuggestedSupervisor suggestedSupervisor, ApplicationForm applicationForm) {
		String supervisorEmail = suggestedSupervisor.getEmail();
		RegisteredUser possibleUser = userService.getUserByEmailIncludingDisabledAccounts(supervisorEmail);
		if (possibleUser == null) {
			possibleUser = userService.createNewUserInRole(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), supervisorEmail,
			                Authority.SUPERVISOR, DirectURLsEnum.VIEW_APPLIATION_AS_SUPERVISOR, applicationForm);
		}
		return possibleUser;
	}

	public List<Question> getCustomQuestions(ApplicationForm applicationForm) throws ScoringDefinitionParseException {
		ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
		if (scoringDefinition != null) {
			CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
			return customQuestion.getQuestion();
		}
		return null;
	}
}
