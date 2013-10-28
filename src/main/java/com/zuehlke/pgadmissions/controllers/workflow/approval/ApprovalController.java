package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.components.ApplicationDescriptorProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.controllers.workflow.EditApplicationFormAsProgrammeAdminController;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
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
public class ApprovalController extends EditApplicationFormAsProgrammeAdminController {

    private static final String PROPOSE_OFFER_RECOMMENDATION_SECTION = "/private/staff/supervisors/propose_offer_recommendation";
    private static final String PORTICO_VALIDATION_SECTION = "/private/staff/supervisors/portico_validation_section";
    private static final String APPROVAL_PAGE = "/private/staff/supervisors/approval_details";
    private static final String QUALIFICATION_SECTION = "/private/staff/supervisors/components/qualification_portico_validation";
    private static final String REFERENCE_SECTION = "/private/staff/supervisors/components/reference_portico_validation";

    private final ApprovalRoundValidator approvalRoundValidator;

    private final SupervisorPropertyEditor supervisorPropertyEditor;

    private final ApprovalService approvalService;

    private final GenericCommentValidator commentValidator;

    private final QualificationService qualificationService;
    
    private final SendToPorticoDataDTOValidator sendToPorticoDataDTOValidator;

    private final DatePropertyEditor datePropertyEditor;
    
    private final ActionsProvider actionsProvider;

    private final ProgramInstanceService programInstanceService;

    public ApprovalController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @InitBinder(value = "sendToPorticoData")
    public void registerSendToPorticoData(WebDataBinder binder) {
    	binder.setValidator(sendToPorticoDataDTOValidator);
        binder.registerCustomEditor(List.class, sendToPorticoDataDTOEditor);
    }
    
