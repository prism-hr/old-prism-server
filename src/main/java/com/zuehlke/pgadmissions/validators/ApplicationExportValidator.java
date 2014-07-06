package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.services.ApplicationQualificationService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.PorticoService;
import com.zuehlke.pgadmissions.services.RefereeService;

@Component
public class ApplicationExportValidator extends AbstractValidator {

    @Autowired
	private ApplicationService applicationService;
	
    @Autowired
    private ApplicationQualificationService applicationQualificationService;

    @Autowired
    private RefereeService refereeService;
    
    @Autowired
    private PorticoService porticoService;

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
        	
            for (int i = 0; i < qualifications.size(); i++) {
            	if (applicationQualificationService.getById(qualifications.get(i)).getDocument() == null) {
            		qualifications.remove(i);
            	}
            }
            
            // It was possible to select a qualification and neither a selection nor a justification for not making one was entered
            // Form level error
            boolean hasVerifiedQualifications = !applicationQualificationService.getExportApplicationQualifications().isEmpty();
            
            if (hasVerifiedQualifications && qualifications.isEmpty() && StringUtils.isBlank(explanation)) {
            	errors.rejectValue("qualificationsSendToPortico", "portico.submit.no.qualification.or.explanation");
            }
            
            // It was not possible to select a qualification and a justification for not selecting one was not made
            // Form and field level errors
            if (!hasVerifiedQualifications && StringUtils.isBlank(explanation)) {
            	errors.rejectValue("qualificationsSendToPortico", "portico.submit.no.qualification.or.explanation");
            	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emptyQualificationsExplanation", EMPTY_FIELD_ERROR_MESSAGE);
            }
  
            // In either of the above scenarios an out of range text explanation was provided
            // Field level error
            if (!StringUtils.isBlank(explanation) && explanation.trim().length() > 500) {
            	errors.rejectValue("qualificationsSendToPortico", "portico.submit.no.qualification.or.explanation");
            }
            
        }

        if (referees != null) {
        	
            for (int i = 0; i < referees.size(); i++) {
            	Comment reference = refereeService.getById(referees.get(i)).getComment();
                if (reference == null || reference.getDeclinedResponse()) {
            		referees.remove(i);
            	}
            }

            if (referees.size() < 2) {
                errors.rejectValue("refereesSendToPortico", "portico.submit.referees.invalid");
            }
        }
    }
}