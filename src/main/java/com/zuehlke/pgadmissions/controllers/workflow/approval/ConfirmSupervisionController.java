package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_SUPERVISION;

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
import com.zuehlke.pgadmissions.components.ApplicationDescriptorProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
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

    private final ApplicationFormAccessService accessService;

    private final ActionsProvider actionsProvider;

    private final ApplicationDescriptorProvider applicationDescriptorProvider;
    
    private final ProgramInstanceService programInstanceService;

    public ConfirmSupervisionController() {
        this(null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ConfirmSupervisionController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService,
                    DatePropertyEditor datePropertyEditor, ConfirmSupervisionDTOValidator confirmSupervisionDTOValidator,
                    ApplicationFormAccessService accessService, ActionsProvider actionsProvider, 
                    ApplicationDescriptorProvider applicationDescriptorProvider, ProgramInstanceService programInstanceService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.approvalService = approvalService;
        this.datePropertyEditor = datePropertyEditor;
        this.confirmSupervisionDTOValidator = confirmSupervisionDTOValidator;
        this.accessService = accessService;
        this.actionsProvider = actionsProvider;
        this.applicationDescriptorProvider = applicationDescriptorProvider;
        this.programInstanceService = programInstanceService;
    }

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
        return applicationDescriptorProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    @ModelAttribute("confirmSupervisionDTO")
    public ConfirmSupervisionDTO getConfirmSupervisionDTO(@RequestParam String applicationId) {
        ConfirmSupervisionDTO confirmSupervisionDTO = new ConfirmSupervisionDTO();

        ApplicationForm applicationForm = getApplicationForm(applicationId);
        ApprovalRound approvalRound = applicationForm.getLatestApprovalRound();

        confirmSupervisionDTO.setProjectTitle(approvalRound.getProjectTitle());
        confirmSupervisionDTO.setProjectAbstract(approvalRound.getProjectAbstract());
        
        Date startDate = approvalRound.getRecommendedStartDate();
        
        if (!programInstanceService.isPrefferedStartDateWithinBounds(applicationForm, startDate)) {
        	startDate = programInstanceService.getEarliestPossibleStartDate(applicationForm);
        }
        
        confirmSupervisionDTO.setRecommendedStartDate(startDate);
        confirmSupervisionDTO.setRecommendedConditionsAvailable(approvalRound.getRecommendedConditionsAvailable());
        confirmSupervisionDTO.setRecommendedConditions(approvalRound.getRecommendedConditions()); 
        confirmSupervisionDTO.setProjectAcceptingApplications(approvalRound.getProjectAcceptingApplications());

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
        actionsProvider.validateAction(applicationForm, user, CONFIRM_SUPERVISION);
        accessService.deregisterApplicationUpdate(applicationForm, user);
        return CONFIRM_SUPERVISION_PAGE;
    }

    @RequestMapping(value = "applyConfirmSupervision", method = RequestMethod.POST)
    public String applyConfirmSupervision(@Valid ConfirmSupervisionDTO confirmSupervisionDTO, BindingResult result, ModelMap modelMap) {
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        RegisteredUser user = (RegisteredUser) modelMap.get("user");
        actionsProvider.validateAction(applicationForm, user, CONFIRM_SUPERVISION);

        if (result.hasErrors()) {
            return CONFIRM_SUPERVISION_PAGE;
        }

        approvalService.confirmOrDeclineSupervision(applicationForm, confirmSupervisionDTO);
        accessService.registerApplicationUpdate(applicationForm, new Date(), ApplicationUpdateScope.INTERNAL);

        applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.INTERNAL, new Date()));
        accessService.updateAccessTimestamp(applicationForm, getUser(), new Date());

        if (BooleanUtils.isTrue(confirmSupervisionDTO.getConfirmedSupervision())) {
            return "redirect:/applications?messageCode=supervision.confirmed&application=" + applicationForm.getApplicationNumber();
        } else {
            return "redirect:/applications?messageCode=supervision.declined&application=" + applicationForm.getApplicationNumber();
        }
    }

}
