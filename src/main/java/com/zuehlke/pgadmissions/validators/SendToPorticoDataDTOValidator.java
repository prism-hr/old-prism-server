package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;

@Component
public class SendToPorticoDataDTOValidator extends AbstractValidator {

	private final ApplicationsService applicationFormService;
	
    private final QualificationService qualificationService;

    private final RefereeService refereeService;

    @Autowired
    public SendToPorticoDataDTOValidator(ApplicationsService applicationFormService, QualificationService qualificationService, 
    		RefereeService refereeService) {
    	this.applicationFormService = applicationFormService;
        this.qualificationService = qualificationService;
        this.refereeService = refereeService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SendToPorticoDataDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        SendToPorticoDataDTO dto = (SendToPorticoDataDTO) target;

        List<Integer> qualifications = dto.getQualificationsSendToPortico();
        
        List<Integer> referees = dto.getRefereesSendToPortico();
        
        String explanation = dto.getEmptyQualificationsExplanation();

        if (qualifications != null) {
        	
        	ApplicationForm applicationForm = applicationFormService.getApplicationByApplicationNumber(dto.getApplicationNumber()); 
        	
            for (int i = 0; i < qualifications.size(); i++) {
            	if (qualificationService.getQualificationById(qualifications.get(i)).getProofOfAward() == null) {
            		qualifications.remove(i);
            	}
            }
            
            // It was possible to select a qualification and neither a selection nor a justification for not making one was entered
            // Form level error
            if (applicationForm.hasQualificationsWithTranscripts() &&
            	qualifications.isEmpty() && 
                StringUtils.isBlank(explanation)) {
            	errors.rejectValue("qualificationsSendToPortico", "portico.submit.no.qualification.or.explanation");
            }
            
            // It was not possible to select a qualification and a justification for not selecting one was not made
            // Form and field level errors
            if (!applicationForm.hasQualificationsWithTranscripts() &&
            	StringUtils.isBlank(explanation)) {
            	errors.rejectValue("qualificationsSendToPortico", "portico.submit.no.qualification.or.explanation");
            	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emptyQualificationsExplanation", EMPTY_FIELD_ERROR_MESSAGE);
            }
  
            // In either of the above scenarios an out of range text explanation was provided
            // Field level error
            if (!StringUtils.isBlank(explanation) &&
            	explanation.trim().length() > 500) {
            	errors.rejectValue("qualificationsSendToPortico", "portico.submit.no.qualification.or.explanation");
            }
            
        }

        if (referees != null) {
        	
            for (int i = 0; i < referees.size(); i++) {
            	if (!refereeService.getById(referees.get(i)).hasProvidedReference()) {
            		referees.remove(i);
            	}
            }

            if (referees.size() < 2) {
                errors.rejectValue("refereesSendToPortico", "portico.submit.referees.invalid");
            }
        }
    }
}