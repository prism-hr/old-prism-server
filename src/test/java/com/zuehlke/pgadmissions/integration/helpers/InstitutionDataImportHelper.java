package com.zuehlke.pgadmissions.integration.helpers;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.importers.AdvertCategoryImportService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import com.zuehlke.pgadmissions.services.importers.InstitutionDomicileImportService;

@Service
public class InstitutionDataImportHelper {

    @Autowired
    private EntityService entityService;

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private InstitutionDomicileImportService institutionDomicileImportService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private AdvertCategoryImportService opportunityCategoryImportService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StateService stateService;

    public void verifyEntityImport(Institution institution) throws DataImportException {
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

        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals("disability1", importedEntityService.getByCode(Disability.class, institution, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeName.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals("disability2", importedEntityService.getByCode(Disability.class, institution, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "0").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/changeCode.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals("disability2", importedEntityService.getByCode(Disability.class, institution, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "1").isEnabled());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());

        importedEntityFeed.setLocation("reference_data/conflicts/disabilities/removeDisability.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        assertEquals("disability2", importedEntityService.getByCode(Disability.class, institution, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, institution, "99").getName());
        assertTrue(importedEntityService.getByCode(Disability.class, institution, "1").isEnabled());
        assertFalse(importedEntityService.getByCode(Disability.class, institution, "99").isEnabled());
    }

    @SuppressWarnings("unchecked")
    public void verifyProgramImport(Institution institution) throws DataImportException {
        for (String code : new String[] { "AGH-1", "AGH-99" }) {
            Program program = programService.getProgramByCode(code);
            if (program != null) {
                for (ProgramStudyOption studyOption : program.getStudyOptions()) {
                    for (ProgramStudyOptionInstance instance : studyOption.getInstances()) {
                        entityService.delete(instance);
                    }
                    entityService.delete(studyOption);
                }
                entityService.delete(program);
            }
        }

        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(PrismImportedEntity.PROGRAM);
        importedEntityFeed.setLocation("reference_data/conflicts/programs/initialPrograms.xml");
        importedEntityFeed.setInstitution(institution);

        entityImportService.importReferenceEntities(importedEntityFeed);

        Program program1 = programService.getProgramByImportedCode(institution, "1");
        Program otherProgram = programService.getProgramByImportedCode(institution, "99");
        assertEquals("MRes program1", program1.getTitle());
        assertSame(PrismProgramType.POSTGRADUATE_STUDY, program1.getProgramType());
        assertEquals("Internship otherProgram", otherProgram.getTitle());
        assertSame(PrismProgramType.INTERNSHIP, otherProgram.getProgramType());
        assertTrue(program1.isRequireProjectDefinition());
        assertTrue(otherProgram.isRequireProjectDefinition());
        
        for ()
        

        assertThat(
                program1.getProgramInstances(),
                contains(equalTo(new ProgramStudyOptionInstance().withIdentifier("0009").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withInstitution(institution).withCode(PrismStudyOption.FULL_TIME.name()).withName("F+++++").withEnabled(true))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true))));

        assertThat(
                otherProgram.getProgramInstances(),
                contains(equalTo(new ProgramStudyOptionInstance().withIdentifier("0014").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withInstitution(institution).withCode(PrismStudyOption.FULL_TIME.name()).withName("F+++++").withEnabled(true))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true))));

