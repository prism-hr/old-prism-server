package com.zuehlke.pgadmissions.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.integration.helpers.SystemDataImportHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT2ImportSystemReferenceData implements IPrismIntegrationTest {

    @Autowired
    private SystemDataImportHelper systemDataImportHelper;
    
    @Autowired
    private IT1InitialiseSystem it1;
    
    @Test
    @Override
    public void run() throws Exception {
        it1.run();
        systemDataImportHelper.verifyInstitutionDomicileImport();
        systemDataImportHelper.verifyAdvertCategoryImport();
    }
    
}
