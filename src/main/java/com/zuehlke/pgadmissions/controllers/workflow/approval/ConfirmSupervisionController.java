package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CompleteApprovalComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ConfirmSupervisionDTOValidator;

@Controller
@RequestMapping("/confirmSupervision")
public class ConfirmSupervisionController {
    // TODO fix tests

    private static final String CONFIRM_SUPERVISION_PAGE = "/private/staff/supervisors/confirm_supervision_page";

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private DatePropertyEditor datePropertyEditor;

    @Autowired
    private ConfirmSupervisionDTOValidator confirmSupervisionDTOValidator;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    private ActionsProvider actionsProvider;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("confirmSupervisionDTO")
    public ConfirmSupervisionDTO getConfirmSupervisionDTO(@RequestParam String applicationId) {
        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();

        ApplicationForm applicationForm = getApplicationForm(applicationId);
        CompleteApprovalComment comment = (CompleteApprovalComment) applicationsService.getLatestStateChangeComment(applicationForm, ApplicationFormAction.COMPLETE_APPROVAL_STAGE);

        confirmSupervisionDTO.setProjectTitle(comment.getProjectTitle());
        confirmSupervisionDTO.setProjectAbstract(comment.getProjectAbstract());

        Date startDate = comment.getRecommendedStartDate();

        if (!programInstanceService.isPrefferedStartDateWithinBounds(applicationForm, startDate)) {
            startDate = programInstanceService.getEarliestPossibleStartDate(applicationForm);
        }

        confirmSupervisionDTO.setRecommendedStartDate(startDate);
        confirmSupervisionDTO.setRecommendedConditionsAvailable(comment.getRecommendedConditionsAvailable());
        confirmSupervisionDTO.setRecommendedConditions(comment.getRecommendedConditions());

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
    public String confirmSupervision(ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION);
        applicationFormUserRoleService.deregisterApplicationUpdate(applicationForm, user);
        return CONFIRM_SUPERVISION_PAGE;
    }

    @RequestMapping(value = "applyConfirmSupervision", method = RequestMethod.POST)
    public String applyConfirmSupervision(@Valid ConfirmSupervisionDTO confirmSupervisionDTO, BindingResult result, ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, ApplicationFormAction.CONFIRM_PRIMARY_SUPERVISION);

        if (result.hasErrors()) {
            return CONFIRM_SUPERVISION_PAGE;
        }

        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, user, ApplicationUpdateScope.INTERNAL);

        if (BooleanUtils.isTrue(confirmSupervisionDTO.getConfirmedSupervision())) {
            return "redirect:/applications?messageCode=supervision.confirmed&application=" + applicationForm.getApplicationNumber();
        } else {
            return "redirect:/applications?messageCode=supervision.declined&application=" + applicationForm.getApplicationNumber();
        }
    }

}
