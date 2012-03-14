package com.zuehlke.pgadmissions.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicantRecordService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {

	private static final String REGISTER_APPLICANT_VIEW_NAME = "public/register/register_applicant";
	private final ApplicantRecordService applicantRecordService;
	private final ApplicantRecordValidator validator;

	RegisterController( ) {
		this(null, null);
	}

	@Autowired
	public RegisterController(ApplicantRecordService applicantRecordService, ApplicantRecordValidator validator) {
		this.applicantRecordService = applicantRecordService;
		this.validator = validator;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getRegisterPage() throws NoSuchAlgorithmException {
		RegisterPageModel model = new RegisterPageModel();
		model.setResult(new DirectFieldBindingResult(model, "model"));
		model.setRecord(new ApplicantRecord());
		return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME,"model", model);
	}
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView getRegisterSubmitPage(@ModelAttribute ApplicantRecord record, BindingResult errors) throws NoSuchAlgorithmException {
		validator.validate(record, errors);
		
		if (!errors.hasErrors()) {
			if(record.getId()==null)
				record.setPassword(createHash(record.getPassword()));
			applicantRecordService.save(record);
		}
		RegisterPageModel model = new RegisterPageModel();
		model.setRecord(record);
		model.setResult(errors);
		
		return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
	}

	public String createHash(String password) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes());
		byte byteData[] = md5.digest();
		// convert bytes to hex chars
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
