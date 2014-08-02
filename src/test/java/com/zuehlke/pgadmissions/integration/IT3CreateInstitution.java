package com.zuehlke.pgadmissions.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.integration.helpers.InstitutionCreationHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT3CreateInstitution implements IPrismIntegrationTest {

    @Autowired
    private InstitutionCreationHelper institutionCreationHelper;
    
    @Autowired
    private IntegrationTestRunner integrationTestRunner;
    
    @Autowired
    private IT2ImportSystemReferenceData it2;
    
    @Test
    @Override
    public void run() throws Exception {
        integrationTestRunner.run(it2);
        institutionCreationHelper.verifyInstitutionCreation();
        integrationTestRunner.recordTestSuccess(this);
    }
    
}