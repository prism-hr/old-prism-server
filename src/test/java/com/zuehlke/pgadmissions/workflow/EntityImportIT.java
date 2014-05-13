package com.zuehlke.pgadmissions.workflow;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.ImportedEntityType;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class EntityImportIT {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private InstitutionService institutionService;

    @SuppressWarnings("unchecked")
    @Test
    public void testConflictsInProgramImport() throws Exception {
        Institution ucl = institutionService.getByCode("UK0275");
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(ImportedEntityType.PROGRAM);
        importedEntityFeed.setLocation("reference_data/conflicts/programs/initialPrograms.xml");
        importedEntityFeed.setInstitution(ucl);

        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, programService.getAllPrograms().size());
        Program program1 = programService.getProgramByCode("1");
        Program otherProgram = programService.getProgramByCode("99");
        assertEquals("program1", program1.getTitle());
        assertEquals("otherProgram", otherProgram.getTitle());
        assertTrue(program1.isEnabled());
        assertTrue(otherProgram.isEnabled());
        assertTrue(program1.getRequireProjectDefinition());
        assertTrue(otherProgram.getRequireProjectDefinition());

        assertThat(
                program1.getInstances(),
                contains(equalTo(new ProgramInstance().withIdentifier("0009").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withId("F+++++").withDisplayName("Full-time"))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23).toDate()).withApplicationDeadline(new LocalDate(2014, 9, 15).toDate())
                        .withEnabled(true))));

        assertThat(
                otherProgram.getInstances(),
                contains(equalTo(new ProgramInstance().withIdentifier("0014").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withId("F+++++").withDisplayName("Full-time"))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23).toDate()).withApplicationDeadline(new LocalDate(2014, 9, 15).toDate())
                        .withEnabled(true))));

        importedEntityFeed.setLocation("reference_data/conflicts/programs/updatedPrograms.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, programService.getAllPrograms().size());
        program1 = programService.getProgramByCode("1");
        otherProgram = programService.getProgramByCode("99");
        assertEquals("new_program1", program1.getTitle());
        assertEquals("otherProgram", otherProgram.getTitle());
//        assertTrue(program1.isEnabled());
//        assertFalse(otherProgram.isEnabled());
        assertFalse(program1.getRequireProjectDefinition());
        assertTrue(otherProgram.getRequireProjectDefinition());
        
        assertThat(
                program1.getInstances(),
                containsInAnyOrder(
                        equalTo(new ProgramInstance().withIdentifier("0009").withAcademicYear("2013")
                                .withStudyOption(new StudyOption().withId("F+++++").withDisplayName("Full-time"))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23).toDate()).withApplicationDeadline(new LocalDate(2014, 9, 15).toDate())
                                .withEnabled(true)),
                        equalTo(new ProgramInstance().withIdentifier("0008").withAcademicYear("2014")
                                .withStudyOption(new StudyOption().withId("P+++++").withDisplayName("Fart-time"))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23).toDate()).withApplicationDeadline(new LocalDate(2014, 9, 15).toDate())
                                .withEnabled(true))));

        assertThat(otherProgram.getInstances(), contains(equalTo(new ProgramInstance().withIdentifier("0014").withAcademicYear("2013")
                .withStudyOption(new StudyOption().withId("F+++++").withDisplayName("Full-time"))
                .withApplicationStartDate(new LocalDate(2013, 9, 23).toDate()).withApplicationDeadline(new LocalDate(2014, 9, 15).toDate())
                .withEnabled(false))));
    }

    @Test
    public void testConflictsInImport() throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(ImportedEntityType.DISABILITY);
        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/initialDisabilities.xml");

        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability1", importedEntityService.getByCode(Disability.class, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeName.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeCode.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "1").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/removeDisability.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "1").isEnabled());
        assertFalse(importedEntityService.getByCode(Disability.class, "99").isEnabled());
    }

}
