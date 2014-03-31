package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;

import au.com.bytecode.opencsv.CSVWriter;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.exporters.PorticoExportService;
import com.zuehlke.pgadmissions.services.exporters.TransferListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/testPorticoIntegrationContext.xml"})
@TransactionConfiguration(defaultRollback = true)
@Ignore
public class PorticoWebServicePhase2IT {

    private static final String TEST_REPORT_FILENAME = "PorticoWebServicePhase2IT.csv";
    
    @Autowired
    private PorticoExportService uclExportService;
    
    @Autowired
    private ApplicationFormService applicationsService;
    
    @Autowired 
    private SessionFactory sessionFactory;
    
    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    private CSVWriter writer;
    
    private List<String> csvEntries;
    
    @Before
    public void prepare() throws IOException {
        writer = new CSVWriter(new FileWriter(TEST_REPORT_FILENAME, true), ',');
        csvEntries = new ArrayList<String>();
    }
    
    @After
    public void finish() throws IOException {
        writer.writeNext(csvEntries.toArray(new String[]{}));
        writer.close();
        csvEntries.clear();
    }
    
    @Test
    @DirtiesContext
    @Transactional
    public void sendApplications() throws IOException {
        List<String> applications = new ArrayList<String>(Arrays.asList(
                "RRDEENSING01-2013-000087",  "RRDEENSING01-2013-000088", "RRDEENSING01-2013-000089",
                "TMRCOMSWEB01-2013-000010", "TMRCOMSWEB01-2013-000011", "TMRCOMSWEB01-2013-000012", 
                "TMRMSISING01-2013-000101", "TMRMSISING01-2013-000102", "TMRMSISING01-2013-000103",
                "TMRMSISING01-2013-000104", "RRDCENSING01-2013-000049", "RRDCENSING01-2013-000050",
                "RRDMECSING01-2013-000043", "RRDMECSING01-2013-000044", "RRDMECSING01-2013-000045"));
        
        for (String appNumber : applications) {
            ApplicationForm form = applicationsService.getByApplicationNumber(appNumber);
            ApplicationFormTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(form);
            selectReferres(form);
            try {
                uclExportService.sendToPortico(form, applicationFormTransfer, new CsvTransferListener());
            } catch (Exception e) {
                // do nothing
            } finally {
                finish();
            }
        }
    }
    
    private void selectReferres(ApplicationForm form) {
        for (Referee referee : form.getReferees()) {
            referee.setSendToUCL(true);
        }
        applicationsService.save(form);
        sessionFactory.getCurrentSession().refresh(form);
    }
    
    private class CsvTransferListener implements TransferListener {
        @Override
        public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
            request.getApplication().getCourseApplication().setExternalApplicationID(request.getApplication().getCourseApplication().getExternalApplicationID() + "-1");
            Marshaller marshaller = webServiceTemplate.getMarshaller();
            try {
                marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
            } catch (Exception e) {
                Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
            }
        }

        @Override
        public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
            if (response != null) {
                csvEntries.add(response.getReference().getApplicantID());
                csvEntries.add(response.getReference().getApplicationID());
                csvEntries.add("null");
            } else {
                csvEntries.add("null");
                csvEntries.add("null");
                csvEntries.add("null");
            }
        }

        @Override
        public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from web service [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferStarted(ApplicationForm form) {
        }

        @Override
        public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from SFTP upload [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
            csvEntries.add(zipFileName);
            csvEntries.add(transfer.getUclUserIdReceived());
            csvEntries.add(transfer.getUclBookingReferenceReceived());
            csvEntries.add("null");
        }
    }
}
