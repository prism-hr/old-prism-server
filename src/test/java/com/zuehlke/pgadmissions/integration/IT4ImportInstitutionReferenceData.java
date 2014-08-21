package com.zuehlke.pgadmissions.integration;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.integration.helpers.InstitutionDataImportHelper;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.importers.AdvertCategoryImportService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import com.zuehlke.pgadmissions.services.importers.InstitutionDomicileImportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT4ImportInstitutionReferenceData implements IPrismIntegrationTest {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionDomicileImportService institutionDomicileImportService;

    @Autowired
    private AdvertCategoryImportService opportunityCategoryImportService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private InstitutionDataImportHelper referenceDataImportHelper;
    
    @Autowired
    private IT1InitialiseSystem it1;

    @Test
    @Override
    public void run() throws Exception {
        it1.run();
        Institution institution = createTestInstitution();
        referenceDataImportHelper.verifyEntityImport(institution);
        referenceDataImportHelper.verifyProgramImport(institution);
        referenceDataImportHelper.verifyProductionDataImport(institution);
        referenceDataImportHelper.verifyImportedProgramInitialisation();
    }

    private Institution createTestInstitution() {
        System system = systemService.getSystem();
        State institutionState = entityService.getByProperty(State.class, "id", PrismState.INSTITUTION_APPROVED);

        InstitutionDomicile poland = entityService.getByProperty(InstitutionDomicile.class, "id", "PL");
        InstitutionAddress address = new InstitutionAddress().withAddressLine1("test").withAddressTown("test").withCountry(poland);
        entityService.save(address);
        
        User user = new User().withEmail("jerzy@urban.pl").withFirstName("Jerzy").withLastName("Urban").withActivationCode("jurekjurektrzymajsie");
        Institution institution = new Institution().withTitle("University College London").withState(institutionState).withHomepage("www.agh.edu.pl")
                .withSystem(system).withUser(user).withCreatedTimestamp(new DateTime()).withUpdatedTimestamp(new DateTime())
                .withAddress(address).withUclInstitution(true).withDomicile(poland);
        entityService.getOrCreate(user);
        return entityService.getOrCreate(institution);
    }
    
}
