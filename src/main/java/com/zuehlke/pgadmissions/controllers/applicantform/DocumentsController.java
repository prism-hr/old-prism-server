package com.zuehlke.pgadmissions.controllers.applicantform;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormDocument;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormDocumentService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.validators.ApplicationFormDocumentValidator;

@Controller
@RequestMapping("/update")
public class DocumentsController {

    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Autowired
    private ApplicationFormDocumentService applicationFormDocumentService;

    @Autowired
    private ApplicationFormDocumentValidator documentSectionValidator;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public String getDocumentsView(@ModelAttribute ApplicationForm applicationForm, ModelMap modelMap) {
        return returnView(modelMap, applicationFormDocumentService.getOrCreate(applicationForm));
    }

    @RequestMapping(value = "/editDocuments", method = RequestMethod.POST)
    public String editDocuments(@Valid ApplicationFormDocument applicationFormDocument, BindingResult result, @ModelAttribute ApplicationForm applicationForm,
            ModelMap modelMap) {
        if (result.hasErrors()) {
            return returnView(modelMap, applicationFormDocument);
        }
        applicationFormService.saveOrUpdateApplicationFormSection(applicationForm);
        return "redirect:/update/getDocuments?applicationId=" + applicationForm.getApplicationNumber();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplicationForm(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    @InitBinder("applicationFormDocument")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(documentSectionValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
    }

    private String returnView(ModelMap modelMap, ApplicationFormDocument applicationFormDocument) {
        modelMap.put("applicationFormDocument", applicationFormDocument);
        return TemplateLocation.APPLICATION_APPLICANT_DOCUMENT;
    }

}
