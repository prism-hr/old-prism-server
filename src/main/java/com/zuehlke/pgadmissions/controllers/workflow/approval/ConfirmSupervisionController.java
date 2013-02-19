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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
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

@Controller
@RequestMapping("/confirmSupervision")
public class ConfirmSupervisionController {

    private static final String CONFIRM_SUPERVISION_PAGE = "/private/staff/supervisors/confirm_supervision_page";

    private final ApplicationsService applicationsService;

    private final UserService userService;

    private final ApprovalService approvalService;

    private final DatePropertyEditor datePropertyEditor;

    public ConfirmSupervisionController() {
        this(null, null, null, null);
    }

    @Autowired
    public ConfirmSupervisionController(ApplicationsService applicationsService, UserService userService, ApprovalService approvalService,
            DatePropertyEditor datePropertyEditor) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.approvalService = approvalService;
        this.datePropertyEditor = datePropertyEditor;
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
    
    @ModelAttribute("confirmSupervisionDTO")
    public ConfirmSupervisionDTO getConfirmSupervisionDTO(@RequestParam String applicationId){
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

    @RequestMapping(value = "confirmSupervision", method = RequestMethod.GET)
    public String confirmSupervision(ApplicationForm applicationForm) {
        return CONFIRM_SUPERVISION_PAGE;
    }

    @RequestMapping(value = "applyConfirmSupervision", method = RequestMethod.POST)
    public String applyConfirmSupervision(ApplicationForm applicationForm, @Valid ConfirmSupervisionDTO confirmSupervisionDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return CONFIRM_SUPERVISION_PAGE;
        }
        return CONFIRM_SUPERVISION_PAGE;
    }

}
