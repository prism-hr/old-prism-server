package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;

@Component
public class StateChangeValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(StateChangeDTO.class);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
    	StateChangeDTO stateChangeDTO = (StateChangeDTO) target;
    	
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
    	
        if (stateChangeDTO.getApplicationForm().getStatus() == ApplicationFormStatus.VALIDATION) {
            if (stateChangeDTO.getQualifiedForPhd() == null) {
                errors.rejectValue("qualifiedForPhd", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
            if (stateChangeDTO.getEnglishCompentencyOk() == null) {
                errors.rejectValue("englishCompentencyOk", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
            if (stateChangeDTO.getHomeOrOverseas() == null) {
                errors.rejectValue("homeOrOverseas", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
        }
        
        ApplicationFormStatus nextStatus = stateChangeDTO.getNextStatus();
        boolean stateChangeRequiresFastTrack = !(ApplicationFormStatus.APPROVED.equals(nextStatus) || ApplicationFormStatus.REJECTED.equals(nextStatus)) &&
        		BooleanUtils.isTrue(stateChangeDTO.hasGlobalAdministrationRights());
        boolean fastrackValueMissing = stateChangeDTO.getFastTrackApplication() == null && stateChangeDTO.getApplicationForm().getBatchDeadline() != null;        
        
        if(stateChangeRequiresFastTrack && fastrackValueMissing){
            errors.rejectValue("fastTrackApplication", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
        
        if(BooleanUtils.isTrue(stateChangeDTO.getDelegate())) {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "delegateFirstName", EMPTY_FIELD_ERROR_MESSAGE);
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "delegateLastName", EMPTY_FIELD_ERROR_MESSAGE);
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "delegateEmail", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (stateChangeDTO.getNextStatus() == null) {
            errors.rejectValue("nextStatus", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        if (BooleanUtils.isNotTrue(stateChangeDTO.getConfirmNextStage())) {
            errors.rejectValue("confirmNextStage", MANDATORY_CHECKBOX);
        }
        
        if (stateChangeDTO.getApplicationForm().getStatus().equals(ApplicationFormStatus.VALIDATION)
                && stateChangeDTO.getCustomQuestionCoverage().contains(ScoringStage.REFERENCE)
                && stateChangeDTO.getUseCustomReferenceQuestions() == null) {
            errors.rejectValue("useCustomReferenceQuestions", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
        
        if ((nextStatus == ApplicationFormStatus.REVIEW
                && stateChangeDTO.getCustomQuestionCoverage().contains(ScoringStage.REVIEW))
                || (nextStatus == ApplicationFormStatus.INTERVIEW
                        && stateChangeDTO.getCustomQuestionCoverage().contains(ScoringStage.INTERVIEW))
                && stateChangeDTO.getUseCustomQuestions() == null) {
            errors.rejectValue("useCustomQuestions", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
        
    }

}