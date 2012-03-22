package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
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

		String originalFilename = multipartFile.getOriginalFilename();
		if (StringUtils.isBlank(originalFilename)) {
			if (StringUtils.isBlank(referee.getComment())) {
				referee.setComment(null);
				ApplicationPageModel model = new ApplicationPageModel();
				model.setGlobalErrorCodes(Arrays.asList("reference.missing"));
				model.setReferee(referee);
				return new ModelAndView(ADD_REFERENCES_VIEW_NAME, "model", model);
			}
			refereeService.save(referee);
			return new ModelAndView("redirect:/addReferences/referenceuploaded");
		}

		Document document = newDocument();
		document.setFileName(originalFilename);
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setReferee(referee);
		referee.setDocument(document);
		BindingResult errors = newErrors(document);
		documentValidator.validate(document, errors);

		if (errors.hasFieldErrors("fileName")) {
			ApplicationPageModel model = new ApplicationPageModel();
			model.setUploadErrorCode(errors.getFieldError("fileName").getCode());
			model.setReferee(referee);
			return new ModelAndView(ADD_REFERENCES_VIEW_NAME, "model", model);
		}
		referee.setDocument(document);
		refereeService.save(referee);
		return new ModelAndView("redirect:/addReferences/referenceuploaded");

	}

	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, "document");
	}

	Document newDocument() {
		return new Document();
	}

}
