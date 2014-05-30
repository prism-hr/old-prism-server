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

import com.zuehlke.pgadmissions.controllers.locations.RedirectLocation;
import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationDocumentService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.validators.ApplicationFormDocumentValidator;

@Controller
@RequestMapping("/update")
public class DocumentsController {

    @Autowired
    private ApplicationService applicationFormService;
    
    @Autowired
    private ApplicationDocumentService applicationFormDocumentService;

    @Autowired
    private ApplicationFormDocumentValidator documentSectionValidator;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public String getDocumentsView(@ModelAttribute Application applicationForm, ModelMap modelMap) {
        return returnView(modelMap, applicationFormDocumentService.getOrCreate(applicationForm));
    }

    @RequestMapping(value = "/editDocuments", method = RequestMethod.POST)
    public String editDocuments(@Valid ApplicationDocument applicationFormDocument, BindingResult result, @ModelAttribute Application applicationForm,
            ModelMap modelMap) {
        if (result.hasErrors()) {
            return returnView(modelMap, applicationFormDocument);
        }
        applicationFormDocumentService.saveOrUpdate(applicationForm.getId(), applicationFormDocument);
        return RedirectLocation.UPDATE_APPLICATION_DOCUMENT + applicationForm.getCode();
    }

    @ModelAttribute("applicationForm")
    public Application getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, PrismAction.APPLICATION_COMPLETE,
                PrismAction.APPLICATION_CORRECT);
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    @InitBinder("applicationFormDocument")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(documentSectionValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(Application.class, applicationFormPropertyEditor);
    }

    private String returnView(ModelMap modelMap, ApplicationDocument applicationFormDocument) {
        modelMap.put("applicationFormDocument", applicationFormDocument);
        return TemplateLocation.APPLICATION_APPLICANT_DOCUMENT;
    }

}
