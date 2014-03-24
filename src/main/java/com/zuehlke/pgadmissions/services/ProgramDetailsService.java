package com.zuehlke.pgadmissions.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;

@Service
@Transactional
public class ProgramDetailsService {
    
    @Autowired
	private ProgramInstanceDAO programInstanceDAO;
    
    @Autowired 
    private ApplicationFormService applicationFormService;
    
    
    public ProgramDetails getOrCreate(ApplicationForm application) {
        ProgramDetails programDetails = application.getProgramDetails();
        if (programDetails == null) {
            programDetails = new ProgramDetails();
        }
        return programDetails;
    }
	
	public List<StudyOption> getAvailableStudyOptions(Program program) {
		HashSet<StudyOption> options = new HashSet<StudyOption>();
		List<ProgramInstance> activeProgramInstances = programInstanceDAO.getActiveProgramInstances(program);
		for (ProgramInstance programInstance : activeProgramInstances) {
			StudyOption option = new StudyOption(programInstance.getStudyOptionCode(), programInstance.getStudyOption());
		    options.add(option);
		}
		return new ArrayList<StudyOption>(options);
	}
	
    public String getStudyOptionCodeForProgram(Program program, String studyOption) {
        for (ProgramInstance programInstance :  programInstanceDAO.getActiveProgramInstances(program)) {
            if (StringUtils.equalsIgnoreCase(programInstance.getStudyOption(), studyOption)) {
                return programInstance.getStudyOptionCode();
            }
        }
        return null;
    }
    
    public String getDefaultStartDateForDisplay(ApplicationForm application) {
        Date defaultStartDate = applicationFormService.getDefaultStartDate(application);
        if (defaultStartDate != null) {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            return format.format(defaultStartDate);
        }
        return StringUtils.EMPTY;
    }
 	
}
