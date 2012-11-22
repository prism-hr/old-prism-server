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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;

@Component
public class PorticoAttachmentsZipCreator {

    private final PdfDocumentBuilder pdfDocumentBuilder;
    
    public PorticoAttachmentsZipCreator() {
        this(null);
    }
    
    @Autowired
    public PorticoAttachmentsZipCreator(PdfDocumentBuilder pdfDocumentBuilder) {
        this.pdfDocumentBuilder = pdfDocumentBuilder;
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
        List<ReferenceComment> references = applicationForm.getReferencesToSend();
        String filename;
        switch (references.size()) {
            case 2:
                filename = UUID.randomUUID() + ".pdf";
                zos.putNextEntry(new ZipEntry(filename));
                pdfDocumentBuilder.writeCombinedReferencesAsPdfToOutputstream(references.get(1), zos);
                zos.closeEntry();
                contentsProperties.put("reference.2.serverFilename", filename);
                contentsProperties.put("reference.2.applicationFilename", "References.2.pdf");
            case 1:
                filename = UUID.randomUUID() + ".pdf";
                zos.putNextEntry(new ZipEntry(filename));
                pdfDocumentBuilder.writeCombinedReferencesAsPdfToOutputstream(references.get(0), zos);
                zos.closeEntry();
                contentsProperties.put("reference.1.serverFilename", filename);
                contentsProperties.put("reference.1.applicationFilename", "References.1.pdf");
            case 0:
                break;
            default:
                throw new CouldNotCreateAttachmentsPack("There should be at most 2 references marked for sending to UCL");
        }
    }

    protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        Document cv = applicationForm.getCv();
        if (cv != null) {
            String filename = UUID.randomUUID() + ".pdf";
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(cv.getContent());
            zos.closeEntry();
            contentsProperties.put("curriculumVitae.1.serverFilename", filename);
            contentsProperties.put("curriculumVitae.1.applicationFilename", cv.getFileName());
        }
    }

    protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        List<LanguageQualification> languageQualifications = applicationForm.getLanguageQualificationToSend();
        if (languageQualifications.size() > 1)
            throw new CouldNotCreateAttachmentsPack("There should be at most 1 languageQualification marked for sending to UCL");
        if (!languageQualifications.isEmpty()) {
            String filename = UUID.randomUUID() + ".pdf";
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(languageQualifications.get(0).getLanguageQualificationDocument().getContent());
            zos.closeEntry();            
            contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
            contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", languageQualifications.get(0).getLanguageQualificationDocument().getFileName());
        }
    }

    protected void addReserchProposal(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        Document personalStatement = applicationForm.getPersonalStatement();
        if (personalStatement != null) {
            String filename = UUID.randomUUID() + ".pdf";
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(personalStatement.getContent());
            zos.closeEntry();
            contentsProperties.put("researchProposal.1.serverFilename", filename);
            contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
        }
    }

    protected void addTranscriptFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        List<Document> qualifications = applicationForm.getQualificationsToSend();
        String filename;
        switch (qualifications.size()) {
            case 2:
                filename = UUID.randomUUID() + ".pdf";
                zos.putNextEntry(new ZipEntry(filename));
                zos.write(qualifications.get(1).getContent());
                zos.closeEntry();
                contentsProperties.put("transcript.2.serverFilename", filename);
                contentsProperties.put("transcript.2.applicationFilename", qualifications.get(1).getFileName());
            case 1:
                filename = UUID.randomUUID() + ".pdf";
                zos.putNextEntry(new ZipEntry(filename));
                zos.write(qualifications.get(0).getContent());
                zos.closeEntry();
                contentsProperties.put("transcript.1.serverFilename", filename);
                contentsProperties.put("transcript.1.applicationFilename", qualifications.get(0).getFileName());
            case 0:
                break;//todo: check if business ruless force us to have at least one transcript file attached - it yes, throw CouldNotCreateAttachmentsPack
            default:
                throw new CouldNotCreateAttachmentsPack("There should be at most 2 qualifications marked for sending to UCL");
        }
    }
    
    protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        pdfDocumentBuilder.buildPdf(applicationForm, zos, false);
        zos.closeEntry();
        contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
        contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
    }
    
    protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "MergedApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        pdfDocumentBuilder.buildPdf(applicationForm, zos, true);
        zos.closeEntry();
        contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
        contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);        
    }
}
