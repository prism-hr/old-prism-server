package com.zuehlke.pgadmissions.controllers;

import java.net.Authenticator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ApplicantRecordDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicantRecordService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {

	private static final String REGISTER_APPLICANT_VIEW_NAME = "public/register/register_applicant";
	private final UserService userService;
	private final ApplicantRecordService applicantRecordService;
	private final ApplicantRecordValidator validator;

	RegisterController() {
		this(null, null, null);
	}

	@Autowired
	public RegisterController(ApplicantRecordService applicantRecordService, ApplicantRecordValidator validator,
			UserService userService) {
		this.applicantRecordService = applicantRecordService;
		this.validator = validator;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getRegisterPage() throws NoSuchAlgorithmException {
		RegisterPageModel model = new RegisterPageModel();
		model.setRecord(new ApplicantRecordDTO());
		return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelAndView getRegisterSubmitPage(@ModelAttribute("record") ApplicantRecordDTO record, BindingResult errors)
			throws NoSuchAlgorithmException {
		validator.validate(record, errors);

		RegisterPageModel model = new RegisterPageModel();
		ModelMap modelMap = new ModelMap();
		if (errors.hasFieldErrors("firstname"))
			modelMap.put("firstnameErrorCode", errors.getFieldError("firstname").getCode());
		if (errors.hasFieldErrors("lastname"))
			modelMap.put("lastnameErrorCode", errors.getFieldError("lastname").getCode());
		if (errors.hasFieldErrors("email"))
			modelMap.put("emailfirstnameErrorCode", errors.getFieldError("email").getCode());
		if (errors.hasFieldErrors("password"))
			modelMap.put("passwordErrorCode", errors.getFieldError("password").getCode());
		if (errors.hasFieldErrors("confirmPassword"))
			modelMap.put("confirmPasswordErrorCode", errors.getFieldError("confirmPassword").getCode());
		if (!errors.hasErrors()) {
			if (record.getPassword() != null)
				record.setPassword(createHash(record.getPassword()));
			RegisteredUser user = new RegisteredUser();
			user.setUsername(record.getEmail());
			user.setFirstName(record.getFirstname());
			user.setLastName(record.getLastname());
			user.setEmail(record.getEmail());
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setPassword(record.getPassword());
			user.setEnabled(true);
			user.setCredentialsNonExpired(true);
			user.getRoles().add(userService.getRoleById(2));
			userService.save(user);
			System.out.println("user id" + user.getId());

			model.setMessage("You have been successfully registered. Please check your emails to activate your account");
			model.setRecord(new ApplicantRecordDTO());
			return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
		}
		modelMap.put("record", record);
		return new ModelAndView("redirect:/register", modelMap);
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
