package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

@Service
public class ProgrammeDetailsService {

	private final ProgrammeDetailDAO programmeDetailDAO;
	private final ProgramInstanceDAO programInstanceDAO;

	ProgrammeDetailsService() {
		this(null, null);
	}

	@Autowired
	public ProgrammeDetailsService(ProgrammeDetailDAO programmeDetailDAO, ProgramInstanceDAO programInstanceDAO) {
		this.programmeDetailDAO = programmeDetailDAO;
		this.programInstanceDAO = programInstanceDAO;

	}

	@Transactional
	public void save(ProgrammeDetails pd) {
		programmeDetailDAO.save(pd);
	}
	
	@Transactional
	public List<StudyOption> getAvailableStudyOptions(Program program) {
		List<StudyOption> options = new ArrayList<StudyOption>();
		List<ProgramInstance> activeProgramInstances = programInstanceDAO.getActiveProgramInstances(program);
		for (ProgramInstance programInstance : activeProgramInstances) {
			if (!options.contains(programInstance.getStudyOption())) {
				options.add(programInstance.getStudyOption());
			}
		}
		return options;
	}

}
