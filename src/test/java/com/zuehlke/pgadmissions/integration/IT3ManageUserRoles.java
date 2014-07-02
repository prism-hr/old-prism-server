package com.zuehlke.pgadmissions.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT3ManageUserRoles {

    @Autowired
    private IT2SystemReferenceDataImport it2SystemReferenceDataImport;

    @Test
    public void testManageUserRoles() throws Exception {
        it2SystemReferenceDataImport.testImportData();
    }
}
