package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.dao.SourcesOfInterestDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.StudyOption;

@Service
public class ProgrammeDetailsService {

	private final ProgrammeDetailDAO programmeDetailDAO;
	private final ProgramInstanceDAO programInstanceDAO;
	private final SourcesOfInterestDAO sourcesOfInterestDAO;

	ProgrammeDetailsService() {
		this(null, null, null);
	}

	@Autowired
	public ProgrammeDetailsService(ProgrammeDetailDAO programmeDetailDAO, 
	        ProgramInstanceDAO programInstanceDAO, SourcesOfInterestDAO sourcesOfInterestDAO) {
		this.programmeDetailDAO = programmeDetailDAO;
		this.programInstanceDAO = programInstanceDAO;
		this.sourcesOfInterestDAO = sourcesOfInterestDAO;
	}

	@Transactional
	public void save(ProgrammeDetails pd) {
		programmeDetailDAO.save(pd);
	}
	
	@Transactional
	public List<StudyOption> getAvailableStudyOptions(Program program) {
		HashSet<StudyOption> options = new HashSet<StudyOption>();
		List<ProgramInstance> activeProgramInstances = programInstanceDAO.getActiveProgramInstances(program);
		for (ProgramInstance programInstance : activeProgramInstances) {
			StudyOption option = new StudyOption(programInstance.getStudyOptionCode(), programInstance.getStudyOption());
		    options.add(option);
		}
		return new ArrayList<StudyOption>(options);
	}
	
	@Transactional
    public Integer getStudyOptionCodeForProgram(Program program, String studyOption) {
        for (ProgramInstance programInstance :  programInstanceDAO.getActiveProgramInstances(program)) {
            if (StringUtils.equalsIgnoreCase(programInstance.getStudyOption(), studyOption)) {
                return programInstance.getStudyOptionCode();
            }
        }
        return -1;
    }

	@Transactional
	public List<ProgramInstance> getActiveProgramInstancesOrderedByApplicationStartDate(Program program, String studyOption) {
	    return programInstanceDAO.getActiveProgramInstancesOrderedByApplicationStartDate(program, studyOption);
	}
	
	@Transactional
    public List<SourcesOfInterest> getAllEnabledSourcesOfInterest() {
        return sourcesOfInterestDAO.getAllEnabledSourcesOfInterest();
    }
}
