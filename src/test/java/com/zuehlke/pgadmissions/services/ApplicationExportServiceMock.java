package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.application.Application;

public class ApplicationExportServiceMock extends ApplicationExportServiceDevelopment {

    public void verify() {
        for (Application application : exportRequests.keySet()) {
            assertTrue(exportRequests.containsKey(application));
            ApplicationExportRequest request = exportRequests.get(application);

            SubmitAdmissionsApplicationRequest dataExportRequest = request.getDataExportRequest();
            verifyDataExportRequest(application, dataExportRequest);

            byte[] documentExportRequest = request.getDocumentExportRequest();
            verifyDocumentExportRequest(application, documentExportRequest);
        }
    }

    private void verifyDataExportRequest(Application application, SubmitAdmissionsApplicationRequest dataExportRequest) {
        assertNotNull(dataExportRequest);
    }

    private void verifyDocumentExportRequest(Application application, byte[] documentExportRequest) {
        assertNotNull(documentExportRequest);
    }
    
}
