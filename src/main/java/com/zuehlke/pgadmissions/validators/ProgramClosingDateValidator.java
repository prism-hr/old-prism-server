package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.dao.ProgramClosingDateDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;

@Component
public class ProgramClosingDateValidator extends AbstractValidator {

    private static final String PROSPECTUS_CLOSING_DATE_ALREADY_EXISTS = "prospectus.closingDate.alreadyExists";
	private static final String PROSPECTUS_CLOSING_DATE_STUDY_PLACES_NUMBER_NOT_GREATER_THAN_ZERO = "prospectus.closingDate.studyPlaces.numberNotGreaterThanZero";
    private final ProgramClosingDateDAO programClosingDateDAO = new ProgramClosingDateDAO();

    @Override
    public boolean supports(Class<?> clazz) {
        return ProgramClosingDate.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ProgramClosingDate newClosingDate = (ProgramClosingDate) target;
        Program program = newClosingDate.getProgram();
		if(program==null){
            errors.rejectValue("program", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
		
		
        
        Date date = newClosingDate.getClosingDate();
        if(date==null){
            errors.rejectValue("closingDate", MUST_SELECT_DATE_AND_TIME);
        }else{
        	if(!date.after(new Date())){
        		errors.rejectValue("closingDate", MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE);
        	}
            if(program!=null){
            	ProgramClosingDate existingDate = programClosingDateDAO.getByDate(date);
            	if(existingDate!=null && (newClosingDate.getId()==null||newClosingDate.getId()!=existingDate.getId())){
            			errors.rejectValue("closingDate", PROSPECTUS_CLOSING_DATE_ALREADY_EXISTS);
            	}
            }
        }

        Integer studyPlaces = newClosingDate.getStudyPlaces();
        if(studyPlaces!=null && studyPlaces<=0){
            errors.rejectValue("studyPlaces", PROSPECTUS_CLOSING_DATE_STUDY_PLACES_NUMBER_NOT_GREATER_THAN_ZERO);
        }

    }
}
