package com.zuehlke.pgadmissions.lifecycle;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.services.ProgramService;

@Component
public class ProgramHelper {
    
    @Autowired
    private ProgramService programService;

    public void updateProgramStudyOptions() {
        LocalDate baseline = new LocalDate();
        List<Program> programs = programService.getProgramsWithElapsedStudyOptions(baseline);
        
        for (Program program : programs) {
            programService.updateProgramStudyOptions(program, baseline);
        }
    }
    
}
