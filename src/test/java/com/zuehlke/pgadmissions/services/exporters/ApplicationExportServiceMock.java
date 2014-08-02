package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Application;

public class ApplicationExportServiceMock extends ApplicationExportService {

    private final HashMap<Application, ApplicationExportRequest> exportRequests = Maps.newHashMap();

    @Override
    public void exportUclApplications() {
        List<Application> applications = getUclApplicationsForExport();
        for (Application application : applications) {
            OutputStream outputStream = null;
            try {
                SubmitAdmissionsApplicationRequest dataExportRequest = buildDataExportRequest(application);

                byte[] documentExportRequest = null;
                outputStream = buildDocumentExportRequest(application, application.getCode(), new ByteArrayOutputStream());
                outputStream.write(documentExportRequest);

                ApplicationExportRequest exportRequest = new ApplicationExportRequest().withDataExportRequest(dataExportRequest). //
                        withDocumentExportRequest(documentExportRequest);
                exportRequests.put(application, exportRequest);
            } catch (Exception e) {
                throw new Error(e);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    public void verify() {
        List<Application> applications = getUclApplicationsForExport();
        for (Application application : applications) {
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
        // TODO: assertions for the content of the SOAP request
    }

    private void verifyDocumentExportRequest(Application application, byte[] documentExportRequest) {
        assertNotNull(documentExportRequest);
        // TODO: assertions for the content of the zip folder
    }

    private class ApplicationExportRequest {

        private SubmitAdmissionsApplicationRequest dataExportRequest;

        private byte[] documentExportRequest;

        public SubmitAdmissionsApplicationRequest getDataExportRequest() {
            return dataExportRequest;
        }

        public byte[] getDocumentExportRequest() {
            return documentExportRequest;
        }

        public ApplicationExportServiceMock.ApplicationExportRequest withDataExportRequest(SubmitAdmissionsApplicationRequest dataExportRequest) {
            this.dataExportRequest = dataExportRequest;
            return this;
        }

        public ApplicationExportServiceMock.ApplicationExportRequest withDocumentExportRequest(byte[] documentExportRequest) {
            this.documentExportRequest = documentExportRequest;
            return this;
        }

    }

}
