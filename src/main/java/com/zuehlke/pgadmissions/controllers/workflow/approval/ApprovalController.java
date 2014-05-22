package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.zuehlke.pgadmissions.controllers.workflow.EditApplicationFormAsProgrammeAdminController;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.CommentAssignedUserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.PorticoService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.validators.ApprovalCommentValidator;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;
import com.zuehlke.pgadmissions.validators.SendToPorticoDataDTOValidator;

@SessionAttributes("approvalRound")
@Controller
@RequestMapping("/approval")
public class ApprovalController extends EditApplicationFormAsProgrammeAdminController {
    // TODO change approvalRound to approvalComment, supervisor to assignedUser, removed supervisorPropertyEditor: try creating
    // CommentAssignedUserPropertyEditor, fix tests

    private static final String PROPOSE_OFFER_RECOMMENDATION_SECTION = "/private/staff/supervisors/propose_offer_recommendation";
    private static final String PORTICO_VALIDATION_SECTION = "/private/staff/supervisors/portico_validation_section";
    private static final String APPROVAL_PAGE = "/private/staff/supervisors/approval_details";
    private static final String QUALIFICATION_SECTION = "/private/staff/supervisors/components/qualification_portico_validation";
    private static final String REFERENCE_SECTION = "/private/staff/supervisors/components/reference_portico_validation";

    @Autowired
    private ApprovalCommentValidator approvalRoundValidator;
    
    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private GenericCommentValidator commentValidator;

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private SendToPorticoDataDTOValidator sendToPorticoDataDTOValidator;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private ApplicationService applicationsService;
    
    @Autowired
    private CommentAssignedUserPropertyEditor assignedUserPropertyEditor;
    
    @Autowired
    private PorticoService porticoService;

    @InitBinder(value = "sendToPorticoData")
    public void registerSendToPorticoData(WebDataBinder binder) {
        binder.setValidator(sendToPorticoDataDTOValidator);
        binder.registerCustomEditor(List.class, sendToPorticoDataDTOEditor);
    }

    @RequestMapping(method = RequestMethod.GET, value = "moveToApproval")
    public String getMoveToApprovalPage(ModelMap modelMap, @RequestParam(required = false) String action) {
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");

        actionService.validateAction(applicationForm, user, SystemAction.APPLICATION_ASSIGN_SUPERVISORS);

        modelMap.put("approvalRound", getApprovalRound(applicationForm.getApplicationNumber()));

        Comment latestApprovalComment = applicationsService.getLatestStateChangeComment(applicationForm, SystemAction.APPLICATION_COMPLETE_APPROVAL_STAGE);
        if (latestApprovalComment != null) {
            SendToPorticoDataDTO porticoData = new SendToPorticoDataDTO();
            porticoData.setApplicationNumber(applicationForm.getApplicationNumber());
            porticoData.setQualificationsSendToPortico(porticoService.getQualificationsToSendToPorticoIds(applicationForm));
            porticoData.setRefereesSendToPortico(porticoService.getRefereesToSendToPorticoIds(applicationForm));
            porticoData.setEmptyQualificationsExplanation(latestApprovalComment.getMissingQualificationExplanation());
            modelMap.put("sendToPorticoData", porticoData);
        }

        applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, user);
        return APPROVAL_PAGE;
    }

    @ModelAttribute
    public Application getApplicationForm(@RequestParam String applicationId) {
        Application applicationForm = applicationsService.getByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new ResourceNotFoundException(applicationId);
        }
        return applicationForm;
    }

    @ModelAttribute("approvalRound")
    public AssignSupervisorsComment getApprovalRound(String applicationId) {
        return approvalService.initiateApprovalComment(applicationId);
    }

    @ModelAttribute("usersInterestedInApplication")
    public List<User> getUsersInterestedInApplication(@RequestParam String applicationId) {
        // FIXME isSupervisorInApprovalRound method has been removed from RegisteredUser class, provide this information in other way (by moving the method into
        // aservice, or this method can return a map)
        return applicationFormUserRoleService.getUsersInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("usersPotentiallyInterestedInApplication")
    public List<User> getUsersPotentiallyInterestedInApplication(@RequestParam String applicationId) {
        return applicationFormUserRoleService.getUsersPotentiallyInterestedInApplication(getApplicationForm(applicationId));
    }

    @ModelAttribute("explanation")
    public String getExplanation() {
        return "";
    }

    @InitBinder("approvalRound")
    public void registerValidatorAndPropertyEditorForApprovalRound(WebDataBinder binder) {
        binder.setValidator(approvalRoundValidator);
         binder.registerCustomEditor(CommentAssignedUser.class, assignedUserPropertyEditor);
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
    public String assignSupervisors(ModelMap modelMap, @Valid @ModelAttribute("approvalComment") AssignSupervisorsComment approvalComment,
            BindingResult bindingResult, SessionStatus sessionStatus) {
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User initiator = getCurrentUser();
        actionService.validateAction(applicationForm, initiator, SystemAction.APPLICATION_ASSIGN_SUPERVISORS);

        if (bindingResult.hasErrors()) {
            return PROPOSE_OFFER_RECOMMENDATION_SECTION;
        }

        approvalService.moveApplicationToApproval(applicationForm, approvalComment, initiator);
        sessionStatus.setComplete();
        return "/private/common/ajax_OK";
    }

    @RequestMapping(value = "/applyPorticoData", method = RequestMethod.POST)
    public String applySendToPorticoData(@ModelAttribute Application applicationForm,
            @ModelAttribute("approvalRound") AssignSupervisorsComment approvalRound,
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
        applicationFormUserRoleService.applicationUpdated(applicationForm, getCurrentUser());
        return PROPOSE_OFFER_RECOMMENDATION_SECTION;
    }

    @RequestMapping(value = "/postQualificationsData", method = RequestMethod.POST)
    public String submitQualificationsData(@ModelAttribute Application applicationForm,
            @Valid @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, BindingResult result) {
        if (sendToPorticoData.getQualificationsSendToPortico() == null) {
            throw new ResourceNotFoundException();
        }

        qualificationService.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        applicationFormUserRoleService.applicationUpdated(applicationForm, getCurrentUser());
        return QUALIFICATION_SECTION;
    }

    @RequestMapping(value = "/postRefereesDataAndValidateForApproval", method = RequestMethod.POST)
    public String submitRefereesData(@ModelAttribute Application applicationForm,
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
            if (referee.getComment() != null) {
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

            // TODO get referee and refresh it, or do sth without refreshing
            Referee referee = null;
            // Referee referee = newComment.getReferee();
            // applicationsService.refresh(applicationForm);
            // refereeService.refresh(referee);

            applicationFormUserRoleService.applicationUpdated(applicationForm, getCurrentUser());
            applicationsService.saveUpdate(applicationForm);

            String newRefereeId = encryptionHelper.encrypt(referee.getId());
            model.addAttribute("editedRefereeId", newRefereeId);

            if (refereesSendToPortico != null && !refereesSendToPortico.contains(referee.getId())) {
                refereesSendToPortico.add(referee.getId());
            }
        }

        sendToPorticoDataDTOValidator.validate(sendToPorticoData, porticoResult);
        return REFERENCE_SECTION;
    }

    public List<Question> getCustomQuestions(Application applicationForm) throws ScoringDefinitionParseException {
        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
        if (scoringDefinition != null) {
            CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
            return customQuestion.getQuestion();
        }
        return null;
    }

}