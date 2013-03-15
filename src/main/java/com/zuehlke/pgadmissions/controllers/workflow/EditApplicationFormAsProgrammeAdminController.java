package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
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

    private final RefereeService refereeService;

    private final RefereesAdminEditDTOValidator refereesAdminEditDTOValidator;

    private final SendToPorticoDataDTOEditor sendToPorticoDataDTOEditor;

    private final EncryptionHelper encryptionHelper;

    private final CountryService countryService;

    private final CountryPropertyEditor countryPropertyEditor;

    private final MessageSource messageSource;

    public EditApplicationFormAsProgrammeAdminController() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public EditApplicationFormAsProgrammeAdminController(final UserService userService, final ApplicationsService applicationService,
            final DocumentPropertyEditor documentPropertyEditor, final RefereeService refereeService,
            final RefereesAdminEditDTOValidator refereesAdminEditDTOValidator, final SendToPorticoDataDTOEditor sendToPorticoDataDTOEditor,
            final EncryptionHelper encryptionHelper, final CountryService countryService, final CountryPropertyEditor countryPropertyEditor,
            final MessageSource messageSource) {
        this.userService = userService;
        this.applicationService = applicationService;
        this.documentPropertyEditor = documentPropertyEditor;
        this.refereeService = refereeService;
        this.refereesAdminEditDTOValidator = refereesAdminEditDTOValidator;
        this.encryptionHelper = encryptionHelper;
        this.sendToPorticoDataDTOEditor = sendToPorticoDataDTOEditor;
        this.countryService = countryService;
        this.countryPropertyEditor = countryPropertyEditor;
        this.messageSource = messageSource;
    }

    @InitBinder(value = "sendToPorticoData")
    public void registerSendToPorticoData(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, sendToPorticoDataDTOEditor);
    }

    @InitBinder(value = "refereesAdminEditDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereesAdminEditDTOValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(Country.class, countryPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String view(@ModelAttribute ApplicationForm applicationForm) {
        if (!applicationForm.isUserAllowedToSeeAndEditAsAdministrator(getCurrentUser())) {
            throw new InsufficientApplicationFormPrivilegesException(applicationForm.getApplicationNumber());
        }
        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_VIEW_NAME;
    }

    @RequestMapping(value = "/editReferenceData", method = RequestMethod.POST)
    @ResponseBody
    public String updateReference(@ModelAttribute ApplicationForm applicationForm, @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO,
            BindingResult result, Model model) {

        if (!applicationForm.isUserAllowedToSeeAndEditAsAdministrator(getCurrentUser())) {
            throw new InsufficientApplicationFormPrivilegesException(applicationForm.getApplicationNumber());
        }

        model.addAttribute("editedRefereeId", refereesAdminEditDTO.getEditedRefereeId());
        refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, result);

        Map<String, String> map = new HashMap<String, String>();
        if (!result.hasErrors()) {
            refereeService.editReferenceComment(refereesAdminEditDTO);
            map.put("success", "true");
        } else {
            map.put("success", "false");
            for (FieldError error : result.getFieldErrors()) {
                String message;
                if (!StringUtils.isBlank(error.getCode())) {
                    message = messageSource.getMessage(error.getCode(), null, Locale.getDefault());
                } else {
                    message = error.getDefaultMessage();
                }
                map.put(error.getField(), message);
            }
        }

        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @RequestMapping(value = "/postRefereesData", method = RequestMethod.POST)
    public String submitRefereesData(@ModelAttribute ApplicationForm applicationForm, @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO,
            BindingResult referenceResult, @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData,
            @RequestParam(required = false) Boolean forceSavingReference, Model model) {

        if (!applicationForm.isUserAllowedToSeeAndEditAsAdministrator(getCurrentUser())) {
            throw new InsufficientApplicationFormPrivilegesException(applicationForm.getApplicationNumber());
        }

        String editedRefereeId = refereesAdminEditDTO.getEditedRefereeId();
        model.addAttribute("editedRefereeId", editedRefereeId);

        // save "send to UCL" data first
        if (sendToPorticoData.getRefereesSendToPortico() != null) {
            refereeService.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        }

        if (!"newReferee".equals(editedRefereeId)) {
            Integer decryptedId = encryptionHelper.decryptToInteger(editedRefereeId);
            Referee referee = refereeService.getRefereeById(decryptedId);
            if (referee.getReference() != null) {
                return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
            }
        }

        if (BooleanUtils.isTrue(forceSavingReference) || refereesAdminEditDTO.hasUserStartedTyping()) {
            refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, referenceResult);

            if (referenceResult.hasErrors()) {
                return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
            }

            ReferenceComment newComment = refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
            Referee referee = newComment.getReferee();
            applicationService.refresh(applicationForm);
            refereeService.refresh(referee);

            String newRefereeId = encryptionHelper.encrypt(referee.getId());
            model.addAttribute("editedRefereeId", newRefereeId);
        }

        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
    }

    @ModelAttribute(value = "refereesAdminEditDTO")
    public RefereesAdminEditDTO getRefereesAdminEditDTO() {
        return new RefereesAdminEditDTO();
    }

    @ModelAttribute("countries")
    public List<Country> getAllCountries() {
        return countryService.getAllCountries();
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
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        if (!getCurrentUser().canSee(applicationForm)) {
            throw new InsufficientApplicationFormPrivilegesException(applicationId);
        }

        return applicationForm;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return getCurrentUser();
    }
}
