package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDate;
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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.CompleteApprovalComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.ConfirmSupervisionDTOValidator;

@Controller
@RequestMapping("/confirmSupervision")
public class ConfirmSupervisionController {
    // TODO fix tests

    private static final String CONFIRM_SUPERVISION_PAGE = "/private/staff/supervisors/confirm_supervision_page";

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private ConfirmSupervisionDTOValidator confirmSupervisionDTOValidator;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(@RequestParam String applicationId) {
        Application application = applicationsService.getByApplicationNumber(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException(applicationId);
        }
        return application;
    }

    @ModelAttribute("applicationDescriptor")
    public Application getApplicationDescriptor(@RequestParam String applicationId) {
        Application applicationForm = getApplicationForm(applicationId);
        User user = getUser();
        return applicationsService.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("confirmSupervisionDTO")
    public ConfirmSupervisionDTO getConfirmSupervisionDTO(@RequestParam String applicationId) {
        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();

        Application applicationForm = getApplicationForm(applicationId);
        CompleteApprovalComment comment = (CompleteApprovalComment) applicationsService.getLatestStateChangeComment(applicationForm, SystemAction.APPLICATION_COMPLETE_APPROVAL_STAGE);

        confirmSupervisionDTO.setProjectTitle(comment.getPositionTitle());
        confirmSupervisionDTO.setProjectAbstract(comment.getPositionDescription());

        LocalDate startDate = comment.getPositionProvisionalStartDate();

        if (!programInstanceService.isPrefferedStartDateWithinBounds(applicationForm, startDate)) {
            startDate = programInstanceService.getEarliestPossibleStartDate(applicationForm);
        }

        confirmSupervisionDTO.setRecommendedStartDate(startDate);
        confirmSupervisionDTO.setRecommendedConditions(comment.getAppointmentConditions());

        return confirmSupervisionDTO;
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @InitBinder("confirmSupervisionDTO")
    public void registerValidatorAndPropertyEditorForApprovalRound(WebDataBinder binder) {
        binder.setValidator(confirmSupervisionDTOValidator);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @RequestMapping(method = RequestMethod.GET)
    public String confirmSupervision(ModelMap modelMap) {
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, SystemAction.APPLICATION_CONFIRM_SUPERVISION);
        workflowService.deleteApplicationUpdate(applicationForm, user);
        return CONFIRM_SUPERVISION_PAGE;
    }

    @RequestMapping(value = "applyConfirmSupervision", method = RequestMethod.POST)
    public String applyConfirmSupervision(@Valid ConfirmSupervisionDTO confirmSupervisionDTO, BindingResult result, ModelMap modelMap) {
        Application applicationForm = (Application) modelMap.get("applicationForm");
        User user = (User) modelMap.get("user");
        actionService.validateAction(applicationForm, user, SystemAction.APPLICATION_CONFIRM_SUPERVISION);

        if (result.hasErrors()) {
            return CONFIRM_SUPERVISION_PAGE;
        }

        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        workflowService.applicationUpdated(applicationForm, user);

        if (BooleanUtils.isTrue(confirmSupervisionDTO.getConfirmedSupervision())) {
            return "redirect:/applications?messageCode=supervision.confirmed&application=" + applicationForm.getApplicationNumber();
        } else {
            return "redirect:/applications?messageCode=supervision.declined&application=" + applicationForm.getApplicationNumber();
        }
    }

}
