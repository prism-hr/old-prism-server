package com.zuehlke.pgadmissions.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.PassedTest;
import com.zuehlke.pgadmissions.services.EntityService;

@Service
public class IntegrationTestRunner {

    @Autowired
    private EntityService entityService;
    
    public <T extends IPrismIntegrationTest> void run(T integrationTest) throws Exception {
        String integrationTestId = integrationTest.getClass().getSimpleName();
        if (entityService.getById(PassedTest.class, integrationTestId) == null) {
            integrationTest.run();
        }
    }
    
    public <T extends IPrismIntegrationTest> void recordTestSuccess(T integrationTest) {
        String integrationTestId = integrationTest.getClass().getSimpleName();
        if (entityService.getById(PassedTest.class, integrationTestId) == null) {
            entityService.save(new PassedTest().withId(integrationTest.getClass().getSimpleName()));
        }
    }
    
}
