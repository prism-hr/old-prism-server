package com.zuehlke.pgadmissions.controllers.applicantform;

import javax.validation.Valid;

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

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.BooleanPropertyEditor;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.AdditionalInfoService;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AdditionalInformationValidator;

@Controller
@RequestMapping("/update")
public class AdditionalInformationController {

    private static final String STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW = "/private/pgStudents/form/components/additional_information";
    private final AdditionalInfoService additionalService;
    private final ApplicationsService applicationService;
    private final AdditionalInformationValidator additionalInformationValidator;
    private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
    private final BooleanPropertyEditor booleanPropertyEditor;
    private final UserService userService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    private final ContentAccessProvider contentAccessProvider;

    AdditionalInformationController() {
        this(null, null, null, null, null, null, null, null);
    }

    @Autowired
    public AdditionalInformationController(ApplicationsService applicationService,
            UserService userService,
            ApplicationFormPropertyEditor applicationFormPropertyEditor,
            BooleanPropertyEditor booleanEditor,
            AdditionalInfoService addInfoServiceMock, AdditionalInformationValidator infoValidator,
            ApplicationFormUserRoleService applicationFormUserRoleService,
            ContentAccessProvider contentAccessProvider) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.applicationFormPropertyEditor = applicationFormPropertyEditor;
        this.booleanPropertyEditor = booleanEditor;
        this.additionalService = addInfoServiceMock;
        this.additionalInformationValidator = infoValidator;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.contentAccessProvider = contentAccessProvider;
    }

    @RequestMapping(value = "/editAdditionalInformation", method = RequestMethod.POST)
    public String editAdditionalInformation(@Valid AdditionalInformation info, BindingResult result,
    		@ModelAttribute ApplicationForm applicationForm) {
        if (result.hasErrors()) {
            return STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW;
        }
        
        additionalService.save(info);
        applicationFormUserRoleService.applicationEdited(applicationForm, getCurrentUser());

        return "redirect:/update/getAdditionalInformation?applicationId=" + info.getApplication().getApplicationNumber();

    }

    @RequestMapping(value = "/getAdditionalInformation", method = RequestMethod.GET)
    public String getAdditionalInformationView(@ModelAttribute ApplicationForm applicationForm) {
        return STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW;
    }

    @ModelAttribute("additionalInformation")
    public AdditionalInformation getAdditionalInformation(@RequestParam String applicationId) {
        ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
        return application.getAdditionalInformation();
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("errorCode")
    public String getErrorCode(String errorCode) {
        return errorCode;
    }

    @InitBinder(value = "additionalInformation")
    public void registerValidatorsEditors(WebDataBinder binder) {
        binder.setValidator(additionalInformationValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(Boolean.class, booleanPropertyEditor);
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
        contentAccessProvider.validateCanEditAsApplicant(application, getCurrentUser());
        return application;
    }
}