package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.ws.client.core.WebServiceTemplate;

import au.com.bytecode.opencsv.CSVWriter;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.pdf.CombinedReferencesPdfBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.Transcript1PdfBuilder;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/testPorticoIntegrationContext.xml"})
@TransactionConfiguration(defaultRollback = true)
@Ignore
public class PorticoDocumentUploadIT {

    private static final String TEST_REPORT_FILENAME = "PorticoDocumentUploadIT.csv";

    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    @Autowired
    private ApplicationFormService applicationsService;
    
    @Autowired
    private ExportService uclExportService;
    
    @Autowired
    private PdfDocumentBuilder pdfDocumentBuilder;
    
    @Autowired
    private CombinedReferencesPdfBuilder combinedReferenceBuilder;
    
    @Autowired
    private Transcript1PdfBuilder transcriptBuilder;
    
    private SecureRandom random = new SecureRandom();
    
    private CSVWriter writer;
    
    private List<String> csvEntries;
    
    private int sentApps = 0;

    private ApplicationForm applicationForm;

    private Resource validPdf; 

    private Resource damagedPdf; 
    
    private static Set<String> USED_APPLICATION_NUMBERS = new HashSet<String>();
    
    @Before
    public void prepare() throws IOException {
        writer = new CSVWriter(new FileWriter(TEST_REPORT_FILENAME, true), ',');
        csvEntries = new ArrayList<String>();
        validPdf = new ClassPathResource("/pdf/valid.pdf");
        damagedPdf = new ClassPathResource("/pdf/damaged.pdf");
    }
    
