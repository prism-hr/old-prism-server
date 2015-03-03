package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.ProgramService;

@Component
public class ProgramServiceHelper implements AbstractServiceHelper {

    @Autowired
    private ProgramService programService;

    @Override
    public void execute() {
        programService.disableElapsedProgramStudyOptions();
    }

}
