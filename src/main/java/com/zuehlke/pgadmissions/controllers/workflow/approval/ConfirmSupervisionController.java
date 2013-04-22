package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.exceptions.application.PrimarySupervisorNotDefinedException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ConfirmSupervisionDTOValidator;

@Controller
@RequestMapping("/confirmSupervision")
public class ConfirmSupervisionController {

    private static final String CONFIRM_SUPERVISION_PAGE = "/private/staff/supervisors/confirm_supervision_page";

    private final ApplicationsService applicationsService;

    private final UserService userService;

    private final ApprovalService approvalService;

    private final DatePropertyEditor datePropertyEditor;

    private final ConfirmSupervisionDTOValidator confirmSupervisionDTOValidator;

    public ConfirmSupervisionController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public ConfirmSupervisionController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService,
            DatePropertyEditor datePropertyEditor, ConfirmSupervisionDTOValidator confirmSupervisionDTOValidator) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.approvalService = approvalService;
        this.datePropertyEditor = datePropertyEditor;
        this.confirmSupervisionDTOValidator = confirmSupervisionDTOValidator;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null){
            throw new MissingApplicationFormException(applicationId);
        }
        Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
        if(primarySupervisor == null) {
            throw new PrimarySupervisorNotDefinedException(applicationId);
        }
        if(getUser().getId() != primarySupervisor.getUser().getId()){
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }
        if(primarySupervisor.getConfirmedSupervision() != null){
            throw new ActionNoLongerRequiredException(applicationId);
        }
        return application;
    }
    
    @ModelAttribute("actionsDefinition")
    public ApplicationActionsDefinition getActionsDefinition(@RequestParam String applicationId){
        ApplicationForm application = getApplicationForm(applicationId);
        return applicationsService.getActionsDefinition(getUser(), application);
    }

    @ModelAttribute("confirmSupervisionDTO")
    public ConfirmSupervisionDTO getConfirmSupervisionDTO(@RequestParam String applicationId) {
        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();

        ApplicationForm applicationForm = getApplicationForm(applicationId);
        ApprovalRound approvalRound = applicationForm.getLatestApprovalRound();

        confirmSupervisionDTO.setProjectTitle(approvalRound.getProjectTitle());
        confirmSupervisionDTO.setProjectAbstract(approvalRound.getProjectAbstract());
        confirmSupervisionDTO.setRecommendedStartDate(approvalRound.getRecommendedStartDate());
        confirmSupervisionDTO.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
        confirmSupervisionDTO.setRecommendedConditions(approvalRound.getRecommendedConditions());

        return confirmSupervisionDTO;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @InitBinder("confirmSupervisionDTO")
    public void registerValidatorAndPropertyEditorForApprovalRound(WebDataBinder binder) {
        binder.setValidator(confirmSupervisionDTOValidator);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @RequestMapping(value = "confirmSupervision", method = RequestMethod.GET)
    public String confirmSupervision(ApplicationForm applicationForm) {
        return CONFIRM_SUPERVISION_PAGE;
    }

    @RequestMapping(value = "applyConfirmSupervision", method = RequestMethod.POST)
    public String applyConfirmSupervision(ApplicationForm applicationForm, @Valid ConfirmSupervisionDTO confirmSupervisionDTO, BindingResult result) {
        if (result.hasErrors()) {
            return CONFIRM_SUPERVISION_PAGE;
        }
        
        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        
        if (BooleanUtils.isTrue(confirmSupervisionDTO.getConfirmedSupervision())) {
            return "redirect:/applications?messageCode=supervision.confirmed&application=" + applicationForm.getApplicationNumber();
        } else {
            return "redirect:/applications?messageCode=supervision.declined&application=" + applicationForm.getApplicationNumber();
        }
    }

}
