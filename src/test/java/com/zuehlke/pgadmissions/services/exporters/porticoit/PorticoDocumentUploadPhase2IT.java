package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;

import au.com.bytecode.opencsv.CSVWriter;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.QualificationsTp;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.pdf.CombinedReferencesPdfBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.Transcript1PdfBuilder;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;
import com.zuehlke.pgadmissions.services.exporters.TransferListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/testPorticoIntegrationContext.xml"})
@TransactionConfiguration(defaultRollback = true)
@Ignore
public class PorticoDocumentUploadPhase2IT {

    private static final String TEST_REPORT_FILENAME = "PorticoDocumentUploadPhase2IT.csv";

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    @Autowired
    private ApplicationService applicationsService;
    
    @Autowired
    private ExportService uclExportService;
    
    @Autowired
    private PdfDocumentBuilder pdfDocumentBuilder;
    
    @Autowired
    private CombinedReferencesPdfBuilder combinedReferenceBuilder;
    
    @Autowired
    private Transcript1PdfBuilder transcriptBuilder;
    
    private CSVWriter writer;
    
    private List<String> csvEntries;
    
    private Application applicationForm;

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
    
//    // ----------------------------------------------------------------------------------
//    // RRDCIVSGEO01-2012-000111
//    // ----------------------------------------------------------------------------------
//    @Test
//    @Transactional
//    public void missingResearchProposal1ApplicationFilenameForValidResearchProposal() throws ExportServiceException {
//        csvEntries.add("Missing researchProposal.1.applicationFilename for valid research proposal document in the document upload package.");
//        applicationForm = applicationsService.getByApplicationNumber("RRDCIVSGEO01-2012-000111");
//        addReferres(applicationForm);
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document personalStatement = applicationForm.getPersonalStatement();
//                if (personalStatement != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(personalStatement.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
//                }
//            }
//        });
//        
//        ApplicationFormTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
//        uclExportService.sendToPortico(applicationForm, applicationFormTransfer, new CsvTransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
//                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCIVSGEO01-2016-000111");
//                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
//                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
//                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
//                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
//                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
//                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
//                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
//                    detailsTp.getInstitution().setCode("UK0275");
//                }
//                
//                Marshaller marshaller = webServiceTemplate.getMarshaller();
//                try {
//                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
//                } catch (Exception e) {
//                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
//                }
//            }
//        });
//    }
//    
//    // ----------------------------------------------------------------------------------
//    // RRDCOMSING01-2012-000329
//    // ----------------------------------------------------------------------------------
//    @Test
//    @Transactional
//    public void missingtranscript2applicationFilename() throws ExportServiceException {
//        csvEntries.add("Missing transcript.2.applicationFilename with valid corresponding document in the upload package.");
//        applicationForm = applicationsService.getByApplicationNumber("RRDCOMSING01-2012-000329");
//        addReferres(applicationForm);
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addTranscriptFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<Document> qualifications = applicationForm.getQualificationsToSendToPortico();
//                String filename;
//                
//                switch (qualifications.size()) {
//                case 2:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(qualifications.get(1).getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.2.serverFilename", filename);
//                case 1:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(qualifications.get(0).getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", filename);
//                    contentsProperties.put("transcript.1.applicationFilename", qualifications.get(0).getFileName());
//                    break;
//                case 0:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(transcriptBuilder.build(applicationForm));
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", filename);
//                    contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
//                    break;
//                }
//            }
//        });
//        
//        ApplicationFormTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
//        uclExportService.sendToPortico(applicationForm, applicationFormTransfer, new CsvTransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
//                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCOMSING01-2016-000329");
//                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
//                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
//                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
//                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
//                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
//                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
//                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
//                    detailsTp.getInstitution().setCode("UK0275");
//                }
//                
//                Marshaller marshaller = webServiceTemplate.getMarshaller();
//                try {
//                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
//                } catch (Exception e) {
//                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
//                }
//            }
//        });
//    }
//    
//    // ----------------------------------------------------------------------------------
//    // RRDCOMSING01-2012-000329
//    // ----------------------------------------------------------------------------------
//    @Test
//    @Transactional
//    public void noEnglishLanguageTestCertificate1InDocumentUpload() throws ExportServiceException {
//        csvEntries.add("No englishLanguageTestCertificate.1 in document upload.");
//        applicationForm = applicationsService.getByApplicationNumber("RRDCOMSING01-2012-000329");
//        addReferres(applicationForm);
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//            }
//        });
//        
//        ApplicationFormTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
//        uclExportService.sendToPortico(applicationForm, applicationFormTransfer, new CsvTransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
//                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCOMSING01-2016-000329-1");
//                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
//                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
//                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
//                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
//                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
//                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
//                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
//                    detailsTp.getInstitution().setCode("UK0275");
//                }
//                
//                Marshaller marshaller = webServiceTemplate.getMarshaller();
//                try {
//                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
//                } catch (Exception e) {
//                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
//                }
//            }
//        });
//    }
//    
//    // ----------------------------------------------------------------------------------
//    // RRDSECSING01-2012-000202
//    // ----------------------------------------------------------------------------------
//    @Test
//    @Transactional
//    public void corruptedApplicationForm1InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile() throws ExportServiceException {
//        csvEntries.add("Corrupted applicationForm.1 in document upload with valid corresponding entry in document upload contents file (optional document).");
//        applicationForm = applicationsService.getByApplicationNumber("RRDSECSING01-2012-000202");
//        addReferres(applicationForm);
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                zos.closeEntry();
//                contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
//                contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
//            }
//        });
//        
//        ApplicationFormTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
//        uclExportService.sendToPortico(applicationForm, applicationFormTransfer, new CsvTransferListener() {
//            @Override
//            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
//                request.getApplication().getCourseApplication().setExternalApplicationID("RRDSECSING01-2016-000202");
//                request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
//                request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
//                request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
//                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
//                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
//                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
//                for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
//                    detailsTp.getInstitution().setCode("UK0275");
//                }
//                
//                Marshaller marshaller = webServiceTemplate.getMarshaller();
//                try {
//                    marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
//                } catch (Exception e) {
//                    Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
//                }
//            }
//        });
//    }
    
