package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
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

    ApprovalController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ApprovalController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService,
            ApprovalRoundValidator approvalRoundValidator, SupervisorPropertyEditor supervisorPropertyEditor, DocumentPropertyEditor documentPropertyEditor,
            GenericCommentValidator commentValidator, RefereesAdminEditDTOValidator refereesAdminEditDTOValidator, QualificationService qualificationService,
            RefereeService refereeService, EncryptionHelper encryptionHelper, SendToPorticoDataDTOEditor sendToPorticoDataDTOEditor,
            SendToPorticoDataDTOValidator sendToPorticoDataDTOValidator) {
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
    }

    @InitBinder(value = "refereesAdminEditDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereesAdminEditDTOValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor());
    }

    @ModelAttribute(value = "refereesAdminEditDTO")
    public RefereesAdminEditDTO getRefereesAdminEditDTO() {
        return new RefereesAdminEditDTO();
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
            if (!listContainsId(registeredUser, applicationForm.getProgram().getSupervisors())) {
                availablePreviousSupervisors.add(registeredUser);
            }
        }
        List<RegisteredUser> interviewersWillingToSupervise = applicationForm.getUsersWillingToSupervise();
        for (RegisteredUser registeredUser : interviewersWillingToSupervise) {
            if (!listContainsId(registeredUser, applicationForm.getProgram().getSupervisors()) && !listContainsId(registeredUser, availablePreviousSupervisors)) {
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
            if (!registeredUser.isSupervisorInApprovalRound(approvalRound)) {
                Supervisor supervisor = new Supervisor();
                supervisor.setUser(registeredUser);
                approvalRound.getSupervisors().add(supervisor);
            }
        }
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
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    @ModelAttribute("sendToPorticoData")
    public SendToPorticoDataDTO getSendToPorticoDataDTO() {
        return new SendToPorticoDataDTO();
    }

    @RequestMapping(value = "/assignSupervisors", method = RequestMethod.POST)
    public String assignSupervisors(@RequestParam String applicationId, @Valid @ModelAttribute("approvalRound") ApprovalRound approvalRound,
            BindingResult bindingResult, SessionStatus sessionStatus) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (bindingResult.hasErrors()) {
            return SUPERVISORS_SECTION;
        }

        if (!approvalService.validateSendToPorticoData(applicationForm, null)) {
            return PORTICO_VALIDATION_SECTION;
        }

        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
        sessionStatus.setComplete();
        return "/private/common/ajax_OK";
    }

    @RequestMapping(value = "/applyPorticoData", method = RequestMethod.POST)
    public String applySendToPorticoData(@ModelAttribute ApplicationForm applicationForm, @ModelAttribute("approvalRound") ApprovalRound approvalRound,
            @Valid @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, BindingResult result, SessionStatus sessionStatus) {
        if (sendToPorticoData.getQualificationsSendToPortico() == null || sendToPorticoData.getRefereesSendToPortico() == null) {
            throw new ResourceNotFoundException();
        }

        qualificationService.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        refereeService.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());

        if (result.hasErrors()) {
            return PORTICO_VALIDATION_SECTION;
        }

        approvalService.moveApplicationToApproval(applicationForm, approvalRound);
        sessionStatus.setComplete();
        return "/private/common/ajax_OK";
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
            @Valid @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData, BindingResult porticoResult, @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO, BindingResult referenceResult, Model model) {
        if (sendToPorticoData.getRefereesSendToPortico() == null) {
            throw new ResourceNotFoundException();
        }
        
        model.addAttribute("editedRefereeId", refereesAdminEditDTO.getEditedRefereeId());

        refereeService.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());

        if (refereesAdminEditDTO.hasUserStartedTyping()) {
            refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, referenceResult);

            if (referenceResult.hasErrors()) {
                return REFERENCE_SECTION;
            }

            Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
            Referee referee = refereeService.getRefereeById(refereeId);

            refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
            refereeService.refresh(referee);
        }
        
        return REFERENCE_SECTION;
    }

    @RequestMapping(value = "/postReference", method = RequestMethod.POST)
    public String submitReference(@Valid @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO, BindingResult result,
            @ModelAttribute ApplicationForm applicationForm, Model model) {
        model.addAttribute("editedRefereeId", refereesAdminEditDTO.getEditedRefereeId());

        Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
        Referee referee = refereeService.getRefereeById(refereeId);

        if (referee.getReference() == null) {
            // reference not uploaded yet, try to do it now
            if (result.hasErrors()) {
                return REFERENCE_SECTION;
            }

            refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
            refereeService.refresh(referee);
        }

        return REFERENCE_SECTION;
    }

    @ModelAttribute("comment")
    public RequestRestartComment getRequestRestartComment(@RequestParam String applicationId) {
        RequestRestartComment comment = new RequestRestartComment();
        comment.setApplication(getApplicationForm(applicationId));
        comment.setUser(getUser());
        return comment;
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

}
