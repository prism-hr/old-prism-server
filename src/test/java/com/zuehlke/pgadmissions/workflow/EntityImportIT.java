package com.zuehlke.pgadmissions.workflow;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.enums.PrismImportedEntityType;
import com.zuehlke.pgadmissions.domain.enums.PrismProgramType;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.services.*;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class EntityImportIT {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private SystemInitialisationIT systemInitialisationIT;

    @Autowired
    private SystemService systemService;

    @Test
    public void testImportData() throws Exception {
        systemInitialisationIT.testSystemInitialisation();
        Institution institution = createInstitution();
        testImportDisabilities(institution);
        testConflictsInProgramImport(institution);
    }

    public Institution createInstitution() {
        System system = systemService.getSystem();
        State institutionState = entityService.getByProperty(State.class, "id", PrismState.INSTITUTION_APPROVED);

        InstitutionDomicile institutionDomicile = new InstitutionDomicile().withCode("PL").withName("Poland").withEnabled(true);
        User user = new User().withEmail("jerzy@urban.pl").withFirstName("Jerzy").withLastName("Urban").withActivationCode("jurekjurektrzymajsie");
        Institution institution = new Institution().withDomicile(institutionDomicile).withName("Akademia Gorniczo-Hutnicza").withState(institutionState).withCode("AGH").withHomepage("www.agh.edu.pl").withSystem(system).withUser(user);
        entityService.save(institutionDomicile, user, institution);

        return institution;
    }

    public void testImportDisabilities(Institution institution) throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntityType.DISABILITY);
        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/initialDisabilities.xml");
        importedEntityFeed.setInstitution(institution);

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

    public void testConflictsInProgramImport(Institution institution) throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntityType.PROGRAM);
        importedEntityFeed.setLocation("reference_data/conflicts/programs/initialPrograms.xml");
        importedEntityFeed.setInstitution(institution);

        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, programService.getAllPrograms().size());
        Program program1 = programService.getProgramByCode("AGH-1");
        Program otherProgram = programService.getProgramByCode("AGH-99");
        assertEquals("MRes program1", program1.getTitle());
        assertSame(PrismProgramType.MRES, program1.getProgramType());
        assertEquals("Internship otherProgram", otherProgram.getTitle());
        assertSame(PrismProgramType.INTERNSHIP, otherProgram.getProgramType());
        assertTrue(program1.getRequireProjectDefinition());
        assertTrue(otherProgram.getRequireProjectDefinition());

        assertThat(
                program1.getProgramInstances(),
                contains(equalTo(new ProgramInstance().withIdentifier("0009").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withInstitution(institution).withCode("F+++++").withName("Full-time").withEnabled(true))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15))
                        .withEnabled(true))));

        assertThat(
                otherProgram.getProgramInstances(),
                contains(equalTo(new ProgramInstance().withIdentifier("0014").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withInstitution(institution).withCode("F+++++").withName("Full-time").withEnabled(true))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15))
                        .withEnabled(true))));

        importedEntityFeed.setLocation("reference_data/conflicts/programs/updatedPrograms.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, programService.getAllPrograms().size());
        program1 = programService.getProgramByCode("AGH-1");
        otherProgram = programService.getProgramByCode("AGH-99");
        assertEquals("MRes new_program1", program1.getTitle());
        assertSame(PrismProgramType.MRES, program1.getProgramType());
        assertEquals("Internship otherProgram", otherProgram.getTitle());
        assertSame(PrismProgramType.INTERNSHIP, otherProgram.getProgramType());
//        assertTrue(program1.isEnabled());
//        assertFalse(otherProgram.isEnabled());
        assertFalse(program1.getRequireProjectDefinition());
        assertTrue(otherProgram.getRequireProjectDefinition());

        assertThat(
                program1.getProgramInstances(),
                containsInAnyOrder(
                        equalTo(new ProgramInstance().withIdentifier("0009").withAcademicYear("2013")
                                .withStudyOption(new StudyOption().withInstitution(institution).withCode("F+++++").withName("Full-time").withEnabled(true))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15))
                                .withEnabled(true)),
                        equalTo(new ProgramInstance().withIdentifier("0008").withAcademicYear("2014")
                                .withStudyOption(new StudyOption().withInstitution(institution).withCode("P+++++").withName("Fart-time").withEnabled(true))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15))
                                .withEnabled(true))));

        assertThat(otherProgram.getProgramInstances(), contains(equalTo(new ProgramInstance().withIdentifier("0014").withAcademicYear("2013")
                .withStudyOption(new StudyOption().withInstitution(institution).withCode("F+++++").withName("Full-time").withEnabled(true))
                .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15))
                .withEnabled(false))));
    }

    public void testConflictsInInstitutionImport() throws Exception {
        Institution ucl = entityService.getByCode(Institution.class, "AGH");
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntityType.DOMICILE);
        importedEntityFeed.setLocation("xml/defaultEntities/domicile.xml");
        importedEntityFeed.setInstitution(ucl);

        entityImportService.importEntities(importedEntityFeed);
    }

}
