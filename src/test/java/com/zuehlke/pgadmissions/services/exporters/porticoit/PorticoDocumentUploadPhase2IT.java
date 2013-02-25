package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;

import au.com.bytecode.opencsv.CSVWriter;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.exporters.PorticoAttachmentsZipCreator;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;
import com.zuehlke.pgadmissions.services.exporters.TransferListener;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/testPorticoIntegrationContext.xml"})
@TransactionConfiguration(defaultRollback = true)
@Ignore
public class PorticoDocumentUploadPhase2IT {

    private static final String TEST_REPORT_FILENAME = "PorticoDocumentUploadPhase2IT.csv";

    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    @Autowired
    private ApplicationsService applicationsService;
    
    @Autowired
    private UclExportService uclExportService;
    
    @Autowired
    private PdfDocumentBuilder pdfDocumentBuilder;
    
    private CSVWriter writer;
    
    private List<String> csvEntries;
    
    private ApplicationForm applicationForm;

    private Resource damagedPdf; 
    
    
    @Before
    public void prepare() throws IOException {
        writer = new CSVWriter(new FileWriter(TEST_REPORT_FILENAME, true), ',');
        csvEntries = new ArrayList<String>();
        damagedPdf = new ClassPathResource("/pdf/damaged.pdf");
    }
    
    @After
    public void finish() throws IOException {
        writer.writeNext(csvEntries.toArray(new String[]{}));
        writer.close();
    }
    
    // ----------------------------------------------------------------------------------
    // RRDCIVSGEO01-2012-000111
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void missingResearchProposal1ApplicationFilenameForValidResearchProposal() {
        csvEntries.add("Missing researchProposal.1.applicationFilename for valid research proposal document in the document upload package.");
        applicationForm = applicationsService.getApplicationByApplicationNumber("RRDCIVSGEO01-2012-000111");
        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder) {
            @Override
            protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
                Document personalStatement = applicationForm.getPersonalStatement();
                if (personalStatement != null) {
                    String filename = getRandomFilename();
                    zos.putNextEntry(new ZipEntry(filename));
                    zos.write(personalStatement.getContent());
                    zos.closeEntry();
                    contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
                }
            }
        });
        
        uclExportService.sendToPortico(applicationForm, new CsvTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCIVSGEO01-2015-000111");
                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
                    detailsTp.getInstitution().setCode("UK0275");
                }
                
                Marshaller marshaller = webServiceTemplate.getMarshaller();
                try {
                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
                } catch (Exception e) {
                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
                }
            }
        });
    }
    
    // ----------------------------------------------------------------------------------
    // RRDCOMSING01-2012-000329
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void missingtranscript2applicationFilename() {
        csvEntries.add("Missing transcript.2.applicationFilename with valid corresponding document in the upload package.");
        applicationForm = applicationsService.getApplicationByApplicationNumber("RRDCOMSING01-2012-000329");
        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder) {
            @Override
            protected void addTranscriptFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
                List<Document> qualifications = applicationForm.getQualificationsToSendToPortico();
                String filename;
                
                switch (qualifications.size()) {
                case 2:
                    filename = getRandomFilename();
                    zos.putNextEntry(new ZipEntry(filename));
                    zos.write(qualifications.get(1).getContent());
                    zos.closeEntry();
                    contentsProperties.put("transcript.2.serverFilename", filename);
                case 1:
                    filename = getRandomFilename();
                    zos.putNextEntry(new ZipEntry(filename));
                    zos.write(qualifications.get(0).getContent());
                    zos.closeEntry();
                    contentsProperties.put("transcript.1.serverFilename", filename);
                    contentsProperties.put("transcript.1.applicationFilename", qualifications.get(0).getFileName());
                    break;
                case 0:
                    filename = getRandomFilename();
                    zos.putNextEntry(new ZipEntry(filename));
                    zos.write(pdfDocumentBuilder.buildTranscript1FromApprovalRoundComment(applicationForm));
                    zos.closeEntry();
                    contentsProperties.put("transcript.1.serverFilename", filename);
                    contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
                    break;
                }
            }
        });
        
        uclExportService.sendToPortico(applicationForm, new CsvTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCOMSING01-2015-000329");
                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
                    detailsTp.getInstitution().setCode("UK0275");
                }
                
                Marshaller marshaller = webServiceTemplate.getMarshaller();
                try {
                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
                } catch (Exception e) {
                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
                }
            }
        });
    }
    
    // ----------------------------------------------------------------------------------
    // RRDCOMSING01-2012-000329
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void noEnglishLanguageTestCertificate1InDocumentUpload() {
        csvEntries.add("No englishLanguageTestCertificate.1 in document upload.");
        applicationForm = applicationsService.getApplicationByApplicationNumber("RRDCOMSING01-2012-000329");
        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder) {
            @Override
            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
            }
        });
        
        uclExportService.sendToPortico(applicationForm, new CsvTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCOMSING01-2015-000329-1");
                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
                    detailsTp.getInstitution().setCode("UK0275");
                }
                
                Marshaller marshaller = webServiceTemplate.getMarshaller();
                try {
                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
                } catch (Exception e) {
                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
                }
            }
        });
    }
    
    // ----------------------------------------------------------------------------------
    // RRDSECSING01-2012-000202
    // ----------------------------------------------------------------------------------
    @Test
    @Transactional
    public void corruptedApplicationForm1InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile() {
        csvEntries.add("Corrupted applicationForm.1 in document upload with valid corresponding entry in document upload contents file (optional document).");
        applicationForm = applicationsService.getApplicationByApplicationNumber("RRDSECSING01-2012-000202");
        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder) {
            @Override
            protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
                String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
                String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
                zos.putNextEntry(new ZipEntry(serverfilename));
                zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
                zos.closeEntry();
                contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
                contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
            }
        });
        
        uclExportService.sendToPortico(applicationForm, new CsvTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                request.getApplication().getCourseApplication().setExternalApplicationID("RRDSECSING01-2015-000202");
                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
                    detailsTp.getInstitution().setCode("UK0275");
                }
                
                Marshaller marshaller = webServiceTemplate.getMarshaller();
                try {
                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
                } catch (Exception e) {
                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
                }
            }
        });
    }
    
    // ----------------------------------------------------------------------------------
    // RRDCOMSING01-2012-000282 with bogus institution code
    // ----------------------------------------------------------------------------------    
    @Test
    @Transactional
    public void validApplicationFormWithBogusInstitutionCode() {
        csvEntries.add("Bogus institution code");
        applicationForm = applicationsService.getApplicationByApplicationNumber("RRDCOMSING01-2012-000282");
        uclExportService.sendToPortico(applicationForm, new CsvTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCOMSING01-2015-000282");
                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
                    detailsTp.getInstitution().setCode("FOOBAR00123456");
                }
                
                Marshaller marshaller = webServiceTemplate.getMarshaller();
                try {
                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
                } catch (Exception e) {
                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
                }
            }
        });
    }
    
    private class CsvTransferListener implements TransferListener {
        @Override
        public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request) {
            Marshaller marshaller = webServiceTemplate.getMarshaller();
            try {
                marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
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
            Assert.fail(String.format("Received error from web service [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferStarted() {
        }

        @Override
        public void sftpTransferFailed(ApplicationFormTransferError error) {
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from SFTP upload [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferCompleted(String zipFileName, String applicantId, String bookingReferenceId) {
            csvEntries.add(zipFileName);
            csvEntries.add(applicantId);
            csvEntries.add(bookingReferenceId);
            csvEntries.add("null");
        }
    }
}
