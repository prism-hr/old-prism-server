package com.zuehlke.pgadmissions.rest.validation.validator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ProgramService;

@Component
public class CompleteApplicationValidator extends LocalValidatorFactoryBean implements Validator {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private ProgramService programService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);

    }

    @Override
    public void validate(Object target, Errors errors) {
        validate(target, errors, new Object[0]);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        Application application = (Application) target;

        ValidationUtils.rejectIfEmpty(errors, "programDetail", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "personalDetail", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "address", "notNull");
        // FIXME uncomment when documents are implemented
//        ValidationUtils.rejectIfEmpty(errors, "document", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "additionalInformation", "notNull");


        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail != null) {
            errors.pushNestedPath("programDetail");
            LocalDate startDate = programDetail.getStartDate();
            
            Program program = application.getProgram();
            ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(program, programDetail.getStudyOption());
            
            if (studyOption == null) {
                List<ProgramStudyOption> otherStudyOptions = programService.getEnabledProgramStudyOptions(program);
                if (otherStudyOptions.isEmpty()) {
                    throw new CannotApplyException();
                }
                errors.rejectValue("studyOption", "notAvailable");
            } else {
                LocalDate earliestStartDate = applicationService.getEarliestStartDate(studyOption, new LocalDate());
                LocalDate latestStartDate = applicationService.getLatestStartDate(studyOption);
    
                if (startDate.isBefore(earliestStartDate)) {
                    errors.rejectValue("startDate", "notBefore", new Object[]{earliestStartDate}, null);
                } else if (startDate.isAfter(latestStartDate)) {
                    errors.rejectValue("startDate", "notAfter", new Object[]{latestStartDate}, null);
                }
            }

            errors.popNestedPath();
        }
        if (application.getReferees().size() != 3) {
            errors.rejectValue("referees", "size.exact", new Object[]{3}, null);
        }

    }
}
