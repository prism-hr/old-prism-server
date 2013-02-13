package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;

import au.com.bytecode.opencsv.CSVWriter;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.exporters.TransferListener;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/testPorticoIntegrationContext.xml"})
@TransactionConfiguration(defaultRollback = true)
@Ignore
public class PorticoWebServiceIT {

    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    @Autowired
    private ApplicationsService applicationsService;
    
    @Autowired
    private UclExportService uclExportService;

    @Autowired
    private SessionFactory sessionFactory;
    
    private static final String TEST_REPORT_FILENAME = "PorticoWebServiceIT.csv";

    private SecureRandom random = new SecureRandom();
    
    private List<String> csvEntries;
    
    private int sentApps = 0;

    private ApplicationForm applicationForm;

    private CSVWriter writer;
    
    private ValidApplicationFormBuilder applicationFormBuilder;
    
    @Before
    public void prepare() throws IOException {
        writer = new CSVWriter(new FileWriter(TEST_REPORT_FILENAME, true), ',');
        csvEntries = new ArrayList<String>();
        applicationFormBuilder = new ValidApplicationFormBuilder();
        applicationForm = applicationFormBuilder.build(sessionFactory);
    }
    
    @After
    public void finish() throws IOException {
        writer.writeNext(csvEntries.toArray(new String[]{}));
        writer.close();
    }

    @Test
    @Transactional
    public void withdrawnApplicationWithNoMatchAtTRAN() {
        csvEntries.add("Withdrawn application with no match at ‘tran’");
        
        applicationForm.getPersonalDetails().setLastName(getRandomString());
        applicationForm.getPersonalDetails().setFirstName(getRandomString());
        applicationForm.getPersonalDetails().setEmail(getRandomEmail());
        applicationForm.setStatus(ApplicationFormStatus.WITHDRAWN);
        
    }
    
    @Test
    @Transactional
    public void withdrawnApplicationWithNoMatchAtTRANResent() {
        csvEntries.add("Withdrawn application with no match at ‘tran’ resent");
    }
    
    @Test
    @Transactional
    public void withdrawnApplicationWithMatchAtTRANAndNoActiveUserIdentity_MUA() {
        csvEntries.add("Withdrawn application with match at ‘tran’ and no active user identity (MUA)");
    }
    
    @Test
    @Transactional
    public void withdrawnApplicationWithMatchAtTRANAndActiveUserIdentity_MUA() {
        csvEntries.add("Withdrawn application with match at ‘tran’ and active user identity (MUA)");
    }
    
    @Test
    @Transactional
    public void withdrawnApplicationWithActiveUserIdentity_MUA_knownToUclPrism() {
        csvEntries.add("Withdrawn application with active user identity (MUA) known to UCL Prism");
    }
    
    @Test
    @Transactional
    public void withdrawnUclPrismApplicationByApplicantWithACorrespondingFirstApplicationInProgressInTheUCLPorticoSystem() {
        csvEntries.add("Withdrawn UCL Prism application by applicant with a corresponding first application in progress in the UCL Portico system");
    }
    
    @Test
    @Transactional
    public void rejectedApplicationWithNoMatchAtTRAN() {
        csvEntries.add("Rejected application with no match at ‘tran’");
    }
    
    @Test
    @Transactional
    public void rejectedApplicationWithNoMatchAtTRANResent() {
        csvEntries.add("Rejected application with no match at ‘tran’ resent");
    }
    
    @Test
    @Transactional
    public void rejectedApplicationWithMatchAtTRANAndNoActiveUserIdentity_MUA() {
        csvEntries.add("Rejected application with match at ‘tran’ and no active user identity (MUA)");
    }
    
    @Test
    @Transactional
    public void rejectedApplicationWithMatchAtTRANAndActiveUserIdentity_MUA() {
        csvEntries.add("Rejected application with match at ‘tran’ and active user identity (MUA)");
    }
    
    @Test
    @Transactional
    public void rejectedApplicationWithActiveUserIdentity_MUA_knownToUCLPrism() {
        csvEntries.add("Rejected application with active user identity (MUA) known to UCL Prism");
    }
    
    @Test
    @Transactional
    public void rejectedUCLPrismApplicationByApplicantWithACorrespondingFirstApplicationInProgressInTheUCLPorticoSystem() {
        csvEntries.add("Rejected UCL Prism application by applicant with a corresponding first application in progress in the UCL Portico system");
    }
    
    @Test
    @Transactional
    public void approvedApplicationWithNoMatchAtTRAN() {
        csvEntries.add("Approved application with no match at ‘tran’");
    }
    
    @Test
    @Transactional
    public void approvedApplicationWithNoMatchAtTRANResent() {
        csvEntries.add("Approved application with no match at ‘tran’ resent");
    }
    
    @Test
    @Transactional
    public void approvedApplicationWithMatchAtTRANAndNoActiveUserIdentity_MUA() {
        csvEntries.add("Approved application with match at ‘tran’ and no active user identity (MUA)");
    }
    
    @Test
    @Transactional
    public void approvedApplicationWithMatchAtTRANAndActiveUserIdentity_MUA() {
        csvEntries.add("Approved application with match at ‘tran’ and active user identity (MUA)");
    }
    
    @Test
    @Transactional
    public void approvedApplicationWithActiveUserIdentity_MUA_knownToUCLPrism() {
        csvEntries.add("Approved application with active user identity (MUA) known to UCL Prism");
    }
    
    @Test
    @Transactional
    public void approvedUCLPrismApplicationByApplicantWithACorrespondingFirstApplicationInProgressInTheUCLPorticoSystem() {
        csvEntries.add("Approved UCL Prism application by applicant with a corresponding first application in progress in the UCL Portico system");
    }
    
    @Test
    @Transactional
    public void approvedUCLPrismApplicationByApplicantWithADuplicateApplicationInTheUCLPorticoSystem() {
        csvEntries.add("Approved UCL Prism application by applicant with a duplicate application in the UCL Portico system");
    }
    
    private String getRandomString() {
        return new BigInteger(128, random).toString(32);
    }
    
    private String getRandomEmail() {
        String randomStr = new BigInteger(32, random).toString(32);
        return randomStr + "@" + randomStr + ".com";
    }
    
    private void sendToPortico(ApplicationForm applicationForm) {
        uclExportService.sendToPortico(applicationForm, new CsvTransferListener());
    }
    
    private class CsvTransferListener implements TransferListener {
        
        @Override
        public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
            Marshaller marshaller = webServiceTemplate.getMarshaller();
            try {
                marshaller.marshal(request, new StreamResult(new File("request_" + applicationForm.getApplicationNumber() + ".txt")));
            } catch (Exception e) {
                Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
            }
        }

        @Override
        public void webServiceCallCompleted(AdmissionsApplicationResponse response) {
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
        public void webServiceCallFailed(ApplicationFormTransferError error) {
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from SFTP upload [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferStarted() {
        }

        @Override
        public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
            csvEntries.add(zipFileName);
            csvEntries.add(applicantId);
            csvEntries.add(bookingReferenceId);
            csvEntries.add("null");
        }

        @Override
        public void sftpTransferFailed(ApplicationFormTransferError error) {
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from SFTP upload [reason=%s]", error.getDiagnosticInfo()));
        }
    }
}
