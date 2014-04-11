package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.controllers.locations.TemplateLocation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

@Controller
@RequestMapping("/documents")
public class FileUploadController {
    
    @Autowired
	private ApplicationFormService applicationFormService;
	
    @Autowired
	private DocumentValidator documentValidator;
	
    @Autowired
	private DocumentService documentService;
	
	@RequestMapping(value = "/async", method = RequestMethod.POST)
    public String uploadFileAsynchronously(@Valid @ModelAttribute Document document, BindingResult result) {
        if(!result.hasErrors()){
            documentService.save(document);
        }
        return TemplateLocation.APPLICATION_DOCUMENT;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationFormService.getSecuredApplication(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);
    }

	@ModelAttribute
	public Document getDocument(@RequestParam(value="file", required=false) MultipartFile multipartFile, 
	        @RequestParam(value="type", required=false) DocumentType documentType) throws IOException {		 
		return documentService.create(multipartFile, documentType);
	}
	
	@InitBinder(value="document")
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(documentValidator);
	}
	
}
