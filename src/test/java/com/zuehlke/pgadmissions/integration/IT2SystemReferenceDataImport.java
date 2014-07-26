package com.zuehlke.pgadmissions.integration;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import com.zuehlke.pgadmissions.services.importers.InstitutionDomicileImportService;
import com.zuehlke.pgadmissions.services.importers.OpportunityCategoryImportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT2SystemReferenceDataImport {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionDomicileImportService institutionDomicileImportService;

    @Autowired
    private OpportunityCategoryImportService opportunityCategoryImportService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private IT1SystemInitialisation it1SystemInitialisation;

    @Autowired
    private SystemService systemService;

    @Test
    public void testImportData() throws Exception {
        it1SystemInitialisation.testSystemInitialisation();
        testImportInstitutionDomiciles();
        testOpportunityCategories();
        Institution institution = createInstitution();
        testImportDisabilities(institution);
        testConflictsInProgramImport(institution);
        importRemainingEntities(institution);
    }

    private void testImportInstitutionDomiciles() throws Exception {
        institutionDomicileImportService.importEntities("reference_data/isoCountryCodes/iso_country_codes.xml");
        institutionDomicileImportService.importEntities("xml/iso/iso_country_codes.xml");

        InstitutionDomicile poland = entityService.getByProperty(InstitutionDomicile.class, "id", "PL");
        assertEquals("Poland", poland.getName());
        assertTrue(poland.isEnabled());

        InstitutionDomicileRegion wojBielskie = entityService.getByProperty(InstitutionDomicileRegion.class, "id", "PL-BIELSKO");
        assertEquals("Bielskie", wojBielskie.getName());
        assertFalse(wojBielskie.isEnabled());
    }

    private void testOpportunityCategories() throws Exception {
        opportunityCategoryImportService.importEntities("xml/opportunityCategories/soc2010.csv");
    }

    public Institution createInstitution() {
        com.zuehlke.pgadmissions.domain.System system = systemService.getSystem();
        State institutionState = entityService.getByProperty(State.class, "id", PrismState.INSTITUTION_APPROVED);

        InstitutionDomicile poland = entityService.getByProperty(InstitutionDomicile.class, "id", "PL");
        User user = new User().withEmail("jerzy@urban.pl").withFirstName("Jerzy").withLastName("Urban").withActivationCode("jurekjurektrzymajsie");
        Institution institution = new Institution().withName("Akademia Gorniczo-Hutnicza").withState(institutionState).withHomepage("www.agh.edu.pl")
                .withSystem(system).withUser(user).withCreatedTimestamp(new DateTime()).withUpdatedTimestamp(new DateTime())
                .withAddress(new InstitutionAddress().withCountry(poland));
        entityService.getOrCreate(user);
        return entityService.getOrCreate(institution);
    }

    public void testImportDisabilities(Institution institution) throws Exception {
        // clean-up imported disabilities
        for (String code : new String[] { "1", "99" }) {
            Disability disability = entityService.getByCode(Disability.class, code);
            if (disability != null) {
                entityService.delete(disability);
            }
        }

        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntity.DISABILITY);
        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/initialDisabilities.xml");
        importedEntityFeed.setInstitution(institution);

        entityImportService.importEntities(importedEntityFeed);

        assertEquals("disability1", importedEntityService.getByCode(Disability.class, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeName.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeCode.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "1").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/removeDisability.xml");
        entityImportService.importEntities(importedEntityFeed);

        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, "1").isEnabled());
        assertFalse(importedEntityService.getByCode(Disability.class, "99").isEnabled());
    }

    @SuppressWarnings("unchecked")
    public void testConflictsInProgramImport(Institution institution) throws Exception {

        // clean-up imported programs
        for (String code : new String[] { "AGH-1", "AGH-99" }) {
            Program program = programService.getProgramByCode(code);
            if (program != null) {
                for (ProgramInstance programInstance : program.getProgramInstances()) {
                    entityService.delete(programInstance);
                }
                entityService.delete(program);
            }
        }

        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntity.PROGRAM);
        importedEntityFeed.setLocation("reference_data/conflicts/programs/initialPrograms.xml");
        importedEntityFeed.setInstitution(institution);

        entityImportService.importEntities(importedEntityFeed);

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
        entityImportService.importEntities(importedEntityFeed);

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

    private void importRemainingEntities(Institution institution) throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setInstitution(institution);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.COUNTRY);
        importedEntityFeed.setLocation("xml/defaultEntities/country.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.DISABILITY);
        importedEntityFeed.setLocation("xml/defaultEntities/disability.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.DOMICILE);
        importedEntityFeed.setLocation("xml/defaultEntities/domicile.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.ETHNICITY);
        importedEntityFeed.setLocation("xml/defaultEntities/ethnicity.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.NATIONALITY);
        importedEntityFeed.setLocation("xml/defaultEntities/nationality.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.QUALIFICATION_TYPE);
        importedEntityFeed.setLocation("xml/defaultEntities/qualificationType.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.REFERRAL_SOURCE);
        importedEntityFeed.setLocation("xml/defaultEntities/sourceOfInterest.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.FUNDING_SOURCE);
        importedEntityFeed.setLocation("xml/defaultEntities/fundingSource.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.LANGUAGE_QUALIFICATION_TYPE);
        importedEntityFeed.setLocation("xml/defaultEntities/languageQualificationType.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.TITLE);
        importedEntityFeed.setLocation("xml/defaultEntities/title.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.INSTITUTION);
        importedEntityFeed.setLocation("reference_data/conflicts/institutions/institution.xml");
        entityImportService.importEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.PROGRAM);
        importedEntityFeed.setLocation("reference_data/2014-05-08/program.xml");
        entityImportService.importEntities(importedEntityFeed);
    }

}