        importedEntityFeed.setLocation("reference_data/conflicts/programs/updatedPrograms.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        program1 = programService.getProgramByImportedCode(institution, "1");
        otherProgram = programService.getProgramByImportedCode(institution, "99");
        assertEquals("MRes new_program1", program1.getTitle());
        assertSame(PrismProgramType.POSTGRADUATE_STUDY, program1.getProgramType());
        assertEquals("Internship otherProgram", otherProgram.getTitle());
        assertSame(PrismProgramType.INTERNSHIP, otherProgram.getProgramType());
        assertEquals(program1.getState().getId(), PrismState.PROGRAM_APPROVED);
        assertEquals(otherProgram.getState().getId(), PrismState.PROGRAM_APPROVED);
        assertFalse(program1.isRequireProjectDefinition());
        assertTrue(otherProgram.isRequireProjectDefinition());

        assertThat(
                program1.getProgramInstances(),
                containsInAnyOrder(
                        equalTo(new ProgramStudyOptionInstance().withIdentifier("0009").withAcademicYear("2013")
                                .withStudyOption(new StudyOption().withInstitution(institution).withCode(PrismStudyOption.FULL_TIME).withName("F+++++").withEnabled(true))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true)),
                        equalTo(new ProgramStudyOptionInstance().withIdentifier("0008").withAcademicYear("2013")
                                .withStudyOption(new StudyOption().withInstitution(institution).withCode(PrismStudyOption.PART_TIME).withName("P+++++").withEnabled(true))
                                .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(true))));

        assertThat(
                otherProgram.getProgramInstances(),
                contains(equalTo(new ProgramStudyOptionInstance().withIdentifier("0014").withAcademicYear("2013")
                        .withStudyOption(new StudyOption().withInstitution(institution).withCode(PrismStudyOption.FULL_TIME).withName("F+++++").withEnabled(true))
                        .withApplicationStartDate(new LocalDate(2013, 9, 23)).withApplicationDeadline(new LocalDate(2014, 9, 15)).withEnabled(false))));
    }

    public void verifyProductionDataImport(Institution institution) throws DataImportException {
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

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.GENDER);
        importedEntityFeed.setLocation("xml/defaultEntities/gender.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.INSTITUTION);
        importedEntityFeed.setLocation("reference_data/conflicts/institutions/institution.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);

        importedEntityFeed.setImportedEntityType(PrismImportedEntity.PROGRAM);
        importedEntityFeed.setLocation("reference_data/2014-05-08/program.xml");
        entityImportService.importReferenceEntities(importedEntityFeed);
    }

    @Transactional
    public void verifyImportedProgramInitialisation() {
        List<Program> programs = programService.getPrograms();
        for (Program program : programs) {
            int enabledOptions = 0;
            
            for (ProgramStudyOption studyOption : program.getStudyOptions()) {
                LocalDate startDateExpected = null;
                LocalDate closeDateExpected = null;

                LocalDate startDateActual = studyOption.getApplicationStartDate();
                LocalDate closeDateActual = studyOption.getApplicationCloseDate();

                ProgramStudyOptionInstance earliestEnabledInstance = null;
                for (ProgramStudyOptionInstance instance : studyOption.getInstances()) {
                    LocalDate startDateInstance = instance.getApplicationStartDate();
                    LocalDate closeDateInstance = instance.getApplicationCloseDate();

                    earliestEnabledInstance = instance.isEnabled() && earliestEnabledInstance == null
                            || (startDateExpected == null || startDateInstance.isBefore(startDateExpected)) ? instance : earliestEnabledInstance;

                    startDateExpected = startDateExpected == null || startDateExpected.isAfter(startDateInstance) ? startDateInstance : startDateExpected;
                    closeDateExpected = closeDateExpected == null || closeDateExpected.isBefore(closeDateInstance) ? closeDateInstance : closeDateExpected;
                }
                
                assertEquals(startDateActual, startDateExpected);
                assertEquals(closeDateActual, closeDateExpected);
                
                if (studyOption.isEnabled()) {
                    assertEquals(studyOption.getDefaultStartDate(), earliestEnabledInstance.getApplicationCloseDate());
                    enabledOptions++;
                } else {
                    assertTrue(studyOption.getDefaultStartDate() == null);
                }
            }

            User programUser = program.getUser();
            assertEquals(program.getInstitution().getUser(), program.getUser());
            assertTrue(roleService.hasUserRole(program, programUser, PrismRole.PROGRAM_ADMINISTRATOR));
            
            LocalDate dueDate = program.getDueDate();
            
            if (enabledOptions == 0) {
                LocalDate baseline = new LocalDate();
                assertTrue(baseline.equals(dueDate) || baseline.isAfter(dueDate));
            } else {
                assertEquals(dueDate, programService.getProgramClosureDate(program));
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
