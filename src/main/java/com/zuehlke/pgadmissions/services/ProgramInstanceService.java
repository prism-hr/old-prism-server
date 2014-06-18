package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;

@Service
public class ProgramInstanceService {

    @Autowired
    private ApplicationExportConfigurationService throttleService;

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private ApplicationContext applicationContext;

// TODO: rewrite as a query    
    
//    public LocalDate getEarliestPossibleStartDate(Application applicationForm) {
//        LocalDate result = null;
//        ProgramDetails details = applicationForm.getProgramDetails();
//        LocalDate today = new LocalDate();
//        LocalDate todayPlusConsiderationPeriod = today.plusMonths(CONSIDERATION_PERIOD_MONTHS);
//        for (ProgramInstance instance : applicationForm.getProgram().getInstances()) {
//            LocalDate applicationStartDate = instance.getApplicationStartDate();
//            boolean startDateInFuture = today.isBefore(applicationStartDate);
//            boolean beforeEndDate = todayPlusConsiderationPeriod.isBefore(instance.getApplicationDeadline());
//            boolean sameStudyOption = details.getStudyOption().getId().equals(instance.getStudyOption().getId());
//            if (applicationForm.getAdvert().isFertile() && isActive(instance) && (startDateInFuture || beforeEndDate) && sameStudyOption) {
//                if (startDateInFuture && (result == null || result.isAfter(applicationStartDate))) {
//                    result = applicationStartDate;
//                } else if (result == null || result.isAfter(todayPlusConsiderationPeriod)) {
//                    result = todayPlusConsiderationPeriod;
//                }
//            }
//        }
//        return result;
//    }

// TODO: reconsider/rewrite as queries    
    
//    public boolean isPrefferedStartDateWithinBounds(Application applicationForm) {
//        return isPreferredStartDateWithinBounds(applicationForm, applicationForm.getProgramDetails(), applicationForm.getProgramDetails().getStartDate());
//    }
//
//    public boolean isPrefferedStartDateWithinBounds(Application applicationForm, LocalDate startDate) {
//        return isPreferredStartDateWithinBounds(applicationForm, applicationForm.getProgramDetails(), startDate);
//    }
//
//    private boolean isPreferredStartDateWithinBounds(Application applicationForm, ProgramDetails programDetails, LocalDate startDate) {
//        for (ProgramInstance instance : applicationForm.getProgram().getInstances()) {
//            boolean afterStartDate = startDate.isAfter(instance.getApplicationStartDate());
//            boolean beforeEndDate = startDate.isBefore(instance.getApplicationDeadline());
//            boolean sameStudyOption = programDetails.getStudyOption().getId().equals(instance.getStudyOption().getId());
//            if (applicationForm.getAdvert().isFertile() && isActive(instance) && afterStartDate && beforeEndDate && sameStudyOption) {
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean isActive(ProgramInstance programInstance) {
        // TODO use program.isEnabled()
        // if (programInstance.getEnabled()) {
        // return true;
        // }
        // if (programInstance.getDisabledDate() != null) {
        // LocalDate disableLocalDate = new LocalDate(programInstance.getDisabledDate().getTime());
        // LocalDate today = new LocalDate();
        //
        // int processingDelay = throttleService.getProcessingDelayInDays();
        // if (today.isBefore(disableLocalDate.plusDays(processingDelay))) {
        // return true;
        // }
        // }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected ProgramInstance createOrUpdateProgramInstance(Program program, int startYear, StudyOption studyOption) {
        ProgramInstanceService thisBean = applicationContext.getBean(ProgramInstanceService.class);
        LocalDate startDate = thisBean.findPenultimateSeptemberMonday(startYear);
        LocalDate deadline = thisBean.findPenultimateSeptemberMonday(startYear + 1);

        ProgramInstance programInstance = programDAO.getProgramInstance(program, studyOption, startDate.toDate());
        if (programInstance == null) {
            programInstance = new ProgramInstance();
            programInstance.setProgram(program);
            program.getProgramInstances().add(programInstance);
        }

        programInstance.setApplicationStartDate(startDate);
        programInstance.setAcademicYear(Integer.toString(startYear));
        programInstance.setApplicationDeadline(deadline);
        programInstance.setEnabled(true);
        programInstance.setIdentifier("CUSTOM");
        programInstance.setStudyOption(studyOption);
        programDAO.save(programInstance);

        return programInstance;
    }

    public List<StudyOption> getAvailableStudyOptions() {
        return programDAO.getAvailableStudyOptions();
    }

    public int getFirstProgramInstanceStartYear(LocalDate startDate) {
        int year = startDate.getYear();
        LocalDate actualStartDate = findPenultimateSeptemberMonday(year);

        if (actualStartDate.isAfter(startDate)) {
            year--;
        }

        return year;
    }

    protected LocalDate findPenultimateSeptemberMonday(int year) {
        LocalDate penultimateSeptemberMonday = new LocalDate(year, 9, 30);
        penultimateSeptemberMonday = penultimateSeptemberMonday.minusWeeks(1);

        while (penultimateSeptemberMonday.getDayOfWeek() != 1) {
            penultimateSeptemberMonday = penultimateSeptemberMonday.minusDays(1);
        }

        return penultimateSeptemberMonday;
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
        return new DateTime(programDAO.getLatestActiveInstanceDeadline(program)).getYear();
    }

    public List<StudyOption> getStudyOptions(Program program) {
        return programDAO.getAvailableStudyOptions(program);
    }

    public ProgramInstance getByProgramAndAcademicYearAndStudyOption(Program program, String academicYear, StudyOption studyOption) {
        return programDAO.getByProgramAndAcademicYearAndStudyOption(program, academicYear, studyOption);
    }

    protected List<StudyOption> getStudyOptions(String studyOptionCodesSplit) {
        List<String> studyOptionCodes = Arrays.asList(studyOptionCodesSplit.split(","));
        List<StudyOption> distinctStudyOptions = programDAO.getAvailableStudyOptions();

        List<StudyOption> studyOptions = Lists.newArrayListWithCapacity(studyOptionCodes.size());
        for (StudyOption o : distinctStudyOptions) {
            if (studyOptionCodes.contains(o.getId())) {
                studyOptions.add(o);
            }
        }
        return studyOptions;
    }
    
    @Transactional
    public List<ProgramInstance> createRemoveProgramInstances(Program program, String studyOptionCodes, int advertisingDeadlineYear) {
        ProgramInstanceService thisBean = applicationContext.getBean(ProgramInstanceService.class);

        // disable all existing instances
        for (ProgramInstance existingInstance : program.getProgramInstances()) {
            existingInstance.setEnabled(false);
        }
        // program.setState(ProgramState.PROGRAM_DEACTIVATED);

        List<ProgramInstance> instances = Lists.newLinkedList();

        List<StudyOption> studyOptions = thisBean.getStudyOptions(studyOptionCodes);
        int startYear = thisBean.getFirstProgramInstanceStartYear(new LocalDate());

        for (; startYear < advertisingDeadlineYear; startYear++) {
            for (StudyOption studyOption : studyOptions) {
                ProgramInstance programInstance = thisBean.createOrUpdateProgramInstance(program, startYear, studyOption);
                instances.add(programInstance);
                // program.setState(ProgramState.PROGRAM_APPROVED);
            }
        }
        return instances;
    }
    
}