    @Autowired
    public ApprovalController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService,
            ApprovalRoundValidator approvalRoundValidator, SupervisorPropertyEditor supervisorPropertyEditor, DocumentPropertyEditor documentPropertyEditor,
            GenericCommentValidator commentValidator, RefereesAdminEditDTOValidator refereesAdminEditDTOValidator, QualificationService qualificationService,
            RefereeService refereeService, EncryptionHelper encryptionHelper, SendToPorticoDataDTOEditor sendToPorticoDataDTOEditor,
            SendToPorticoDataDTOValidator sendToPorticoDataDTOValidator, DatePropertyEditor datePropertyEditor, DomicileService domicileService,
            DomicilePropertyEditor domicilePropertyEditor, MessageSource messageSource, ScoringDefinitionParser scoringDefinitionParser, ScoresPropertyEditor 
            scoresPropertyEditor, ScoreFactory scoreFactory, final ApplicationFormAccessService accessService, final ActionsProvider actionsProvider,
            final ApplicationDescriptorProvider applicationDescriptorProvider, ProgramInstanceService programInstanceService) {
    	super(userService, applicationsService, documentPropertyEditor, refereeService, refereesAdminEditDTOValidator, sendToPorticoDataDTOEditor,
                encryptionHelper, messageSource, scoringDefinitionParser, scoresPropertyEditor, scoreFactory, applicationDescriptorProvider,
                domicileService, domicilePropertyEditor, accessService);
        this.approvalService = approvalService;
        this.approvalRoundValidator = approvalRoundValidator;
        this.supervisorPropertyEditor = supervisorPropertyEditor;
        this.commentValidator = commentValidator;
        this.qualificationService = qualificationService;
        this.sendToPorticoDataDTOValidator = sendToPorticoDataDTOValidator;
        this.datePropertyEditor = datePropertyEditor;
        this.actionsProvider = actionsProvider;
        this.programInstanceService = programInstanceService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "moveToApproval")
    public String getMoveToApprovalPage(ModelMap modelMap,
    		@ModelAttribute ApprovalRound approvalRound,
    		BindingResult approvalRoundResult,
    		@RequestParam(required = false) String action) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.ASSIGN_SUPERVISORS);
    	
        if (applicationForm.getLatestApprovalRound() != null) {
	        	SendToPorticoDataDTO porticoData = new SendToPorticoDataDTO();
	        	porticoData.setApplicationNumber(applicationForm.getApplicationNumber());
	        	porticoData.setQualificationsSendToPortico(applicationForm.getQualicationsToSendToPorticoIds());
	        	porticoData.setRefereesSendToPortico(applicationForm.getRefereesToSendToPorticoIds());
	        	porticoData.setEmptyQualificationsExplanation(applicationForm.getLatestApprovalRound().getMissingQualificationExplanation());
	        	modelMap.put("sendToPorticoData", porticoData);
        }
        return APPROVAL_PAGE;
    }

    @RequestMapping(method = RequestMethod.GET, value = "supervisors_section")
    public String getSupervisorSection() {
        return PROPOSE_OFFER_RECOMMENDATION_SECTION;
    }
    
    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!getCurrentUser().hasAdminRightsOnApplication(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        return applicationForm;
    }
    
    @ModelAttribute("nominatedSupervisors")
    public List<RegisteredUser> getNominatedSupervisors(@RequestParam String applicationId) {
        ArrayList<RegisteredUser> nominatedSupervisors = new ArrayList<RegisteredUser>();
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        ApprovalRound latestApprovalRound = applicationForm.getLatestApprovalRound();
        if (latestApprovalRound == null) {
            List<SuggestedSupervisor> suggestedSupervisors = applicationForm.getProgrammeDetails().getSuggestedSupervisors();
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
        
        Project project = applicationForm.getProject();
        boolean applicationHasProject = project != null;
        
        Date startDate = applicationForm.getProgrammeDetails().getStartDate();
        
        if (latestApprovalRound != null) {	
    	   
        	for (Supervisor supervisor : latestApprovalRound.getSupervisors()) {
        		if (!supervisor.hasDeclinedSupervision()) {
        			approvalRound.getSupervisors().add(supervisor);
        		}
        	}
        	
        	if (latestApprovalRound.getProjectDescriptionAvailable() != null) {
        		approvalRound.setProjectDescriptionAvailable(latestApprovalRound.getProjectDescriptionAvailable());
	            approvalRound.setProjectTitle(latestApprovalRound.getProjectTitle());
	            approvalRound.setProjectAbstract(latestApprovalRound.getProjectAbstract());
        	}
        	
        	startDate = latestApprovalRound.getRecommendedStartDate();
        	
        	if (latestApprovalRound.getRecommendedConditionsAvailable() != null) {
	            approvalRound.setRecommendedConditionsAvailable(latestApprovalRound.getRecommendedConditionsAvailable());
	            approvalRound.setRecommendedConditions(latestApprovalRound.getRecommendedConditions());
        	}
        	
        }
        
        else if (applicationHasProject) {
        	
            addUserAsSupervisorInApprovalRound(project.getPrimarySupervisor(), approvalRound, true);
            addUserAsSupervisorInApprovalRound(project.getSecondarySupervisor(), approvalRound, false);
            
        	approvalRound.setProjectDescriptionAvailable(true);
	        approvalRound.setProjectTitle(project.getAdvert().getTitle());
	        approvalRound.setProjectAbstract(project.getAdvert().getDescription());
	        approvalRound.setProjectAcceptingApplications(project.getAdvert().getActive());

	    }
        
        if (!programInstanceService.isPrefferedStartDateWithinBounds(applicationForm, startDate)) {
        	startDate = programInstanceService.getEarliestPossibleStartDate(applicationForm);
        }
        
        approvalRound.setRecommendedStartDate(startDate);
        
        List<RegisteredUser> interviewersWillingToSupervise = applicationForm.getUsersWillingToSupervise();
        for (RegisteredUser registeredUser : interviewersWillingToSupervise) {
            addUserAsSupervisorInApprovalRound(registeredUser, approvalRound, false);
        }

        return approvalRound;
    }

    private void addUserAsSupervisorInApprovalRound(RegisteredUser user, ApprovalRound approvalRound, boolean isPrimary) {
        if (user == null || approvalRound == null || user.isSupervisorInApprovalRound(approvalRound)) {
            return;
        }
        Supervisor supervisor = new Supervisor();
        supervisor.setIsPrimary(isPrimary);
        supervisor.setUser(user);
        approvalRound.getSupervisors().add(supervisor);
    }

    @ModelAttribute("explanation")
    public String getExplanation() {
        return "";
    }

    @InitBinder("approvalRound")
    public void registerValidatorAndPropertyEditorForApprovalRound(WebDataBinder binder) {
        binder.setValidator(approvalRoundValidator);
        binder.registerCustomEditor(Supervisor.class, supervisorPropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    @InitBinder("comment")
    public void registerValidatorAndPropertyEditorForComment(WebDataBinder binder) {
        binder.setValidator(commentValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    @ModelAttribute("sendToPorticoData")
    public SendToPorticoDataDTO getSendToPorticoDataDTO() {
        return new SendToPorticoDataDTO();
    }

    @RequestMapping(value = "/assignSupervisors", method = RequestMethod.POST)
    public String assignSupervisors(ModelMap modelMap, 
    		@Valid @ModelAttribute("approvalRound") ApprovalRound approvalRound, 
    		BindingResult bindingResult,
            SessionStatus sessionStatus) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.ASSIGN_SUPERVISORS);

        if (bindingResult.hasErrors()) {
            return PROPOSE_OFFER_RECOMMENDATION_SECTION;
        }

        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));        
        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
        accessService.updateAccessTimestamp(applicationForm, userService.getCurrentUser(), new Date());
        sessionStatus.setComplete();
        return "/private/common/ajax_OK";
    }

    @RequestMapping(value = "/applyPorticoData", method = RequestMethod.POST)
    public String applySendToPorticoData(@ModelAttribute ApplicationForm applicationForm, 
    		@ModelAttribute("approvalRound") ApprovalRound approvalRound,
            @Valid @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, 
            BindingResult result) {
        if (sendToPorticoData.getQualificationsSendToPortico() == null || sendToPorticoData.getRefereesSendToPortico() == null) {
            throw new ResourceNotFoundException();
        }

        qualificationService.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        refereeService.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());

        if (result.hasErrors()) {
            return PORTICO_VALIDATION_SECTION;
        }

        approvalRound.setMissingQualificationExplanation(sendToPorticoData.getEmptyQualificationsExplanation());
        return PROPOSE_OFFER_RECOMMENDATION_SECTION;
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

    @RequestMapping(value = "/postRefereesDataAndValidateForApproval", method = RequestMethod.POST)
    public String submitRefereesData(@ModelAttribute ApplicationForm applicationForm,
            @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, 
            BindingResult porticoResult,
            @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO, 
            BindingResult referenceResult,
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

            applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
            accessService.updateAccessTimestamp(applicationForm, userService.getCurrentUser(), new Date());
            applicationsService.save(applicationForm);

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

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    private RegisteredUser findOrCreateRegisterUserFromSuggestedSupervisorForForm(SuggestedSupervisor suggestedSupervisor, ApplicationForm applicationForm) {
        String supervisorEmail = suggestedSupervisor.getEmail();
        RegisteredUser possibleUser = userService.getUserByEmailIncludingDisabledAccounts(supervisorEmail);
        if (possibleUser == null) {
            possibleUser = userService.createNewUserInRole(suggestedSupervisor.getFirstname(), suggestedSupervisor.getLastname(), supervisorEmail,
                    DirectURLsEnum.VIEW_APPLIATION_AS_SUPERVISOR, applicationForm, Authority.SUPERVISOR);
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