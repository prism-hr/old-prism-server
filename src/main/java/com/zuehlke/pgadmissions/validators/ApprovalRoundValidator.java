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

        if (approvalRound.getSupervisors().size() != 2) {
            errors.rejectValue("supervisors", "approvalround.supervisors.incomplete");
        }
        
        int primarySupervisors = 0;
        for(Supervisor supervisor : approvalRound.getSupervisors()){
            if(BooleanUtils.isTrue(supervisor.getIsPrimary())){
                primarySupervisors++;
            }
        }
        
        if(primarySupervisors != 1){
            errors.rejectValue("supervisors", "approvalround.supervisors.noprimary");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescriptionAvailable", "dropdown.radio.select.none");

        if (BooleanUtils.isTrue(approvalRound.getProjectDescriptionAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", "text.field.empty");
        }
        
        String projectAbstract = approvalRound.getProjectAbstract();
        if(projectAbstract != null){
            int wordCount = countWords(projectAbstract);
            if(wordCount > MAX_ABSTRACT_WORD_COUNT){
                errors.rejectValue("projectAbstract", "text.field.maxwords", new Object[]{MAX_ABSTRACT_WORD_COUNT}, null);
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", "text.field.empty");
        Date startDate = approvalRound.getRecommendedStartDate();
        Date today = new Date();
        if (startDate != null && !startDate.after(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", "dropdown.radio.select.none");

        if (BooleanUtils.isTrue(approvalRound.getRecommendedConditionsAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", "text.field.empty");
        }

    }
    
    private int countWords(String text){
        return StringUtils.split(text, "\t\n\r ").length;
    }
    
}
