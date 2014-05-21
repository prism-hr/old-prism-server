package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class RefereesAdminEditDTOValidator extends AbstractValidator {

	private final UserService userService;

	@Autowired
	private ScoresValidator scoresValidator;

	@Autowired
	public RefereesAdminEditDTOValidator(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RefereesAdminEditDTO.class.equals(clazz);
	}

	@Override
	protected void addExtraValidation(Object target, Errors errors) {
		RefereesAdminEditDTO dto = (RefereesAdminEditDTO) target;

		// validate reference
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmpty(errors, "suitableForUCL", EMPTY_DROPDOWN_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmpty(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicantRating", EMPTY_FIELD_ERROR_MESSAGE);

		// validate referee
		if (BooleanUtils.isTrue(dto.getContainsRefereeData())) {
			if (userService.getCurrentUser().getEmail().equals(dto.getEmail())) {
				errors.rejectValue("email", "text.email.notyourself");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", EMPTY_FIELD_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", EMPTY_FIELD_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", EMPTY_FIELD_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", EMPTY_FIELD_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", EMPTY_FIELD_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", EMPTY_FIELD_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", EMPTY_FIELD_ERROR_MESSAGE);

			if (dto.getAddressLocation() != null && StringUtils.isBlank(dto.getAddressLocation().getAddressLine1())) {
				errors.rejectValue("addressLocation.address1", EMPTY_FIELD_ERROR_MESSAGE);
			}
			if (dto.getAddressLocation() != null && StringUtils.isBlank(dto.getAddressLocation().getAddressTown())) {
				errors.rejectValue("addressLocation.address3", EMPTY_FIELD_ERROR_MESSAGE);
			}
			if (dto.getAddressLocation() != null && dto.getAddressLocation().getDomicile() == null) {
				errors.rejectValue("addressLocation.domicile", EMPTY_FIELD_ERROR_MESSAGE);
			}
		}

		List<Score> scores = dto.getScores();
		if (scores != null) {
			for (int i = 0; i < scores.size(); i++) {
				try {
					errors.pushNestedPath("scores[" + i + "]");
					ValidationUtils.invokeValidator(scoresValidator, scores.get(i), errors);
				} finally {
					errors.popNestedPath();
				}
			}
		}
	}
}
