package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetails;

@Service
@Transactional
public class ProgramDetailsService {
    
    @Autowired 
    private ApplicationService applicationFormService;
    
    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;
    
    public ApplicationProgramDetails getOrCreate(Application application) {
        ApplicationProgramDetails programDetails = application.getProgramDetails();
        if (programDetails == null) {
            programDetails = new ApplicationProgramDetails();
        }
        return programDetails;
    }
    
    public void saveOrUpdate(Application application, ApplicationProgramDetails programDetails) {
        ApplicationProgramDetails persistentProgramDetails = application.getProgramDetails();
        if (persistentProgramDetails == null) {
            persistentProgramDetails = new ApplicationProgramDetails();
            persistentProgramDetails.setApplication(application);
            application.setProgramDetails(persistentProgramDetails);
            applicationFormService.save(application);
        }
        applicationFormCopyHelper.copyProgramDetails(persistentProgramDetails, programDetails);
        applicationFormService.saveOrUpdateApplicationSection(application);
    }
    
}
