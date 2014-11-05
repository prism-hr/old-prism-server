package com.zuehlke.pgadmissions.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.application.Application;

@Service
@Transactional
public class ApplicationExportServiceDevelopment extends ApplicationExportService {

    protected final HashMap<Application, ApplicationExportRequest> exportRequests = Maps.newHashMap();

    @Override
    public void submitExportRequest(Integer applicationId) throws Exception {
        Application application = applicationService.getById(applicationId);
        OutputStream outputStream = null;
        try {
            LOGGER.info("Building data export request for application: " + application.getCode());
            SubmitAdmissionsApplicationRequest dataExportRequest = buildDataExportRequest(application);

            LOGGER.info("Building document export request for application: " + application.getCode());
            outputStream = buildDocumentExportRequest(application, application.getCode(), new ByteArrayOutputStream());
            ByteArrayOutputStream byteOutputStream = (ByteArrayOutputStream) outputStream;

            ApplicationExportRequest exportRequest = new ApplicationExportRequest().withDataExportRequest(dataExportRequest). //
                    withDocumentExportRequest(byteOutputStream.toByteArray());
            exportRequests.put(application, exportRequest);

            executeExportAction(application, "TEST EXPORT", "TEST EXPORT USER ID", null);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    protected static class ApplicationExportRequest {

        private SubmitAdmissionsApplicationRequest dataExportRequest;

        private byte[] documentExportRequest;

        public SubmitAdmissionsApplicationRequest getDataExportRequest() {
            return dataExportRequest;
        }

        public byte[] getDocumentExportRequest() {
            return documentExportRequest;
        }

        public ApplicationExportRequest withDataExportRequest(SubmitAdmissionsApplicationRequest dataExportRequest) {
            this.dataExportRequest = dataExportRequest;
            return this;
        }

        public ApplicationExportRequest withDocumentExportRequest(byte[] documentExportRequest) {
            this.documentExportRequest = documentExportRequest;
            return this;
        }

    }

}
