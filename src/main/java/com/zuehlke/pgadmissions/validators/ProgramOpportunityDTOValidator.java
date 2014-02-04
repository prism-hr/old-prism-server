package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.dto.ProgramOpportunityDTO;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramsService;

@Component
public class ProgramOpportunityDTOValidator extends AbstractValidator {

    @Autowired
    private ProgramsService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Override
    public boolean supports(Class<?> clazz) {
        return ProgramOpportunityDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ProgramOpportunityDTO dto = (ProgramOpportunityDTO) target;

        Program program = null;
        String programCode = dto.getProgramCode();
        if (programCode == null) {
            // trying to create new program
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programName", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "atasRequired", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCode", EMPTY_FIELD_ERROR_MESSAGE);

            // validate institution code / name
            String institutionCode = dto.getInstitutionCode();
            if (StringUtils.equalsIgnoreCase("OTHER", institutionCode)) {
                if (StringUtils.isBlank(dto.getOtherInstitution())) {
                    errors.rejectValue("otherInstitution", EMPTY_FIELD_ERROR_MESSAGE);
                }
            }

        } else {
            program = programsService.getProgramByCode(dto.getProgramCode());
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);
        validateStudyDuration(errors, dto.getStudyDuration());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "active", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (program == null || program.getProgramFeed() == null) {
            // new or custom program
            if (dto.getStudyOptions().isEmpty()) {
                errors.rejectValue("studyOptions", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }

            Integer deadlineYear = dto.getAdvertiseDeadlineYear();
            List<Integer> possibleDeadlineYears = programInstanceService.getPossibleAdvertisingDeadlineYears();

            if (deadlineYear == null || deadlineYear < possibleDeadlineYears.get(0) || deadlineYear > Iterables.getLast(possibleDeadlineYears)) {
                errors.rejectValue("advertiseDeadlineYear", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
        }
    }

    void setProgramsService(ProgramsService programsService) {
        this.programsService = programsService;
    }

    void setProgramInstanceService(ProgramInstanceService programInstanceService) {
        this.programInstanceService = programInstanceService;
    }

}