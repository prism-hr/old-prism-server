package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperInstitution;

@Service
public class InstitutionDataImportHelper {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityServiceHelperInstitution entityImportHelper;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StateService stateService;

    public void verifyEntityImport(Institution institution) throws DataImportException {
        
    }

    @Transactional
    public void verifyImportedProgramInitialisation() {
        List<Program> programs = programService.getPrograms();
        for (Program program : programs) {
            List<ProgramStudyOption> options = programService.getEnabledProgramStudyOptions(program);
           
            for (ProgramStudyOption option : options) {
                boolean optionEnabled = option.isEnabled();
                
                LocalDate applicationStartDate = null;
                LocalDate applicationCloseDate = null;
                  
                int enabledInstances = 0;
                
                for (ProgramStudyOptionInstance instance : option.getStudyOptionInstances()) {
                    boolean instanceEnabled = instance.isEnabled();
                    enabledInstances = enabledInstances + (instanceEnabled ? 1 : 0);
                    
                    if (instanceEnabled && optionEnabled) {
                        LocalDate instanceStartDate = instance.getApplicationStartDate();
                        LocalDate instanceCloseDate = instance.getApplicationCloseDate();
                        
                        applicationStartDate = instanceStartDate.isBefore(applicationStartDate) ? instanceStartDate : applicationStartDate;
                        applicationCloseDate = instanceCloseDate.isAfter(applicationCloseDate) ? instanceCloseDate : applicationCloseDate;
                    }
                }
                
                if (optionEnabled) {
                    assertEquals(applicationStartDate, option.getApplicationStartDate());
                    assertEquals(applicationCloseDate, option.getApplicationCloseDate());
                }
                
                assertEquals(option.isEnabled(), enabledInstances > 0);
            }
            
            LocalDate baseline = new LocalDate();
            LocalDate programDueDate = program.getDueDate();
            
            if (options.size() == 0) {
                assertTrue(programDueDate.equals(baseline) || programDueDate.isBefore(baseline));
            } else {
                LocalDate programClosureDate = programService.getProgramClosureDate(program);
                assertEquals(programClosureDate, programDueDate);
            }
        }
    }

    @Transactional
    public void verifyImportedProgramReactivation() {
        Program programToDisable1 = programService.getProgramByImportedCode(null, "RRDMECSING01");
        Program programToDisable2 = programService.getProgramByImportedCode(null, "RRDMPHSING01");

        programToDisable1.setState(stateService.getById(PrismState.PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION));
        programToDisable2.setState(stateService.getById(PrismState.PROGRAM_DISABLED_COMPLETED));

        // TODO confirm that this is working
    }

}
