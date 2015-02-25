package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.services.ProgramService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramServiceHelper implements AbstractServiceHelper {

    @Autowired
    private ProgramService programService;

    @Override
    public void execute() {
        LocalDate baseline = new LocalDate();
        List<Program> programs = programService.getProgramsWithElapsedStudyOptions(baseline);
        for (Program program : programs) {
            programService.updateProgramStudyOptions(program, baseline);
        }
    }

}
