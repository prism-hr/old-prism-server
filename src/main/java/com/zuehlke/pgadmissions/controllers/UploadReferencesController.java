package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

@Controller
@RequestMapping("/addReferences")
public class UploadReferencesController {

	private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
	
	private ApplicationsService applicationService;
	private DocumentValidator documentValidator;

	UploadReferencesController() {
		this(null, null);
	}

	@Autowired
	public UploadReferencesController(ApplicationsService applicationService, DocumentValidator documentValidator) {
		this.applicationService = applicationService;
		this.documentValidator = documentValidator;
	}
	

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getReferencesPage(@RequestParam Integer refereeId, @RequestParam String activationCode, @RequestParam Integer applicationId) {
		System.out.println(applicationService);
		Referee referee = applicationService.getRefereeById(refereeId);
		System.out.println(referee.getId());
		ApplicationForm applicationForm = applicationService.getApplicationById(applicationId);
		ApplicationPageModel model = new ApplicationPageModel();
		if(applicationForm==null || referee==null || !referee.getActivationCode().equals(activationCode)){
			model.setMessage("The link you provided is incorrect please try again");
		}
		else{
			System.out.println(applicationForm.getId());
			model.setApplicationForm(applicationForm);
			model.setReferee(referee);
		}
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME, "model", model);
	}
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView submitReference(@ModelAttribute("referee") Referee referee, @RequestParam("file") MultipartFile multipartFile) throws IOException {
		Referee ref = applicationService.getRefereeById(referee.getId());
		if (ref == null ) {
			throw new ResourceNotFoundException();
		}
		Document document = newDocument();
		document.setFileName(multipartFile.getOriginalFilename());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setReferee(ref);
		BindingResult errors = newErrors(document);
		documentValidator.validate(document, errors);
		ModelMap modelMap = new ModelMap();		
		if(errors.hasFieldErrors("fileName")){
			modelMap.put("uploadErrorCode", errors.getFieldError("fileName").getCode());
		}else{
			ref.setComment(referee.getComment());
			document.getReferee().setDocument(document);
			applicationService.saveDocument(document);
			applicationService.saveReferee(ref);
			return new ModelAndView("private/referees/upload_success", modelMap);
		}
		modelMap.put("id", ref.getId());
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME, modelMap);
	}
	
	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, "document");
	}
	
	Document newDocument() {
		return new Document();
	}

}
