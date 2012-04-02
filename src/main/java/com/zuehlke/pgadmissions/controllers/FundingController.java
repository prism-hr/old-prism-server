package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@Controller
@RequestMapping("/updateFunding")
public class FundingController {
	
	private final ApplicationsService applicationService;
	
	FundingController() {
		this(null);
	}

	@Autowired
	public FundingController(ApplicationsService applicationService) {
		this.applicationService = applicationService;
	}


	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer appId, @RequestParam(required=false) String add,  BindingResult result, ModelMap modelMap) {
		ApplicationForm application = applicationService.getApplicationById(appId);

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
			if (fund.getFundingId() == null) {
				application.getFundings().add(funding);
			}
			applicationService.save(application);
			model.setFunding(new Funding());
		} else {
			model.setFunding(fund);
		}
		modelMap.put("model", model);
		if(StringUtils.isNotBlank(add)){
			modelMap.put("add", "add");
		}
		return new ModelAndView("private/pgStudents/form/components/funding_details", modelMap);
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
}
