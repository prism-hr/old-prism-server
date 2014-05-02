package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgramDetails;

@Service
@Transactional
public class ProgramDetailsService {
    
    @Autowired 
    private ApplicationFormService applicationFormService;
    
    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;
    
    public ProgramDetails getOrCreate(ApplicationForm application) {
        ProgramDetails programDetails = application.getProgramDetails();
        if (programDetails == null) {
            programDetails = new ProgramDetails();
        }
        return programDetails;
    }
    
    public void saveOrUpdate(ApplicationForm application, ProgramDetails programDetails) {
        ProgramDetails persistentProgramDetails = application.getProgramDetails();
        if (persistentProgramDetails == null) {
            persistentProgramDetails = new ProgramDetails();
            persistentProgramDetails.setApplication(application);
            application.setProgramDetails(persistentProgramDetails);
            applicationFormService.save(application);
        }
        applicationFormCopyHelper.copyProgramDetails(persistentProgramDetails, programDetails);
        applicationFormService.saveOrUpdateApplicationSection(application);
    }
    
}
