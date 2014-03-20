package com.zuehlke.pgadmissions.controllers.workflow.approval;

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
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.controllers.workflow.EditApplicationFormAsProgrammeAdminController;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
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
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.DomicileService;
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

    public ApprovalController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
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
            DomicilePropertyEditor domicilePropertyEditor, MessageSource messageSource, ScoringDefinitionParser scoringDefinitionParser,
            ScoresPropertyEditor scoresPropertyEditor, ScoreFactory scoreFactory, ApplicationFormUserRoleService applicationFormUserRoleService,
            ActionsProvider actionsProvider) {
        super(userService, applicationsService, documentPropertyEditor, refereeService, refereesAdminEditDTOValidator, sendToPorticoDataDTOEditor,
                encryptionHelper, messageSource, scoringDefinitionParser, scoresPropertyEditor, scoreFactory, domicileService, domicilePropertyEditor,
                applicationFormUserRoleService, actionsProvider);
        this.approvalService = approvalService;
        this.approvalRoundValidator = approvalRoundValidator;
        this.supervisorPropertyEditor = supervisorPropertyEditor;
        this.commentValidator = commentValidator;
        this.qualificationService = qualificationService;
        this.sendToPorticoDataDTOValidator = sendToPorticoDataDTOValidator;
        this.datePropertyEditor = datePropertyEditor;
    }

    @RequestMapping(method = RequestMethod.GET, value = "moveToApproval")
    public String getMoveToApprovalPage(ModelMap modelMap, @RequestParam(required = false) String action) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser registeredUser = (RegisteredUser) modelMap.get("user");
        
        actionsProvider.validateAction(applicationForm, registeredUser, ApplicationFormAction.ASSIGN_SUPERVISORS);
        
        modelMap.put("approvalRound", getApprovalRound(applicationForm.getApplicationNumber()));

        if (applicationForm.getLatestApprovalRound() != null) {
            SendToPorticoDataDTO porticoData = new SendToPorticoDataDTO();
            porticoData.setApplicationNumber(applicationForm.getApplicationNumber());
            porticoData.setQualificationsSendToPortico(applicationForm.getQualicationsToSendToPorticoIds());
            porticoData.setRefereesSendToPortico(applicationForm.getRefereesToSendToPorticoIds());
            porticoData.setEmptyQualificationsExplanation(applicationForm.getLatestApprovalRound().getMissingQualificationExplanation());
            modelMap.put("sendToPorticoData", porticoData);
        }
        
        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, registeredUser);
        return APPROVAL_PAGE;
    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return applicationForm;
    }  

    @ModelAttribute("approvalRound")
    public ApprovalRound getApprovalRound(String applicationId) {
       return approvalService.initiateApprovalRound(applicationId);
    }
    
    @ModelAttribute("usersInterestedInApplication") 
    public List<RegisteredUser> getUsersInterestedInApplication (@RequestParam String applicationId) {
    	return applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId));
    }
    
    @ModelAttribute("usersPotentiallyInterestedInApplication") 
    public List<RegisteredUser> getUsersPotentiallyInterestedInApplication (@RequestParam String applicationId) {
    	return applicationFormUserRoleService.getUsersPotentiallyInterestedInApplication(getApplicationForm(applicationId));
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
    public String assignSupervisors(ModelMap modelMap, @Valid @ModelAttribute("approvalRound") ApprovalRound approvalRound, BindingResult bindingResult,
            SessionStatus sessionStatus) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser initiator = getCurrentUser();
        actionsProvider.validateAction(applicationForm, initiator, ApplicationFormAction.ASSIGN_SUPERVISORS);

        if (bindingResult.hasErrors()) {
            return PROPOSE_OFFER_RECOMMENDATION_SECTION;
        }

        approvalService.moveApplicationToApproval(applicationForm, approvalRound, initiator);
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
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
        return PROPOSE_OFFER_RECOMMENDATION_SECTION;
    }

    @RequestMapping(value = "/postQualificationsData", method = RequestMethod.POST)
    public String submitQualificationsData(@ModelAttribute ApplicationForm applicationForm,
            @Valid @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, BindingResult result) {
        if (sendToPorticoData.getQualificationsSendToPortico() == null) {
            throw new ResourceNotFoundException();
        }

        qualificationService.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
        return QUALIFICATION_SECTION;
    }

    @RequestMapping(value = "/postRefereesDataAndValidateForApproval", method = RequestMethod.POST)
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
            Referee referee = refereeService.getById(decryptedId);
            if (referee.getReference() != null) {
                return REFERENCE_SECTION;
            }
        }

        createScoresWithQuestion(applicationForm, refereesAdminEditDTO);

        if (BooleanUtils.isTrue(forceSavingReference) || refereesAdminEditDTO.hasUserStartedTyping()
                || (BooleanUtils.isTrue(forceSavingReference) && BooleanUtils.isFalse(refereesAdminEditDTO.getContainsRefereeData()))) {

            refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, referenceResult);

            if (referenceResult.hasErrors()) {
                return REFERENCE_SECTION;
            }

            ReferenceComment newComment = refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
            Referee referee = newComment.getReferee();
            applicationsService.refresh(applicationForm);
            refereeService.refresh(referee);

            applicationFormUserRoleService.insertApplicationUpdate(applicationForm, getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
            applicationsService.save(applicationForm);

            String newRefereeId = encryptionHelper.encrypt(referee.getId());
            model.addAttribute("editedRefereeId", newRefereeId);

            if (refereesSendToPortico != null && !refereesSendToPortico.contains(referee.getId())) {
                refereesSendToPortico.add(referee.getId());
            }
        }

        sendToPorticoDataDTOValidator.validate(sendToPorticoData, porticoResult);
        return REFERENCE_SECTION;
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