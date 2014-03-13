package com.zuehlke.pgadmissions.services.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.pdf.CombinedReferencesPdfBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.pdf.Transcript1PdfBuilder;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;

@Component
public class PorticoAttachmentsZipCreator {
    
    private Logger log = LoggerFactory.getLogger(PorticoAttachmentsZipCreator.class);

    private final PdfDocumentBuilder pdfDocumentBuilder;
    
    private final CombinedReferencesPdfBuilder combinedReferenceBuilder;
    
    private final Transcript1PdfBuilder transcriptBuilder;
    
    private final String emailAddressTo;
    
    public PorticoAttachmentsZipCreator() {
        this(null, null, null, null);
    }
    
    @Autowired
    public PorticoAttachmentsZipCreator(final PdfDocumentBuilder pdfDocumentBuilder, 
            final CombinedReferencesPdfBuilder combinedReferenceBuilder, final Transcript1PdfBuilder transcriptBuilder,
            @Value("${email.address.to}") final String emailAddressTo) {
        this.pdfDocumentBuilder = pdfDocumentBuilder;
        this.combinedReferenceBuilder = combinedReferenceBuilder;
        this.transcriptBuilder = transcriptBuilder;
        this.emailAddressTo = emailAddressTo;
    }
    
    public void writeZipEntries(ApplicationForm applicationForm, String referenceNumber, OutputStream sftpOs) throws IOException, CouldNotCreateAttachmentsPack {
        Properties contentsProperties = new Properties();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(sftpOs);
            addTranscriptFiles(applicationForm, referenceNumber, contentsProperties, zos);
            addReserchProposal(applicationForm, referenceNumber, contentsProperties, zos);
            addLanguageTestCertificate(applicationForm, referenceNumber, contentsProperties, zos);
            addCV(applicationForm, referenceNumber, contentsProperties, zos);
            addReferences(applicationForm, referenceNumber, contentsProperties, zos);
            addApplicationForm(applicationForm, referenceNumber, contentsProperties, zos);
            addMergedApplicationForm(applicationForm, referenceNumber, contentsProperties, zos);
            addContentsFiles(applicationForm, referenceNumber, contentsProperties, zos);
        } finally {
            IOUtils.closeQuietly(zos);
        }
    }

    protected void addContentsFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        contentsProperties.put("applicationNumber", applicationForm.getApplicationNumber());
        contentsProperties.put("bookingReferenceNumber", referenceNumber);
        zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
        contentsProperties.store(zos, StringUtils.EMPTY);
        zos.closeEntry();
    }

    protected void addReferences(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        List<ReferenceComment> references = applicationForm.getReferencesToSendToPortico();
        String filename;
        switch (references.size()) {
            case 2:
                if (references.get(1) != null) {
                    filename = getRandomFilename();
                    zos.putNextEntry(new ZipEntry(filename));
                    combinedReferenceBuilder.build(references.get(1), zos);
                    zos.closeEntry();
                    contentsProperties.put("reference.2.serverFilename", filename);
                    contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
                }

                if (references.get(0) != null) {
                    filename = getRandomFilename();
                    zos.putNextEntry(new ZipEntry(filename));
                    combinedReferenceBuilder.build(references.get(0), zos);
                    zos.closeEntry();
                    contentsProperties.put("reference.1.serverFilename", filename);
                    contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
                }
                break;
        }
    }

    protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        Document cv = applicationForm.getCv();
        if (cv != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(cv, applicationForm));
            zos.closeEntry();
            contentsProperties.put("curriculumVitae.1.serverFilename", filename);
            contentsProperties.put("curriculumVitae.1.applicationFilename", cv.getFileName());
        }
    }

    protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        LanguageQualification languageQualification = applicationForm.getPersonalDetails().getLanguageQualification();
        if (languageQualification != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(languageQualification.getLanguageQualificationDocument(), applicationForm));
            zos.closeEntry();            
            contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
            contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", languageQualification.getLanguageQualificationDocument().getFileName());
        }
    }

    protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        Document personalStatement = applicationForm.getPersonalStatement();
        if (personalStatement != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(personalStatement, applicationForm));
            zos.closeEntry();
            contentsProperties.put("researchProposal.1.serverFilename", filename);
            contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
        }
    }

    protected void addTranscriptFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        List<Document> qualifications = applicationForm.getQualificationsToSendToPortico();
        String filename;
        
        switch (qualifications.size()) {
        case 2:
            filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(qualifications.get(1), applicationForm));
            zos.closeEntry();
            contentsProperties.put("transcript.2.serverFilename", filename);
            contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
        case 1:
            filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(qualifications.get(0), applicationForm));
            zos.closeEntry();
            contentsProperties.put("transcript.1.serverFilename", filename);
            contentsProperties.put("transcript.1.applicationFilename", qualifications.get(0).getFileName());
            break;
        case 0:
            filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(transcriptBuilder.build(applicationForm));
            zos.closeEntry();
            contentsProperties.put("transcript.1.serverFilename", filename);
            contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
            break;
        }
    }
    
    protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            pdfDocumentBuilder.build(new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true).includeAttachments(false), zos, applicationForm);
        } catch (Exception e) {
            zos.write(getAlternativeMergedFileContents(applicationForm));
        } 
        zos.closeEntry();
        contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
        contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
    }
    
    protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "MergedApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, applicationForm);
        } catch (Exception e) {
            zos.write(getAlternativeMergedFileContents(applicationForm));
        }
        zos.closeEntry();
        contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
        contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);        
    }
    
    protected String getRandomFilename() {
        return UUID.randomUUID() + ".pdf";
    }
    
    private byte[] getFileContents(Document document, ApplicationForm application) {
        try {
            return document.getContent();
        } catch (Exception e) {
            log.error("Couldnt read document", e);
            return getAlternativeMergedFileContents(application);
        }
    }
    
    private byte[] getAlternativeMergedFileContents(ApplicationForm application) {
        return ("We are sorry but we were unable to read and merge the contents of this document. " +
                "Please contact us at " + emailAddressTo + " to obtain an original copy, " +
                "quoting our application reference number: " + application.getApplicationNumber() + ".").getBytes();
    }
    
}
