package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.exceptions.RefereeAlreadyUploadedReference;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

@Controller
@RequestMapping("/addReferences")
public class UploadReferencesController {

	private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
	
	private RefereeService refereeService;
	private DocumentValidator documentValidator;

	UploadReferencesController() {
		this(null, null);
	}

	@Autowired
	public UploadReferencesController(RefereeService refereeService, DocumentValidator documentValidator) {
		this.refereeService = refereeService;
		this.documentValidator = documentValidator;
	}
	

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getReferencesPage(@ModelAttribute("referee") Referee referee, @RequestParam String activationCode) {
		Referee ref = refereeService.getRefereeByActivationCode(activationCode);
		ApplicationPageModel model = new ApplicationPageModel();
		if(ref == null || ref.getApplication()==null){
			model.setMessage("Sorry, the system was unable to find you in the system.");
		}
		else{
			ApplicationForm applicationForm = referee.getApplication();
			model.setApplicationForm(applicationForm);
			model.setReferee(ref);
		}
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME, "model", model);
	}
	
	
	@ModelAttribute("referee")
	public Referee getReferee(Integer refereeId) {
		Referee referee = refereeService.getRefereeById(refereeId);
		if (referee == null) {
			throw new ResourceNotFoundException();
		}
		return referee;
	}
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView submitReference(@ModelAttribute("referee") Referee referee, @RequestParam("file") MultipartFile multipartFile) throws IOException {
		if(referee.getDocument()!=null){
			throw new RefereeAlreadyUploadedReference();
		}
		Document document = newDocument();
		document.setFileName(multipartFile.getOriginalFilename());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setReferee(referee);
		BindingResult errors = newErrors(document);
		documentValidator.validate(document, errors);
		ModelMap modelMap = new ModelMap();		
		if(errors.hasFieldErrors("fileName")){
			modelMap.put("uploadErrorCode", errors.getFieldError("fileName").getCode());
		}else{
			referee.setDocument(document);
			refereeService.save(referee);
			return new ModelAndView("private/referees/upload_success", modelMap);
		}
		modelMap.put("id", referee.getId());
		modelMap.put("message", "There was a problem with the details you provided.");
		modelMap.put("referee", referee);
		return new ModelAndView(ADD_REFERENCES_VIEW_NAME,"model", modelMap);
	}
	
	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, "document");
	}
	
	Document newDocument() {
		return new Document();
	}

}
