package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;

@Service
@Transactional
public class ProgramInstanceService {
    
    private static final int CONSIDERATION_PERIOD_MONTHS = 1;

    @Autowired
    private ThrottleService throttleService;

    public boolean isProgrammeStillAvailable(ApplicationForm applicationForm) {
        Date maxProgrammeEndDate = null;
        Date today = new Date();

        ProgrammeDetails details = applicationForm.getProgrammeDetails();

        for (ProgramInstance instance : applicationForm.getProgram().getInstances()) {
            boolean isProgrammeEnabled = applicationForm.getProgram().isEnabled();
            boolean isInstanceEnabled = isActive(instance);
            boolean sameStudyOption = details.getStudyOption().equals(instance.getStudyOption());
            boolean sameStudyOptionCode = details.getStudyOptionCode().equals(instance.getStudyOptionCode());

            if (isProgrammeEnabled && isInstanceEnabled && sameStudyOption && sameStudyOptionCode) {
                Date programmeEndDate = instance.getApplicationDeadline();
                if (maxProgrammeEndDate == null) {
                    maxProgrammeEndDate = programmeEndDate;
                } else if (programmeEndDate.after(maxProgrammeEndDate)) {
                    maxProgrammeEndDate = programmeEndDate;
                }
            }
        }

        if (maxProgrammeEndDate == null || maxProgrammeEndDate.before(today)) {
            return false;
        }

        return true;
    }
    
    public Date getEarliestPossibleStartDate(ApplicationForm applicationForm) {
        Date result = null;
        ProgrammeDetails details = applicationForm.getProgrammeDetails();
        Date today = new Date();
        Date todayPlusConsiderationPeriod = DateUtils.addMonths(today, CONSIDERATION_PERIOD_MONTHS);
        for (ProgramInstance instance : applicationForm.getProgram().getInstances()) {
            Date applicationStartDate = instance.getApplicationStartDate();
            boolean startDateInFuture = today.before(applicationStartDate);
            boolean beforeEndDate = todayPlusConsiderationPeriod.before(instance.getApplicationDeadline());
            boolean sameStudyOption = details.getStudyOption().equals(instance.getStudyOption());
            boolean sameStudyOptionCode = details.getStudyOptionCode().equals(instance.getStudyOptionCode());
            if (applicationForm.getProgram().isEnabled() && isActive(instance) && (startDateInFuture || beforeEndDate) && sameStudyOption && sameStudyOptionCode) {
                if (startDateInFuture && (result == null || result.after(applicationStartDate))) {
                    result = applicationStartDate;
                } else if (result == null || result.after(todayPlusConsiderationPeriod)) {
                    result = todayPlusConsiderationPeriod;
                }
            }
        }
        return result;
    }

    public boolean isPrefferedStartDateWithinBounds(ApplicationForm applicationForm) {
        return isPreferredStartDateWithinBounds(applicationForm, applicationForm.getProgrammeDetails(), applicationForm.getProgrammeDetails().getStartDate());
    }
    
    public boolean isPrefferedStartDateWithinBounds(ApplicationForm applicationForm, Date startDate) {
    	return isPreferredStartDateWithinBounds(applicationForm, applicationForm.getProgrammeDetails(), startDate);
    }
    
    private boolean isPreferredStartDateWithinBounds(ApplicationForm applicationForm, ProgrammeDetails programDetails, Date startDate) {
        for (ProgramInstance instance : applicationForm.getProgram().getInstances()) {
            boolean afterStartDate = startDate.after(instance.getApplicationStartDate());
            boolean beforeEndDate = startDate.before(instance.getApplicationDeadline());
            boolean sameStudyOption = programDetails.getStudyOption().equals(instance.getStudyOption());
            boolean sameStudyOptionCode = programDetails.getStudyOptionCode().equals(instance.getStudyOptionCode());
            if (applicationForm.getProgram().isEnabled() && isActive(instance) && afterStartDate && beforeEndDate && sameStudyOption && sameStudyOptionCode) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isActive(ProgramInstance programInstance){
        if(programInstance.getEnabled()){
            return true;
        }
        if(programInstance.getDisabledDate() != null){
            LocalDate disableLocalDate = new LocalDate(programInstance.getDisabledDate().getTime());
            LocalDate today = new LocalDate();
            
            int processingDelay = throttleService.getProcessingDelayInDays();
            if(today.isBefore(disableLocalDate.plusDays(processingDelay))){
                return true;
            }
        }
        return false;
    }

}