    // ----------------------------------------------------------------------------------
    // RRDCOMSING01-2012-000282 with bogus institution code
    // ----------------------------------------------------------------------------------    
    @Test
    @Transactional
    public void validApplicationFormWithBogusInstitutionCode() throws ExportServiceException {
        csvEntries.add("Bogus institution code");
        applicationForm = applicationsService.getByApplicationNumber("RRDCOMSING01-2012-000282");
        addReferres(applicationForm);
        
        ApplicationTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
        uclExportService.sendToPortico(applicationForm, applicationFormTransfer, new CsvTransferListener() {
            @Override
            public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
                request.getApplication().getCourseApplication().setExternalApplicationID("RRDCOMSING01-2016-000282");
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
    
    private void addReferres(Application form) {
        int numberOfReferences = 0;
        for (Referee referee : form.getReferees()) {
            if (referee.getComment() != null) {
                referee.setSendToUCL(true);
                numberOfReferences++;
            }
            
            if (numberOfReferences == 2) {
                break;
            }
        }
        applicationsService.save(form);
        sessionFactory.getCurrentSession().refresh(form);
    }
    
    private class CsvTransferListener implements TransferListener {
        @Override
        public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, Application form) {
            Marshaller marshaller = webServiceTemplate.getMarshaller();
            try {
                marshaller.marshal(request, new StreamResult(new File("request_" + request.getApplication().getCourseApplication().getExternalApplicationID() + ".txt")));
            } catch (Exception e) {
                Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
            }
        }

        @Override
        public void webServiceCallCompleted(AdmissionsApplicationResponse response, Application form) {
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
        public void webServiceCallFailed(Throwable throwable, ApplicationTransferError error, Application form) {
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from web service [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferStarted(Application form) {
        }

        @Override
        public void sftpTransferFailed(Throwable throwable, ApplicationTransferError error, Application form) {
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add("null");
            csvEntries.add(error.getDiagnosticInfo());
            Assert.fail(String.format("Received error from SFTP upload [reason=%s]", error.getDiagnosticInfo()));
        }

        @Override
        public void sftpTransferCompleted(String zipFileName, ApplicationTransfer transfer) {
            csvEntries.add(zipFileName);
            csvEntries.add(transfer.getExternalApplicantReference());
            csvEntries.add(transfer.getExternalTransferReference());
            csvEntries.add("null");
        }
    }
}
