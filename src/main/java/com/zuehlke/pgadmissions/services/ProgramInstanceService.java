package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.StudyOption;

@Service
public class ProgramInstanceService {

    private static final int CONSIDERATION_PERIOD_MONTHS = 1;

    @Autowired
    private ThrottleService throttleService;

    @Autowired
    private ProgramInstanceDAO programInstanceDAO;

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
            if (applicationForm.getProgram().isEnabled() && isActive(instance) && (startDateInFuture || beforeEndDate) && sameStudyOption
                    && sameStudyOptionCode) {
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

    public boolean isActive(ProgramInstance programInstance) {
        if (programInstance.getEnabled()) {
            return true;
        }
        if (programInstance.getDisabledDate() != null) {
            LocalDate disableLocalDate = new LocalDate(programInstance.getDisabledDate().getTime());
            LocalDate today = new LocalDate();

            int processingDelay = throttleService.getProcessingDelayInDays();
            if (today.isBefore(disableLocalDate.plusDays(processingDelay))) {
                return true;
            }
        }
        return false;
    }

    public List<StudyOption> getDistinctStudyOptions() {
        List<String[]> options = programInstanceDAO.getDistinctStudyOptions();
        List<StudyOption> studyOptions = Lists.newArrayListWithCapacity(options.size());

        for (String[] option : options) {
            StudyOption studyOption = new StudyOption(option[0], option[1]);
            studyOptions.add(studyOption);
        }
        return studyOptions;
    }

    @Transactional
    public List<ProgramInstance> createNewCustomProgramInstances(OpportunityRequest opportunityRequest, Program program) {
        List<ProgramInstance> instances = Lists.newArrayListWithCapacity(opportunityRequest.getAdvertisingDuration() + 1);

        DateTime startDate = getCustomProgramInstanceStartDate(new DateTime(opportunityRequest.getApplicationStartDate()));

        for (int i = 0; i <= opportunityRequest.getAdvertisingDuration(); i++) {
            DateTime deadline = findLastSeptemberMonday(startDate.getYear() + 1);

            ProgramInstance programInstance = new ProgramInstance();
            programInstance.setApplicationStartDate(startDate.toDate());
            programInstance.setAcademicYear(Integer.toString(startDate.getYear()));
            programInstance.setApplicationDeadline(deadline.toDate());
            programInstance.setDisabledDate(deadline.minusMonths(1).toDate());
            programInstance.setEnabled(true);
            programInstance.setIdentifier("CUSTOM");
            programInstance.setProgram(program);
            programInstance.setStudyOption("Full-time");
            programInstance.setStudyOptionCode("F+++++");

            programInstanceDAO.save(programInstance);

            instances.add(programInstance);
        }
        return instances;
    }

    private DateTime getCustomProgramInstanceStartDate(DateTime intendedStartDate) {
        DateTime startDate = new DateTime();

        if (intendedStartDate.isAfter(startDate)) {
            startDate = intendedStartDate;
        }

        return getLastProgramInstanceStartDate(startDate);
    }

    private DateTime getLastProgramInstanceStartDate(DateTime startDate) {
        int yearNow = startDate.getYear();
        DateTime actualStartDate = findLastSeptemberMonday(yearNow);

        if (actualStartDate.isAfter(startDate)) {
            actualStartDate = findLastSeptemberMonday(yearNow - 1);
        }

        return actualStartDate;
    }

    private DateTime findLastSeptemberMonday(int year) {
        DateTime lastSeptemberMonday = new DateTime(year, 9, 30, 0, 0);

        while (lastSeptemberMonday.getDayOfWeek() != 1) {
            lastSeptemberMonday.minusDays(1);
        }

        return lastSeptemberMonday;
    }

}