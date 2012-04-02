package com.zuehlke.pgadmissions.controllers;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@Controller
@RequestMapping("/updateFunding")
public class FundingController {

	private final ApplicationsService applicationService;
	private final PropertyEditor datePropertyEditor;
	private final DocumentValidator documentValidator;

	FundingController() {
		this(null, null, null);
	}

	@Autowired
	public FundingController(ApplicationsService applicationService, DatePropertyEditor datePropertyEditor, DocumentValidator documentValidator) {
		this.applicationService = applicationService;
		this.datePropertyEditor = datePropertyEditor;
		this.documentValidator = documentValidator;
	}


	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer appIdFunding, 
			@RequestParam(required=false) String shouldAdd, @RequestParam("fundingFile") MultipartFile fundingFile,  BindingResult result, ModelMap modelMap) throws IOException {
		ApplicationForm application = applicationService.getApplicationById(appIdFunding);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(getCurrentUser());
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setResult(result);
		model.setFundingTypes(FundingType.values());

		FundingValidator fundingValidator = new FundingValidator();
		fundingValidator.validate(fund, result);
		if (!result.hasErrors()) {
			com.zuehlke.pgadmissions.domain.Funding funding;
			if (fund.getFundingId() == null) {
				funding = new com.zuehlke.pgadmissions.domain.Funding();
			} else {
				funding = applicationService.getFundingById(fund.getFundingId());
			}
			funding.setApplication(application);
			funding.setType(fund.getFundingType());
			funding.setDescription(fund.getFundingDescription());
			funding.setValue(fund.getFundingValue());
			funding.setAwardDate(fund.getFundingAwardDate());
			Document document = new Document();
			if (fundingFile != null) {
				document.setFileName(fundingFile.getOriginalFilename());
				document.setContentType(fundingFile.getContentType());
				document.setContent(fundingFile.getBytes());
				document.setType(DocumentType.SUPPORTING_FUNDING);
				
				BindingResult errors = newErrors(document);
				documentValidator.validate(document, errors);
				if(errors.hasFieldErrors("fileName")){
					//TODO
				} else {
					funding.setDocument(document);
				}
			}
			if (fund.getFundingId() == null) {
				application.getFundings().add(funding);
			}
			applicationService.save(application);
			model.setFunding(new Funding());
		} else {
			model.setFunding(fund);
		}

		modelMap.put("model", model);
		if(StringUtils.isNotBlank(shouldAdd)){
			modelMap.put("add", "add");
		}
		modelMap.put("id", applicationForm.getId());
		return new ModelAndView("redirect:/application", modelMap);
	}

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}

	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, document.getFileName());
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, datePropertyEditor);
	}
}
