package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;

@Component
public class SendToPorticoDataDTOValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return SendToPorticoDataDTO.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {

	    SendToPorticoDataDTO dto = (SendToPorticoDataDTO) target;
    
	    List<Integer> qualifications = dto.getQualificationsSendToPortico();
	    List<Integer> references = dto.getRefereesSendToPortico();
	    String explanation = dto.getEmptyQualificationsExplanation();
	    
        if(qualifications != null){
            if(qualifications.isEmpty() && StringUtils.isBlank(explanation)) {
	            errors.rejectValue("emptyQualificationsExplanation", "portico.submit.explanation.empty");
	        }
	    }
        
        if(references != null){
            if(references.size() != 2){
                errors.rejectValue("refereesSendToPortico", "portico.submit.referees.invalid");
            }
        }
        
	}

}
