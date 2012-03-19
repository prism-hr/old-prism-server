package com.zuehlke.pgadmissions.controllers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ApplicantRecordDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

@Controller
@RequestMapping(value = { "/register" })
public class RegisterController {

	private static final String REGISTER_APPLICANT_VIEW_NAME = "public/register/register_applicant";
	private static final String REGISTER_INFO_VIEW_NAME = "public/register/register_info";
	private final UserService userService;
	private final ApplicantRecordValidator validator;

	RegisterController() {
		this(null, null);
	}

	@Autowired
	public RegisterController(ApplicantRecordValidator validator,
			UserService userService) {
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
		model.setResult(errors);
		if (!errors.hasErrors()) {
			if (record.getPassword() != null)
				record.setPassword(createHash(record.getPassword()));
			SecureRandom random = new SecureRandom();
			String activationCode = new BigInteger(80, random).toString(32);
			RegisteredUser user = new RegisteredUser();
			user.setUsername(record.getEmail());
			user.setFirstName(record.getFirstname());
			user.setLastName(record.getLastname());
			user.setEmail(record.getEmail());
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setPassword(record.getPassword());
			user.setEnabled(false);
			user.setActivationCode(activationCode);
			user.setCredentialsNonExpired(true);
			user.getRoles().add(userService.getRoleById(2));
			userService.save(user);
			model.setUrl("http://localhost:8080/pgadmissions/register/activateAccount?activationCode="+user.getActivationCode()+"&username="+user.getUsername());
			model.setMessage("  You have been successfully registered. To activate your account please check your emails and click on the activation link :<a href=\""+model.getUrl()+"\"> ${"+model.getUrl()+"}</a>");
			model.setRecord(new ApplicantRecordDTO());
			return new ModelAndView(REGISTER_INFO_VIEW_NAME, "model", model);
		}
		model.setRecord(record);
		return new ModelAndView(REGISTER_APPLICANT_VIEW_NAME, "model", model);
	}


	@RequestMapping(value = "/activateAccount", method = RequestMethod.GET)
	public ModelAndView activateAccountSubmit(@ModelAttribute RegisteredUser regUser,
			@RequestParam String activationCode) {
		RegisteredUser user = userService.getUserByUsername(regUser.getUsername());
		RegisterPageModel model = new RegisterPageModel();
		if (activationCode.equals(user.getActivationCode())) {
			user.setEnabled(true);
			userService.save(user);
			model.setUser(user);
			return new ModelAndView("public/login/login_page", "model", model);
		}
		model.setUser(user);
		model.setMessage("The activation has failed.");
		return new ModelAndView(REGISTER_INFO_VIEW_NAME, "model", model);
	}

	@ModelAttribute("record")
	public ApplicantRecordDTO getApplicantRecord(Integer id) {
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
