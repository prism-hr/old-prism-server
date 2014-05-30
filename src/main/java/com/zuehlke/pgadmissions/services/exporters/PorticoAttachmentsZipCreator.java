package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
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

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.pdf.CombinedReferencesPdfBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.pdf.Transcript1PdfBuilder;
import com.zuehlke.pgadmissions.services.PorticoService;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;

@Component
public class PorticoAttachmentsZipCreator {

    private Logger log = LoggerFactory.getLogger(PorticoAttachmentsZipCreator.class);

    @Autowired
    private PdfDocumentBuilder pdfDocumentBuilder;

    @Autowired
    private CombinedReferencesPdfBuilder combinedReferenceBuilder;

    @Autowired
    private Transcript1PdfBuilder transcriptBuilder;
    
    @Autowired
    private PorticoService porticoService;

    @Value("${email.address.to}")
    private String emailAddressTo;

    public void writeZipEntries(Application applicationForm, String referenceNumber, OutputStream sftpOs) throws IOException, CouldNotCreateAttachmentsPack {
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

    protected void addContentsFiles(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        contentsProperties.put("applicationNumber", applicationForm.getCode());
        contentsProperties.put("bookingReferenceNumber", referenceNumber);
        zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
        contentsProperties.store(zos, StringUtils.EMPTY);
        zos.closeEntry();
    }

    protected void addReferences(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException, CouldNotCreateAttachmentsPack {
        List<Comment> references = porticoService.getReferencesToSendToPortico(applicationForm);
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

    protected void addCV(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        Document document = applicationForm.getApplicationDocument().getCv();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(document, applicationForm));
            zos.closeEntry();
            contentsProperties.put("curriculumVitae.1.serverFilename", filename);
            contentsProperties.put("curriculumVitae.1.applicationFilename", document != null ? document.getFileName()
                    : "curriculumVitae.pdf");
        }
    }

    protected void addLanguageTestCertificate(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException, CouldNotCreateAttachmentsPack {
        Document document = applicationForm.getPersonalDetails().getLanguageQualification().getProofOfAward();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(document, applicationForm));
            zos.closeEntry();
            contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
            contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", document != null ? document.getFileName()
                    : "englishLanguageTestCertificate.pdf");
        }
    }

    protected void addReserchProposal(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        Document document = applicationForm.getApplicationDocument().getPersonalStatement();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(document, applicationForm));
            zos.closeEntry();
            contentsProperties.put("researchProposal.1.serverFilename", filename);
            contentsProperties.put("researchProposal.1.applicationFilename", document != null ? document.getFileName()
                    : "researchProposal.pdf");
        }
    }

    protected void addTranscriptFiles(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException, CouldNotCreateAttachmentsPack {
        List<Document> qualifications = porticoService.getQualificationsToSendToPortico(applicationForm);
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

    protected void addApplicationForm(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "ApplicationForm" + applicationForm.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            pdfDocumentBuilder.build(
                    new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true).includeAttachments(false), zos,
                    applicationForm);
        } catch (Exception e) {
            zos.write(getAlternativeMergedFileContents(applicationForm).getBytes());
        }
        zos.closeEntry();
        contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
        contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
    }

    protected void addMergedApplicationForm(Application applicationForm, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException, CouldNotCreateAttachmentsPack {
        String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "MergedApplicationForm" + applicationForm.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, applicationForm);
        } catch (Exception e) {
            zos.write(getAlternativeMergedFileContents(applicationForm).getBytes());
        }
        zos.closeEntry();
        contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
        contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);
    }

    protected String getRandomFilename() {
        return UUID.randomUUID() + ".pdf";
    }

    private byte[] getFileContents(Document document, Application application) {
        try {
            com.itextpdf.text.Document output = new com.itextpdf.text.Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(output, baos);
            output.open();
            if (document != null) {
                try {
                    return document.getContent();
                } catch (Exception e) {
                    log.error("Couldn't read document", e);
                }
            } else {
                log.error("Attempted to merge null document for portico export");
            }
            output.add(new Paragraph(getAlternativeMergedFileContents(application)));
            return baos.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }
    
    private String getAlternativeMergedFileContents(Application application) {
        return ("We are sorry but we were unable to read and merge the contents of this document. " + "Please contact us at " + emailAddressTo
                + " to obtain an original copy, " + "quoting our application reference number: " + application.getCode() + ".");
    }

}
