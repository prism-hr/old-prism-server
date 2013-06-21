package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Component
public class ApprovalRoundValidator extends AbstractValidator {

    private static final int MAX_ABSTRACT_WORD_COUNT = 200;

    @Override
    public boolean supports(Class<?> clazz) {
        return ApprovalRound.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        ApprovalRound approvalRound = (ApprovalRound) target;

        // supervisors validation
        if (approvalRound.getSupervisors().size() != 2) {
            errors.rejectValue("supervisors", "approvalround.supervisors.incomplete");
        } else {
            int primarySupervisors = 0;
            for (Supervisor supervisor : approvalRound.getSupervisors()) {
                if (BooleanUtils.isTrue(supervisor.getIsPrimary())) {
                    primarySupervisors++;
                }
            }

            if (primarySupervisors != 1) {
                errors.rejectValue("supervisors", "approvalround.supervisors.noprimary");
            }
        }

        
        // project description validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescriptionAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isTrue(approvalRound.getProjectDescriptionAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", EMPTY_FIELD_ERROR_MESSAGE);
        }

        String projectAbstract = approvalRound.getProjectAbstract();
        if (projectAbstract != null) {
            int wordCount = countWords(projectAbstract);
            if (wordCount > MAX_ABSTRACT_WORD_COUNT) {
                errors.rejectValue("projectAbstract", "text.field.maxwords", new Object[] { MAX_ABSTRACT_WORD_COUNT }, null);
            }
        }

        // recommended offer validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        Date startDate = approvalRound.getRecommendedStartDate();
        Date today = new Date();
        if (startDate != null && !startDate.after(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isTrue(approvalRound.getRecommendedConditionsAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", EMPTY_FIELD_ERROR_MESSAGE);
        }

    }

    private int countWords(String text) {
        return StringUtils.split(text, "\t\n\r ").length;
    }

}
