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
import com.zuehlke.pgadmissions.pdf.CombinedReferencesPdfBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.pdf.Transcript1PdfBuilder;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;

@Component
public class PorticoAttachmentsZipCreator {

    private final PdfDocumentBuilder pdfDocumentBuilder;
    
    private final CombinedReferencesPdfBuilder combinedReferenceBuilder;
    
    private final Transcript1PdfBuilder transcriptBuilder;
    
    public PorticoAttachmentsZipCreator() {
        this(null, null, null);
    }
    
    @Autowired
    public PorticoAttachmentsZipCreator(final PdfDocumentBuilder pdfDocumentBuilder, 
            final CombinedReferencesPdfBuilder combinedReferenceBuilder, final Transcript1PdfBuilder transcriptBuilder) {
        this.pdfDocumentBuilder = pdfDocumentBuilder;
        this.combinedReferenceBuilder = combinedReferenceBuilder;
        this.transcriptBuilder = transcriptBuilder;
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
        for (int i = 0; i < ApplicationForm.MAXIMUM_REFERENCES_FOR_EXPORT; i++) {
        	if (references.get(i) != null) {
                filename = getRandomFilename();
                zos.putNextEntry(new ZipEntry(filename));
                combinedReferenceBuilder.build(references.get(i), zos);
                zos.closeEntry();
                String is = Integer.toString(i + 1);
                contentsProperties.put("reference." + is + ".serverFilename", filename);
                contentsProperties.put("reference." + is + ".applicationFilename", "References." + is + ".pdf");
            }
        }
    }

    protected void addCV(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        Document cv = applicationForm.getCv();
        if (cv != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(cv.getContent());
            zos.closeEntry();
            contentsProperties.put("curriculumVitae.1.serverFilename", filename);
            contentsProperties.put("curriculumVitae.1.applicationFilename", cv.getFileName());
        }
    }

    protected void addLanguageTestCertificate(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        List<LanguageQualification> languageQualifications = applicationForm.getPersonalDetails().getLanguageQualificationToSend();
        if (!languageQualifications.isEmpty()) {
            String filename = getRandomFilename();
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
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(personalStatement.getContent());
            zos.closeEntry();
            contentsProperties.put("researchProposal.1.serverFilename", filename);
            contentsProperties.put("researchProposal.1.applicationFilename", personalStatement.getFileName());
        }
    }

    protected void addTranscriptFiles(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        List<Document> qualifications = applicationForm.getQualificationsToSendToPortico();
        String filename;
        int qualificationsToSend = qualifications.size();
        if (qualificationsToSend == 0) {
        	 filename = getRandomFilename();
             zos.putNextEntry(new ZipEntry(filename));
             zos.write(transcriptBuilder.build(applicationForm));
             zos.closeEntry();
             contentsProperties.put("transcript.1.serverFilename", filename);
             contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
        } else {
        	for (int i = 0; i < ApplicationForm.MAXIMUM_QUALIFICATIONS_FOR_EXPORT; i++) {
        		 filename = getRandomFilename();
                 zos.putNextEntry(new ZipEntry(filename));
                 zos.write(qualifications.get(i).getContent());
                 zos.closeEntry();
                 String is = Integer.toString(i + 1);
                 contentsProperties.put("transcript." + is + ".serverFilename", filename);
                 contentsProperties.put("transcript." + is + ".applicationFilename", qualifications.get(1).getFileName());
        	}
        }
    }
    
    protected void addApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "ApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        pdfDocumentBuilder.build(new PdfModelBuilder().includeCriminialConvictions(true).includeEqualOpportunities(true).includeAttachments(false), zos, applicationForm);
        zos.closeEntry();
        contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
        contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
    }
    
    protected void addMergedApplicationForm(ApplicationForm applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "MergedApplicationForm" + applicationForm.getApplicationNumber() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, applicationForm);
        zos.closeEntry();
        contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
        contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);        
    }
    
    protected String getRandomFilename() {
        return UUID.randomUUID() + ".pdf";
    }
    
}