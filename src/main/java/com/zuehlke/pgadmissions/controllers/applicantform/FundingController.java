package com.zuehlke.pgadmissions.controllers.applicantform;

import java.beans.PropertyEditor;
import java.util.Date;

import javax.validation.Valid;

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

import com.google.common.base.Strings;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@Controller
@RequestMapping("/update")
public class FundingController {

    private static final String STUDENT_FUNDING_DETAILS_VIEW = "/private/pgStudents/form/components/funding_details";
    
    @Autowired
    private ApplicationsService applicationService;
    
    @Autowired
    private PropertyEditor datePropertyEditor;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Autowired
    private FundingValidator fundingValidator;
    
    @Autowired
    private FundingService fundingService;
    
    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EncryptionHelper encryptionHelper;
    
    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @InitBinder(value = "funding")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(fundingValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
    }

    @RequestMapping(value = "/editFunding", method = RequestMethod.POST)
    public String editFunding(@RequestParam(value = "fundingId", required = false) String encryptedFundingId, @Valid Funding funding, BindingResult result,
            ModelMap modelMap) {
        if (result.hasErrors()) {
            return STUDENT_FUNDING_DETAILS_VIEW;
        }

        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");

        fundingService.save(applicationForm.getId(), Strings.isNullOrEmpty(encryptedFundingId) ? null : encryptionHelper.decryptToInteger(encryptedFundingId),
                funding);
        applicationService.save(applicationForm);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, userService.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
        return "redirect:/update/getFunding?applicationId=" + funding.getApplication().getApplicationNumber();
    }

    @RequestMapping(value = "/getFunding", method = RequestMethod.GET)
    public String getFundingView(@RequestParam(value = "fundingId", required = false) String encryptedFundingId, ModelMap modelMap) {
        Funding funding;
        if (encryptedFundingId == null) {
            funding = new Funding();
        } else {
            funding = fundingService.getFundingById(encryptionHelper.decryptToInteger(encryptedFundingId));
        }
        modelMap.put("funding", funding);
        return STUDENT_FUNDING_DETAILS_VIEW;
    }

    @ModelAttribute("fundingTypes")
    public FundingType[] getFundingTypes() {
        return FundingType.values();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException();
        }
        if (application.isDecided()) {
            throw new CannotUpdateApplicationException(applicationId);
        }
        return application;
    }

}