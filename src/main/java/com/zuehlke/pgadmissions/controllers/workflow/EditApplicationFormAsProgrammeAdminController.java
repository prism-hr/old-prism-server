package com.zuehlke.pgadmissions.controllers.workflow;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;

@Controller
@RequestMapping("/editApplicationFormAsProgrammeAdmin")
public class EditApplicationFormAsProgrammeAdminController {

    private static final String VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_VIEW_NAME = "/private/staff/admin/application/main_application_page_programme_administrator";

    private static final String VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME = "/private/staff/admin/application/components/references_details_programme_admin";

    private final UserService userService;

    private final ApplicationsService applicationService;

    private final DocumentPropertyEditor documentPropertyEditor;

    private final QualificationService qualificationService;

    private final RefereeService refereeService;

    private final RefereesAdminEditDTOValidator refereesAdminEditDTOValidator;

    private final EncryptionHelper encryptionHelper;

    public EditApplicationFormAsProgrammeAdminController() {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public EditApplicationFormAsProgrammeAdminController(final UserService userService, final ApplicationsService applicationService,
            final DocumentPropertyEditor documentPropertyEditor, final QualificationService qualificationService, final RefereeService refereeService,
            final RefereesAdminEditDTOValidator refereesAdminEditDTOValidator, final EncryptionHelper encryptionHelper) {
        this.userService = userService;
        this.applicationService = applicationService;
        this.documentPropertyEditor = documentPropertyEditor;
        this.qualificationService = qualificationService;
        this.refereeService = refereeService;
        this.refereesAdminEditDTOValidator = refereesAdminEditDTOValidator;
        this.encryptionHelper = encryptionHelper;
    }

    @InitBinder(value = "refereesAdminEditDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereesAdminEditDTOValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String view(@ModelAttribute ApplicationForm applicationForm) {
        if (!applicationForm.isUserAllowedToSeeAndEditAsAdministrator(getCurrentUser())) {
            throw new ResourceNotFoundException();
        }
        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_VIEW_NAME;
    }

    @RequestMapping(value = "/postReference", method = RequestMethod.POST)
    public String submitReference(@Valid @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO, BindingResult result,
            @ModelAttribute ApplicationForm applicationForm, Model model) {

        if (!applicationForm.isUserAllowedToSeeAndEditAsAdministrator(getCurrentUser())) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("editedRefereeId", refereesAdminEditDTO.getEditedRefereeId());

        Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
        Referee referee = refereeService.getRefereeById(refereeId);

        if (referee.getReference() == null) {
            // reference not uploaded yet, try to do it now
            if (result.hasErrors()) {
                return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
            }

            refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
            refereeService.refresh(referee);
        }

        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
    }

    @RequestMapping(value = "/postRefereesData", method = RequestMethod.POST)
    public String submitRefereesData(@RequestParam String sendToPorticoData, @ModelAttribute ApplicationForm applicationForm,
            @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO, BindingResult result, Model model) {

        if (StringUtils.isBlank(sendToPorticoData) || !applicationForm.isUserAllowedToSeeAndEditAsAdministrator(getCurrentUser())) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("editedRefereeId", refereesAdminEditDTO.getEditedRefereeId());        // save "send to UCL" data first        refereeService.selectForSendingToPortico(applicationForm, sendToPorticoData);
        if (refereesAdminEditDTO.hasUserStartedTyping()) {
            refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, result);
            
            if (result.hasErrors()) {
                return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
            }

            Integer refereeId = encryptionHelper.decryptToInteger(refereesAdminEditDTO.getEditedRefereeId());
            Referee referee = refereeService.getRefereeById(refereeId);

            refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
            refereeService.refresh(referee);
        }

        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
    }

    @RequestMapping(value = "/postQualificationsData", method = RequestMethod.POST)
    @ResponseBody
    public String submitQualificationsData(@RequestParam final String sendToPorticoData, @ModelAttribute ApplicationForm applicationForm) {
        
        if (StringUtils.isBlank(sendToPorticoData) || !applicationForm.isUserAllowedToSeeAndEditAsAdministrator(getCurrentUser())) {
            throw new ResourceNotFoundException();
        }
        qualificationService.selectForSendingToPortico(applicationForm, sendToPorticoData);
        return "OK";
    }

    @ModelAttribute(value = "refereesAdminEditDTO")
    public RefereesAdminEditDTO getRefereesAdminEditDTO() {
        return new RefereesAdminEditDTO();
    }

    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null || !getCurrentUser().canSee(applicationForm)) {
            throw new ResourceNotFoundException();
        }
        return applicationForm;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }
}
