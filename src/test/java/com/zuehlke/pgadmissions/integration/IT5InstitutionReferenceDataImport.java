package com.zuehlke.pgadmissions.integration;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
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
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class IT5InstitutionReferenceDataImport {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private IT1SystemInitialisation systemInitialisationIT;

    @Autowired
    private SystemService systemService;

    @Test
    public void testImportData() throws Exception {
        systemInitialisationIT.testSystemInitialisation();
        Institution institution = createInstitution();
        testImportDisabilities(institution);
        testConflictsInProgramImport(institution);
        testImportInstitutionDomiciles();

        importRemainingEntities(institution);
    }

    public Institution createInstitution() {
        System system = systemService.getSystem();
        State institutionState = entityService.getByProperty(State.class, "id", PrismState.INSTITUTION_APPROVED);

        InstitutionDomicile institutionDomicile = new InstitutionDomicile().withName("Poland").withEnabled(true);
        User user = new User().withEmail("jerzy@urban.pl").withFirstName("Jerzy").withLastName("Urban").withActivationCode("jurekjurektrzymajsie");
        Institution institution = new Institution().withName("Akademia Gorniczo-Hutnicza").withState(institutionState).withHomepage("www.agh.edu.pl")
                .withSystem(system).withUser(user).withAddress(new InstitutionAddress().withCountry(institutionDomicile));
        entityService.save(institutionDomicile, user, institution);

        return institution;
    }

    public void testImportDisabilities(Institution institution) throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntity.DISABILITY);
        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/initialDisabilities.xml");
        importedEntityFeed.setInstitution(institution);

        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability1", importedEntityService.getByCode(Disability.class, institution, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeName.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, institution, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeCode.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, institution, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "1").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/removeDisability.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, institution, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "1").isEnabled());
        assertFalse(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());
    }

    @SuppressWarnings("unchecked")
    public void testConflictsInProgramImport(Institution institution) throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntity.PROGRAM);
        importedEntityFeed.setLocation("reference_data/conflicts/programs/initialPrograms.xml");
        importedEntityFeed.setInstitution(institution);

        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals(2, programService.getPrograms().size());
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
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true))));

        assertThat(
                otherProgram.getProgramInstances(),
                contains(equalTo(new ProgramInstance().withIdentifier("0014").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withInstitution(institution).withCode("F+++++").withName("Full-time").withEnabled(true))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true))));

        importedEntityFeed.setLocation("reference_data/conflicts/programs/updatedPrograms.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals(2, programService.getPrograms().size());
        program1 = programService.getProgramByCode("AGH-1");
        otherProgram = programService.getProgramByCode("AGH-99");
        assertEquals("MRes new_program1", program1.getTitle());
        assertSame(PrismProgramType.MRES, program1.getProgramType());
        assertEquals("Internship otherProgram", otherProgram.getTitle());
        assertSame(PrismProgramType.INTERNSHIP, otherProgram.getProgramType());
        // assertTrue(program1.isEnabled());
        // assertFalse(otherProgram.isEnabled());
        assertFalse(program1.getRequireProjectDefinition());
        assertTrue(otherProgram.getRequireProjectDefinition());

        assertThat(
                program1.getProgramInstances(),
                containsInAnyOrder(
                        equalTo(new ProgramInstance().withIdentifier("0009").withAcademicYear("2013")
                                .withStudyOption(new StudyOption().withInstitution(institution).withCode("F+++++").withName("Full-time").withEnabled(true))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true)),
                        equalTo(new ProgramInstance().withIdentifier("0008").withAcademicYear("2014")
                                .withStudyOption(new StudyOption().withInstitution(institution).withCode("P+++++").withName("Fart-time").withEnabled(true))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true))));

        assertThat(
                otherProgram.getProgramInstances(),
                contains(equalTo(new ProgramInstance().withIdentifier("0014").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withInstitution(institution).withCode("F+++++").withName("Full-time").withEnabled(true))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(false))));
    }

    private void testImportInstitutionDomiciles() {
    }

    private void importRemainingEntities(Institution institution) throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setInstitution(institution);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.COUNTRY);
        importedEntityFeed.setLocation("xml/defaultEntities/country.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.DISABILITY);
        importedEntityFeed.setLocation("xml/defaultEntities/disability.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.DOMICILE);
        importedEntityFeed.setLocation("xml/defaultEntities/domicile.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.ETHNICITY);
        importedEntityFeed.setLocation("xml/defaultEntities/ethnicity.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.NATIONALITY);
        importedEntityFeed.setLocation("xml/defaultEntities/nationality.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.QUALIFICATION_TYPE);
        importedEntityFeed.setLocation("xml/defaultEntities/qualificationType.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.REFERRAL_SOURCE);
        importedEntityFeed.setLocation("xml/defaultEntities/sourceOfInterest.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.FUNDING_SOURCE);
        importedEntityFeed.setLocation("xml/defaultEntities/fundingSource.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.LANGUAGE_QUALIFICATION_TYPE);
        importedEntityFeed.setLocation("xml/defaultEntities/languageQualificationType.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.TITLE);
        importedEntityFeed.setLocation("xml/defaultEntities/title.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.INSTITUTION);
        importedEntityFeed.setLocation("reference_data/conflicts/institutions/institution.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);
    }

}
