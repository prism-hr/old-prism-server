package com.zuehlke.pgadmissions.services;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.application.Application;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

@Service
@Transactional
public class ApplicationExportServiceDevelopment extends ApplicationExportService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationExportServiceDevelopment.class);

    protected final HashMap<Application, ApplicationExportRequest> exportRequests = Maps.newHashMap();

    @Override
    public void submitExportRequest(Integer applicationId) throws Exception {
        Application application = applicationService.getById(applicationId);

        OutputStream outputStream = null;
        SubmitAdmissionsApplicationRequest dataExportRequest = null;

        try {
            localize(application);

            String exportId = applicationService.getApplicationExportReference(application);
            if (exportId == null) {
                dataExportRequest = buildDataExportRequest(application);
            }

            outputStream = buildDocumentExportRequest(application, application.getCode(), new ByteArrayOutputStream());
            ByteArrayOutputStream byteOutputStream = (ByteArrayOutputStream) outputStream;

            ApplicationExportRequest exportRequest = new ApplicationExportRequest().withDataExportRequest(dataExportRequest). //
                    withDocumentExportRequest(byteOutputStream.toByteArray());
            exportRequests.put(application, exportRequest);

            executeExportAction(application, dataExportRequest, "TEST EXPORT", "TEST EXPORT USER ID", null);
        } catch (RuntimeException e) {
            log.error("Could not export application", e);
        } catch (Exception e) {
            executeExportAction(application, dataExportRequest, null, null, ExceptionUtils.getStackTrace(e));
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