    @After
    public void finish() throws IOException {
        writer.writeNext(csvEntries.toArray(new String[]{}));
        writer.close();
    }
//    
//    @Test
//    @Transactional
//    public void missingDocumentUploadContentsFile() {
//        csvEntries.add("Missing document upload contents file.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            public void writeZipEntries(ApplicationForm applicationForm, String referenceNumber, OutputStream sftpOs) throws IOException, CouldNotCreateAttachmentsPack {
//                Properties contentsProperties = new Properties();
//                ZipOutputStream zos = null;
//                try {
//                    zos = new ZipOutputStream(sftpOs);
//                    addTranscriptFiles(applicationForm, referenceNumber, contentsProperties, zos);
//                    addReserchProposal(applicationForm, referenceNumber, contentsProperties, zos);
//                    addLanguageTestCertificate(applicationForm, referenceNumber, contentsProperties, zos);
//                    addCV(applicationForm, referenceNumber, contentsProperties, zos);
//                    addReferences(applicationForm, referenceNumber, contentsProperties, zos);
//                    addApplicationForm(applicationForm, referenceNumber, contentsProperties, zos);
//                    addMergedApplicationForm(applicationForm, referenceNumber, contentsProperties, zos);
//                } finally {
//                    IOUtils.closeQuietly(zos);
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void emptyDocumentUploadContentsFile() {
//        csvEntries.add("Empty document upload contents file.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.clear();
//                zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void unreadableDocumentUploadContentsFileSpecialCharacters() {
//        csvEntries.add("Unreadable document upload contents file. (Special Characters).");
//        final char END_OF_TEXT = 0x03;
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.put("applicationNumber", applicationForm.getApplicationNumber() + END_OF_TEXT);
//                contentsProperties.put("bookingReferenceNumber" + END_OF_TEXT, referenceNumber);
//                zos.putNextEntry(new ZipEntry(referenceNumber + "ContentsEND_OF_TEXT.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingBookingReferenceNumberInDocumentUploadContentsFile() {
//        csvEntries.add("Missing bookingReferenceNumber in document upload contents file.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.put("applicationNumber", applicationForm.getApplicationNumber());
//                zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void unrecognisedBookingReferenceNumberInDocumentUploadContentsFile() {
//        csvEntries.add("Wrong/unrecognised bookingReferenceNumber in document upload contents file (e.g. does not match with document upload filename).");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.put("applicationNumber", applicationForm.getApplicationNumber());
//                contentsProperties.put("bookingReferenceNumber", "sihdu8yeyghgd");
//                zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingApplicationNumberInDocumentUploadContentsFile() {
//        csvEntries.add("Missing applicationNumber in document upload contents file.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.put("bookingReferenceNumber", referenceNumber);
//                zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void unrecognisedApplicationNumberInDocumentUploadContentsFile() {
//        csvEntries.add("Wrong/unrecognised applicationNumber in document upload contents file.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.put("applicationNumber", "eu3847346347");
//                contentsProperties.put("bookingReferenceNumber", referenceNumber);
//                zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingResearchProposal1ApplicationFilenameForValidResearchProposal() {
//        csvEntries.add("Missing researchProposal.1.applicationFilename for valid research proposal document in the document upload package.");
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingResearchProposal1ServerFilenameForValidResearchProposal() {
//        csvEntries.add("Missing researchProposal.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document personalStatement = applicationForm.getPersonalStatement();
//                if (personalStatement != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(personalStatement.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("researchProposal.1.serverFilename", filename);
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingMergedApplication1ApplicationFilename() {
//        csvEntries.add("Missing mergedApplication.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingMergedApplication1serverFilename() {
//        csvEntries.add("Missing mergedApplication.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "MergedApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingCurriculumVitae1applicationFilename() {
//        csvEntries.add("Missing curriculumVitae.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document cv = applicationForm.getCv();
//                if (cv != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(cv.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("curriculumVitae.1.serverFilename", filename);
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingCurriculumVitae1serverFilename() {
//        csvEntries.add("Missing curriculumVitae.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document cv = applicationForm.getCv();
//                if (cv != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(cv.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("curriculumVitae.1.applicationFilename", cv.getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingtranscript1applicationFilename() {
//        csvEntries.add("Missing transcript.1.applicationFilename with valid corresponding document in the upload package.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
//                case 1:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(qualifications.get(0).getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", filename);
//                    break;
//                case 0:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(transcriptBuilder.build(applicationForm));
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", filename);
//                    break;
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingtranscript1serverFilename() {
//        csvEntries.add("Missing transcript.1.serverFilename with valid corresponding document in the upload package.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
//                case 1:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(qualifications.get(0).getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.applicationFilename", qualifications.get(0).getFileName());
//                    break;
//                case 0:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(transcriptBuilder.build(applicationForm));
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
//                    break;
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingtranscript2applicationFilename() {
//        csvEntries.add("Missing transcript.2.applicationFilename with valid corresponding document in the upload package.");
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingtranscript2serverFilename() {
//        csvEntries.add("Missing transcript.2.serverFilename with valid corresponding document in the upload package.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingApplicationForm1applicationFilename() {
//        csvEntries.add("Missing applicationForm.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true).includeAttachments(false), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingApplicationForm1serverFilename() {
//        csvEntries.add("Missing applicationForm.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true).includeAttachments(false), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingReference1applicationFilename() {
//        csvEntries.add("Missing reference.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingReference1serverFilename() {
//        csvEntries.add("Missing reference.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingReference2applicationFilename() {
//        csvEntries.add("Missing reference.2.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingReference2serverFilename() {
//        csvEntries.add("Missing reference.2.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingEnglishLanguageTestCertificate1applicationFilename() {
//        csvEntries.add("Missing englishLanguageTestCertificate.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                LanguageQualification languageQualification = applicationForm.getPersonalDetails().getLanguageQualification();
//                if (languageQualification != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(FileUtils.readFileToByteArray(validPdf.getFile()));
//                    zos.closeEntry();            
//                    contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
//                    contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", StringUtils.EMPTY);
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void missingEnglishLanguageTestCertificate1serverFilename() {
//        csvEntries.add("Missing englishLanguageTestCertificate.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                LanguageQualification languageQualification = applicationForm.getPersonalDetails().getLanguageQualification();
//                if (languageQualification != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(FileUtils.readFileToByteArray(validPdf.getFile()));
//                    zos.closeEntry();            
//                    contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", languageQualification.getProofOfAward().getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//        
//    @Test
//    @Transactional
//    public void wrongBookingReferenceNumberInDocumentUploadContentsFile() {
//        csvEntries.add("Wrong bookingReferenceNumber in document upload contents file.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.put("applicationNumber", applicationForm.getApplicationNumber());
//                contentsProperties.put("bookingReferenceNumber", StringUtils.EMPTY);
//                zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongApplicationNumberInDocumentUploadContentsFile() {
//        csvEntries.add("Wrong applicationNumber in document upload contents file.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                contentsProperties.put("applicationNumber", StringUtils.EMPTY);
//                contentsProperties.put("bookingReferenceNumber", referenceNumber);
//                zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
//                contentsProperties.store(zos, StringUtils.EMPTY);
//                zos.closeEntry();
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongResearchProposal1ApplicationFilenameForValidResearchProposal() {
//        csvEntries.add("Wrong researchProposal.1.applicationFilename for valid research proposal document in the document upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document personalStatement = applicationForm.getPersonalStatement();
//                if (personalStatement != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(personalStatement.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("researchProposal.1.serverFilename", StringUtils.EMPTY);
//                    contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongResearchProposal1ServerFilenameForValidResearchProposal() {
//        csvEntries.add("Wrong researchProposal.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document personalStatement = applicationForm.getPersonalStatement();
//                if (personalStatement != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(personalStatement.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("researchProposal.1.serverFilename", filename);
//                    contentsProperties.put("researchProposal.1.applicationFilename", StringUtils.EMPTY);
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongMergedApplication1ApplicationFilename() {
//        csvEntries.add("Wrong mergedApplication.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
//                contentsProperties.put("mergedApplication.1.applicationFilename", StringUtils.EMPTY);        
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongMergedApplication1serverFilename() {
//        csvEntries.add("Wrong mergedApplication.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "MergedApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("mergedApplication.1.serverFilename", StringUtils.EMPTY);
//                contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongCurriculumVitae1applicationFilename() {
//        csvEntries.add("Wrong curriculumVitae.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document cv = applicationForm.getCv();
//                if (cv != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(cv.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("curriculumVitae.1.serverFilename", filename);
//                    contentsProperties.put("curriculumVitae.1.applicationFilename", StringUtils.EMPTY);
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongCurriculumVitae1serverFilename() {
//        csvEntries.add("Wrong curriculumVitae.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document cv = applicationForm.getCv();
//                if (cv != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(cv.getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("curriculumVitae.1.serverFilename", StringUtils.EMPTY);
//                    contentsProperties.put("curriculumVitae.1.applicationFilename", cv.getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongtranscript1applicationFilename() {
//        csvEntries.add("Wrong transcript.1.applicationFilename with valid corresponding document in the upload package.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
//                case 1:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(qualifications.get(0).getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", filename);
//                    contentsProperties.put("transcript.1.applicationFilename", StringUtils.EMPTY);
//                    break;
//                case 0:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(transcriptBuilder.build(applicationForm));
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", filename);
//                    contentsProperties.put("transcript.1.applicationFilename", StringUtils.EMPTY);
//                    break;
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongtranscript1serverFilename() {
//        csvEntries.add("Wrong transcript.1.serverFilename with valid corresponding document in the upload package.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
//                case 1:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(qualifications.get(0).getContent());
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", StringUtils.EMPTY);
//                    contentsProperties.put("transcript.1.applicationFilename", qualifications.get(0).getFileName());
//                    break;
//                case 0:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(transcriptBuilder.build(applicationForm));
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.1.serverFilename", StringUtils.EMPTY);
//                    contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
//                    break;
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongtranscript2applicationFilename() {
//        csvEntries.add("Wrong transcript.2.applicationFilename with valid corresponding document in the upload package.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", StringUtils.EMPTY);
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongtranscript2serverFilename() {
//        csvEntries.add("Wrong transcript.2.serverFilename with valid corresponding document in the upload package.");
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
//                    contentsProperties.put("transcript.2.serverFilename", StringUtils.EMPTY);
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongApplicationForm1applicationFilename() {
//        csvEntries.add("Wrong applicationForm.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true).includeAttachments(false), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
//                contentsProperties.put("applicationForm.1.applicationFilename", StringUtils.EMPTY);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongapplicationForm1serverFilename() {
//        csvEntries.add("Wrong applicationForm.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                pdfDocumentBuilder.build(new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true).includeAttachments(false), zos, applicationForm);
//                zos.closeEntry();
//                contentsProperties.put("applicationForm.1.serverFilename", StringUtils.EMPTY);
//                contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongreference1applicationFilename() {
//        csvEntries.add("Wrong reference.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", StringUtils.EMPTY);
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongreference1serverFilename() {
//        csvEntries.add("Wrong reference.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", StringUtils.EMPTY);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongreference2applicationFilename() {
//        csvEntries.add("Wrong reference.2.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", StringUtils.EMPTY);
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongreference2serverFilename() {
//        csvEntries.add("Wrong reference.2.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", StringUtils.EMPTY);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongEnglishLanguageTestCertificate1applicationFilename() {
//        csvEntries.add("Wrong englishLanguageTestCertificate.1.applicationFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                LanguageQualification languageQualification = applicationForm.getPersonalDetails().getLanguageQualification();
//                if (languageQualification != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(FileUtils.readFileToByteArray(validPdf.getFile()));
//                    zos.closeEntry();            
//                    contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
//                    contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", StringUtils.EMPTY);
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void wrongEnglishLanguageTestCertificate1serverFilename() {
//        csvEntries.add("Wrong englishLanguageTestCertificate.1.serverFilename with valid corresponding document in the upload package.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                LanguageQualification languageQualification = applicationForm.getPersonalDetails().getLanguageQualification();
//                if (languageQualification != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(FileUtils.readFileToByteArray(validPdf.getFile()));
//                    zos.closeEntry();            
//                    contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", StringUtils.EMPTY);
//                    contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", languageQualification.getProofOfAward().getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    @DirtiesContext
//    public void documentUploadNotSent() throws CouldNotCreateAttachmentsPack, LocallyDefinedSshConfigurationIsWrong,
//            CouldNotOpenSshConnectionToRemoteHost, SftpTargetDirectoryNotAccessible,
//            SftpTransmissionFailedOrProtocolError {
//        csvEntries.add("Document upload not sent.");
//        SftpAttachmentsSendingService sendingServiceMock = EasyMock.createMock(SftpAttachmentsSendingService.class);
//        EasyMock.expect(sendingServiceMock.sendApplicationFormDocuments(EasyMock.anyObject(ApplicationForm.class), EasyMock.anyObject(TransferListener.class))).andReturn("123.zip");
//        EasyMock.replay(sendingServiceMock);
//        uclExportService.setSftpAttachmentsSendingService(sendingServiceMock);
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void emptyDocumentUpload() {
//        csvEntries.add("Empty document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            public void writeZipEntries(ApplicationForm applicationForm, String referenceNumber, OutputStream sftpOs) throws IOException, CouldNotCreateAttachmentsPack {
//                ZipOutputStream zos = null;
//                try {
//                    zos = new ZipOutputStream(sftpOs);
//                } finally {
//                    IOUtils.closeQuietly(zos);
//                }
//            }
//        });
//        sendToPortico();
//    }
//
//    @Test
//    @Transactional
//    public void noResearchProposal1IndocumentUpload() {
//        csvEntries.add("No researchProposal.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document personalStatement = applicationForm.getPersonalStatement();
//                if (personalStatement != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(new byte[]{});
//                    zos.closeEntry();
//                    contentsProperties.put("researchProposal.1.serverFilename", filename);
//                    contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedResearchProposal1InDocumentUpload() {
//        csvEntries.add("Corrupted researchProposal.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document personalStatement = applicationForm.getPersonalStatement();
//                if (personalStatement != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                    zos.closeEntry();
//                    contentsProperties.put("researchProposal.1.serverFilename", filename);
//                    contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noMergedApplication1InDocumentUpload() {
//        csvEntries.add("No mergedApplication.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "MergedApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                zos.write(new byte[]{});
//                zos.closeEntry();
//                contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
//                contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);        
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedMergedApplication1InDocumentUpload() {
//        csvEntries.add("Corrupted mergedApplication.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "MergedApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                zos.putNextEntry(new ZipEntry(serverfilename));
//                zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                zos.closeEntry();
//                contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
//                contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);        
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noCurriculumVitae1InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile() {
//        csvEntries.add("No curriculumVitae.1 in document upload with valid corresponding entry in document upload contents file (optional document).");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document cv = applicationForm.getCv();
//                if (cv != null) {
//                    String filename = getRandomFilename();                    
//                    contentsProperties.put("curriculumVitae.1.serverFilename", filename);
//                    contentsProperties.put("curriculumVitae.1.applicationFilename", cv.getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedCurriculumVitae1InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile() {
//        csvEntries.add("Corrupted curriculumVitae.1 in document upload with valid corresponding entry in document upload contents file (optional document).");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
//                Document cv = applicationForm.getCv();
//                if (cv != null) {
//                    String filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                    zos.closeEntry();
//                    contentsProperties.put("curriculumVitae.1.serverFilename", filename);
//                    contentsProperties.put("curriculumVitae.1.applicationFilename", cv.getFileName());
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noTranscript1InDocumentUpload() {
//        csvEntries.add("No transcript.1 in document upload.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
//                case 1:                    
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedTranscript1InDocumentUpload() {
//        csvEntries.add("Corrupted transcript.1 in document upload.");
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
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
//                case 1:
//                    filename = getRandomFilename();
//                    zos.putNextEntry(new ZipEntry(filename));
//                    zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noTranscript2InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile() {
//        csvEntries.add("No transcript.2 in document upload with valid corresponding entry in document upload contents file (optional document).");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addTranscriptFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<Document> qualifications = applicationForm.getQualificationsToSendToPortico();
//                String filename;
//                
//                switch (qualifications.size()) {
//                case 2:
//                    filename = getRandomFilename();
//                    contentsProperties.put("transcript.2.serverFilename", filename);
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedTranscript2InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile () {
//        csvEntries.add("Corrupted transcript.2 in document upload with valid corresponding entry in document upload contents file (optional document).");
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
//                    zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                    zos.closeEntry();
//                    contentsProperties.put("transcript.2.serverFilename", filename);
//                    contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noApplicationForm1InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile() {
//        csvEntries.add("No applicationForm.1 in document upload with valid corresponding entry in document upload contents file (optional document).");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
//                String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
//                contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
//                contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedApplicationForm1InDocumentUploadWithValidCorrespondingEntryInDocumentUploadContentsFile() {
//        csvEntries.add("Corrupted applicationForm.1 in document upload with valid corresponding entry in document upload contents file (optional document).");
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
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noReference1InDocumentUpload() {
//        csvEntries.add("No reference.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedReference1InDocumentUpload() {
//        csvEntries.add("Corrupted reference.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(1), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noReference2InDocumentUpload() {
//        csvEntries.add("No reference.2 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedReference2InDocumentUpload() {
//        csvEntries.add("Corrupted reference.2 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
//                String filename;
//                switch (references.size()) {
//                    case 2:
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                        zos.closeEntry();
//                        contentsProperties.put("reference.2.serverFilename", filename);
//                        contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
//
//                        filename = getRandomFilename();
//                        zos.putNextEntry(new ZipEntry(filename));
//                        combinedReferenceBuilder.build(references.get(0), zos);
//                        zos.closeEntry();
//                        contentsProperties.put("reference.1.serverFilename", filename);
//                        contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
//                        break;
//                    case 1:
//                    case 0:
//                    default:
//                        throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
//                }
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void noEnglishLanguageTestCertificate1InDocumentUpload() {
//        csvEntries.add("No englishLanguageTestCertificate.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//            }
//        });
//        sendToPortico();
//    }
//    
//    @Test
//    @Transactional
//    public void corruptedEnglishLanguageTestCertificate1InDocumentUpload() {
//        csvEntries.add("Corrupted englishLanguageTestCertificate.1 in document upload.");
//        uclExportService.setPorticoAttachmentsZipCreator(new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder, "test@test.com") {
//            @Override
//            protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
//                String filename = getRandomFilename();
//                zos.putNextEntry(new ZipEntry(filename));
//                zos.write(FileUtils.readFileToByteArray(damagedPdf.getFile()));
//                zos.closeEntry();            
//                contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
//                contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", "damaged.pdf");
//            }
//        });
//        sendToPortico();
//    }
//        
//    private void sendToPortico() {        
//        List<ApplicationForm> allApplicationsByStatus = applicationsService.getApplicationsByStatus(ApplicationFormStatus.REVIEW);
//        
//        applicationForm = null; 
//        
//        boolean foundEnoughDataForQualifications = false;
//        boolean foundEnoughDataForReferees = false;
//        
//        do {
//            int numberOfQualifications = 0;
//            int numberOfReferees = 0;
//            foundEnoughDataForQualifications = false;
//            foundEnoughDataForReferees = false;
//           
//            applicationForm = allApplicationsByStatus.get(random.nextInt(allApplicationsByStatus.size()));
//            
//            for (Qualification qualification : applicationForm.getQualifications()) {
//                if (qualification.getProofOfAward() != null) {
//                    qualification.setSendToUCL(true);
//                    numberOfQualifications++;
//                    if (numberOfQualifications == 2) {
//                        break;
//                    }
//                }
//            }
//            
//            for (Referee referee : applicationForm.getReferees()) {
//                if (referee.getReference() != null) {
//                    referee.setSendToUCL(true);
//                    numberOfReferees++;
//                    if (numberOfReferees == 2) {
//                        break;
//                    }
//                }
//            }
//            
//            if (numberOfQualifications >= 2) {
//                foundEnoughDataForQualifications = true;
//            }
//            
//            if (numberOfReferees == 2) {
//                foundEnoughDataForReferees = true;
//            }
//        } while (!(foundEnoughDataForQualifications && foundEnoughDataForReferees && !USED_APPLICATION_NUMBERS.contains(applicationForm.getApplicationNumber())));
//        
//        USED_APPLICATION_NUMBERS.add(applicationForm.getApplicationNumber());
//        
//        csvEntries.add(applicationForm.getApplicationNumber());
//        csvEntries.add(applicationForm.getApplicant().getLastName());
//        csvEntries.add(applicationForm.getApplicant().getFirstName());
//        csvEntries.add(applicationForm.getPersonalDetails().getDateOfBirth().toString());
//        
//        applicationsService.save(applicationForm);
//        
//        ApplicationFormTransfer applicationFormTransfer = uclExportService.createOrReturnExistingApplicationFormTransfer(applicationForm);
//        
//        try {
//            uclExportService.sendToPortico(applicationForm, applicationFormTransfer, new CsvTransferListener());
//            sentApps++;
//        } catch (PorticoExportServiceException e) {
//            e.printStackTrace();
//        }
//    }
//    
//    private class CsvTransferListener implements TransferListener {
//        @Override
//        public void webServiceCallStarted(SubmitAdmissionsApplicationRequest request, ApplicationForm form) {
//            switch (sentApps % 3) {
//            case 1:
//                request.getApplication().getCourseApplication().setApplicationStatus("WITHDRAWN");
//                break;
//            case 2:
//                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
//                request.getApplication().getCourseApplication().setDepartmentalDecision("REJECT");
//                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE UN-CONDITIONAL");
//                break;
//            default:
//            case 0:
//                request.getApplication().getCourseApplication().setApplicationStatus("ACTIVE");
//                request.getApplication().getCourseApplication().setDepartmentalDecision("OFFER");
//                request.getApplication().getCourseApplication().setDepartmentalOfferConditions("EXAMPLE CONDITIONAL");
//            }
//            
//            request.getApplication().getApplicant().setEnglishIsFirstLanguage(true);
//            request.getApplication().getApplicant().setEnglishLanguageQualificationList(null);
//            request.getApplication().getCourseApplication().setAtasStatement("ATAS STATEMENT");
//            
//            for (QualificationsTp detailsTp : request.getApplication().getApplicant().getQualificationList().getQualificationDetail()) {
//                detailsTp.getInstitution().setCode("UK0275");
//            }
//            
//            Marshaller marshaller = webServiceTemplate.getMarshaller();
//            try {
//                marshaller.marshal(request, new StreamResult(new File("request_" + applicationForm.getApplicationNumber() + ".txt")));
//            } catch (Exception e) {
//                Assert.fail(String.format("Could not marshall request correctly [reason=%s]", e.getMessage()));
//            }
//        }
//
//        @Override
//        public void webServiceCallCompleted(AdmissionsApplicationResponse response, ApplicationForm form) {
//            if (response != null) {
//                csvEntries.add(response.getReference().getApplicantID());
//                csvEntries.add(response.getReference().getApplicationID());
//                csvEntries.add("null");
//            } else {
//                csvEntries.add("null");
//                csvEntries.add("null");
//                csvEntries.add("null");
//            }
//        }
//
//        @Override
//        public void webServiceCallFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
//            csvEntries.add("null");
//            csvEntries.add("null");
//            csvEntries.add(error.getDiagnosticInfo());
//            Assert.fail(String.format("Received error from web service [reason=%s]", error.getDiagnosticInfo()));
//        }
//
//        @Override
//        public void sftpTransferStarted(ApplicationForm form) {
//        }
//
//        @Override
//        public void sftpTransferFailed(Throwable throwable, ApplicationFormTransferError error, ApplicationForm form) {
//            csvEntries.add("null");
//            csvEntries.add("null");
//            csvEntries.add("null");
//            csvEntries.add(error.getDiagnosticInfo());
//            Assert.fail(String.format("Received error from SFTP upload [reason=%s]", error.getDiagnosticInfo()));
//        }
//
//        @Override
//        public void sftpTransferCompleted(String zipFileName, ApplicationFormTransfer transfer) {
//            csvEntries.add(zipFileName);
//            csvEntries.add(transfer.getUclUserIdReceived());
//            csvEntries.add(transfer.getUclBookingReferenceReceived());
//            csvEntries.add("null");
//        }
//    }
}
