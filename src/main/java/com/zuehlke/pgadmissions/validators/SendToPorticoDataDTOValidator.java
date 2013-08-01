package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;

@Component
public class SendToPorticoDataDTOValidator extends AbstractValidator {

    private final QualificationService qualificationService;

    private final RefereeService refereeService;

    @Autowired
    public SendToPorticoDataDTOValidator(QualificationService qualificationService, RefereeService refereeService) {
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
            if (qualifications.isEmpty()) {
                if (StringUtils.isBlank(explanation)) {
                    errors.rejectValue("qualificationsSendToPortico", "portico.submit.explanation.required");                    
                }
            }

            if (qualifications.size() > 2) {
                errors.rejectValue("qualificationsSendToPortico", "portico.submit.qualifications.exceed");
            }

            for (Integer qualificationId : qualifications) {
                Qualification qualification = qualificationService.getQualificationById(qualificationId);
                if (qualification.getProofOfAward() == null) {
                    errors.rejectValue("qualificationsSendToPortico", "portico.submit.qualifications.noProofOfAward");
                }
            }
        }

        if (referees != null) {
            if (referees.size() != 2) {
                errors.rejectValue("refereesSendToPortico", "portico.submit.referees.invalid");
            }

            for (Integer refereeId : referees) {
                Referee referee = refereeService.getRefereeById(refereeId);
                if (!referee.hasResponded()) {
                    errors.rejectValue("refereesSendToPortico", "portico.submit.referees.hasNotResponded");
                }
            }
        }

    }

}
