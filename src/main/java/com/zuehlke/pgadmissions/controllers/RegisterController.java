package com.zuehlke.pgadmissions.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;
import com.zuehlke.pgadmissions.dto.ApplicantRecordDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicantRecordService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {

	private static final String REGISTER_APPLICANT_VIEW_NAME = "public/register/register_applicant";
	private static ServiceRegistry serviceRegistry;
	private static SessionFactory sessionFactory;
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
		model.setRecord(new ApplicantRecordDTO());
		return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME,"model", model);
	}
	
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView getRegisterSubmitPage(@ModelAttribute("record") ApplicantRecordDTO record, BindingResult errors) throws NoSuchAlgorithmException {
		System.out.println(record.getFirstname());
		System.out.println(record.getLastname());
		System.out.println(record.getEmail());
		System.out.println(record.getPassword());
		System.out.println(record.getConfirmPassword());
		validator.validate(record, errors);
		RegisterPageModel model = new RegisterPageModel();
		model.setRecord(record);
		model.setResult(errors);
		model.setMessage("You have been successfully registered. Please check your emails to activate your account");
		if (!errors.hasErrors()) {
			if(record.getPassword()!=null)
				record.setPassword(createHash(record.getPassword()));
				ApplicantRecord applicantRecord = new ApplicantRecord();
				applicantRecord.setFirstname(record.getFirstname());
				applicantRecord.setLastname(record.getLastname());
				applicantRecord.setPassword(record.getPassword());
				applicantRecord.setEmail(record.getEmail());
				applicantRecordService.save(applicantRecord);
				return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
		}
		return new ModelAndView("redirect:/register", "model", model);
	}
	
	@ModelAttribute("record")
	public ApplicantRecordDTO getApplicantRecord(Integer id) {
		System.out.println("HERE");
			return new ApplicantRecordDTO();
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
