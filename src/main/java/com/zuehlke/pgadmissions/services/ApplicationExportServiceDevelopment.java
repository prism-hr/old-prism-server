package com.zuehlke.pgadmissions.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Application;

public class ApplicationExportServiceDevelopment extends ApplicationExportService {

    protected final HashMap<Application, ApplicationExportRequest> exportRequests = Maps.newHashMap();

    @Override
    public void export(Application application) {
        OutputStream outputStream = null;
        try {
            logger.info("Building data export request for application: " + application.getCode());
            SubmitAdmissionsApplicationRequest dataExportRequest = buildDataExportRequest(application);

            logger.info("Building document export request for application: " + application.getCode());
            outputStream = buildDocumentExportRequest(application, application.getCode(), new ByteArrayOutputStream());
            ByteArrayOutputStream byteOutputStream = (ByteArrayOutputStream) outputStream; 

            ApplicationExportRequest exportRequest = new ApplicationExportRequest().withDataExportRequest(dataExportRequest). //
                    withDocumentExportRequest(byteOutputStream.toByteArray());
            exportRequests.put(application, exportRequest);
        } catch (Exception e) {
            throw new Error(e);
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
