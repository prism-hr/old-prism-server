package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
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

    @Autowired
    private ApplicationContext applicationContext;

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

    @Transactional
    public List<StudyOption> getDistinctStudyOptions() {
        List<Object[]> options = programInstanceDAO.getDistinctStudyOptions();
        List<StudyOption> studyOptions = Lists.newArrayListWithCapacity(options.size());

        for (Object[] option : options) {
            StudyOption studyOption = new StudyOption(option[0].toString(), option[1].toString());
            studyOptions.add(studyOption);
        }
        return studyOptions;
    }

    @Transactional
    public List<ProgramInstance> createRemoveProgramInstances(Program program, String studyOptionCodes, int advertisingDeadlineYear) {
        ProgramInstanceService thisBean = applicationContext.getBean(ProgramInstanceService.class);

        // disable all existing instances
        for (ProgramInstance existingInstance : program.getInstances()) {
            existingInstance.setEnabled(false);
        }
        program.setEnabled(false);

        List<ProgramInstance> instances = Lists.newLinkedList();

        List<StudyOption> studyOptions = thisBean.getStudyOptions(studyOptionCodes);
        int startYear = thisBean.getFirstProgramInstanceStartYear(new DateTime());

        for (; startYear < advertisingDeadlineYear; startYear++) {
            for (StudyOption studyOption : studyOptions) {
                ProgramInstance programInstance = thisBean.createOrUpdateProgramInstance(program, startYear, studyOption);
                instances.add(programInstance);
                program.setEnabled(true);
            }
        }
        return instances;
    }

    protected ProgramInstance createOrUpdateProgramInstance(Program program, int startYear, StudyOption studyOption) {
        ProgramInstanceService thisBean = applicationContext.getBean(ProgramInstanceService.class);
        DateTime startDate = thisBean.findPenultimateSeptemberMonday(startYear);
        DateTime deadline = thisBean.findPenultimateSeptemberMonday(startYear + 1);

        ProgramInstance programInstance = programInstanceDAO.getProgramInstance(program, studyOption, startDate.toDate());
        if (programInstance == null) {
            programInstance = new ProgramInstance();
            programInstance.setProgram(program);
            program.getInstances().add(programInstance);
        }

        programInstance.setApplicationStartDate(startDate.toDate());
        programInstance.setAcademicYear(Integer.toString(startYear));
        programInstance.setApplicationDeadline(deadline.toDate());
        programInstance.setDisabledDate(deadline.minusMonths(1).toDate());
        programInstance.setEnabled(true);
        programInstance.setIdentifier("CUSTOM");
        programInstance.setStudyOption(studyOption.getName());
        programInstance.setStudyOptionCode(studyOption.getId());

        programInstanceDAO.save(programInstance);
        return programInstance;
    }

    protected List<StudyOption> getStudyOptions(String studyOptionCodesSplit) {
        List<String> studyOptionCodes = Arrays.asList(studyOptionCodesSplit.split(","));
        ProgramInstanceService thisBean = applicationContext.getBean(ProgramInstanceService.class);
        List<StudyOption> distinctStudyOptions = thisBean.getDistinctStudyOptions();

        List<StudyOption> studyOptions = Lists.newArrayListWithCapacity(studyOptionCodes.size());
        for (StudyOption o : distinctStudyOptions) {
            if (studyOptionCodes.contains(o.getId())) {
                studyOptions.add(o);
            }
        }
        return studyOptions;
    }

    public int getFirstProgramInstanceStartYear(DateTime startDate) {
        int year = startDate.getYear();
        DateTime actualStartDate = findPenultimateSeptemberMonday(year);

        if (actualStartDate.isAfter(startDate)) {
            year--;
        }

        return year;
    }

    protected DateTime findPenultimateSeptemberMonday(int year) {
        DateTime penultimateSeptemberMonday = new DateTime(year, 9, 30, 0, 0);
        penultimateSeptemberMonday = penultimateSeptemberMonday.minusWeeks(1);

        while (penultimateSeptemberMonday.getDayOfWeek() != 1) {
            penultimateSeptemberMonday = penultimateSeptemberMonday.minusDays(1);
        }

        return penultimateSeptemberMonday;
    }

    @Transactional
    public void disableLapsedInstances() {
        Set<Program> modifiedPrograms = Sets.newHashSet();

        List<ProgramInstance> lapsedInstances = programInstanceDAO.getLapsedInstances();
        for (ProgramInstance lapsedInstance : lapsedInstances) {
            lapsedInstance.setEnabled(false);
            modifiedPrograms.add(lapsedInstance.getProgram());
        }

        // disable programs without active instances
        for (Program program : modifiedPrograms) {
            if (program.getProgramFeed() != null) {
                throw new RuntimeException("Only custom programs can be disabled during data maintenance. Something went wrong. " + program.getId());
            }
            if (programInstanceDAO.getActiveProgramInstances(program).isEmpty()) {
                program.setEnabled(false);
            }
        }

    }

    public List<Integer> getPossibleAdvertisingDeadlineYears() {
        int deadlineYear = new DateTime().getYear();
        if (new DateTime().getMonthOfYear() >= DateTimeConstants.SEPTEMBER) {
            deadlineYear++;
        }

        List<Integer> advertisingDeadlines = Lists.newArrayListWithCapacity(10);
        for (int i = 0; i < 10; i++) {
            advertisingDeadlines.add(deadlineYear + i);
        }
        return advertisingDeadlines;
    }

    public int getAdvertisingDeadlineYear(Program program) {
        return new DateTime(programInstanceDAO.getLatestActiveInstanceDeadline(program)).getYear();
    }

    public List<String> getStudyOptions(Program program) {
        return programInstanceDAO.getStudyOptions(program);
    }

}
