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

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.pdf.ApplicationAlternativeQualificationBuilder;
import com.zuehlke.pgadmissions.pdf.ApplicationCombinedReferencesBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationDocumentExportBuilder {

    @Autowired
    private PdfDocumentBuilder pdfDocumentBuilder;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationCombinedReferencesBuilder combinedReferenceBuilder;

    @Autowired
    private ApplicationAlternativeQualificationBuilder applicationQualificationTranscriptBuilder;

    public void getDocuments(Application application, String exportReference, OutputStream outputStream) throws IOException {
        Properties contentsProperties = new Properties();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(outputStream);
            buildContentsFile(application, exportReference, contentsProperties, zos);
            buildAcademicQualifications(application, exportReference, contentsProperties, zos);
            buildPersonalStatement(application, exportReference, contentsProperties, zos);
            buildLanguageQualification(application, exportReference, contentsProperties, zos);
            buildCv(application, exportReference, contentsProperties, zos);
            buildReferences(application, exportReference, contentsProperties, zos);
            buildStandaloneApplication(application, exportReference, contentsProperties, zos);
            buildMergedApplication(application, exportReference, contentsProperties, zos);
        } finally {
            IOUtils.closeQuietly(zos);
        }
    }

    private void buildContentsFile(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        contentsProperties.put("applicationNumber", application.getCode());
        contentsProperties.put("bookingReferenceNumber", referenceNumber);
        zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
        contentsProperties.store(zos, StringUtils.EMPTY);
        zos.closeEntry();
    }

    private void buildReferences(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        List<ApplicationReferee> referees = applicationService.getApplicationExportReferees(application);
        int refereeCount = referees.size();

        if (refereeCount == 2) {
            for (int i = 0; i < refereeCount; i++) {
                String filename = getRandomFilename();
                zos.putNextEntry(new ZipEntry(filename));
                combinedReferenceBuilder.build(referees.get(i).getComment(), zos);
                zos.closeEntry();
                int referenceNumberId = i + 1;
                contentsProperties.put("reference." + referenceNumberId + ".serverFilename", filename);
                contentsProperties.put("reference." + referenceNumberId + ".applicationFilename", "References.2.pdf");
            }
        } else {
            throw new Error("Wrong number of references (" + refereeCount + ") for export of application: " + application.getCode());
        }
    }

    private void buildCv(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        Document document = application.getDocument().getCv();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(application, document));
            zos.closeEntry();
            contentsProperties.put("curriculumVitae.1.serverFilename", filename);
            contentsProperties.put("curriculumVitae.1.applicationFilename", document != null ? document.getFileName() : "curriculumVitae.pdf");
        }
    }

    private void buildLanguageQualification(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        Document document = application.getPersonalDetails().getLanguageQualification().getDocument();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(application, document));
            zos.closeEntry();
            contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
            contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", document != null ? document.getFileName()
                    : "englishLanguageTestCertificate.pdf");
        }
    }

    private void buildPersonalStatement(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        Document document = application.getDocument().getPersonalStatement();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(application, document));
            zos.closeEntry();
            contentsProperties.put("researchProposal.1.serverFilename", filename);
            contentsProperties.put("researchProposal.1.applicationFilename", document != null ? document.getFileName() : "researchProposal.pdf");
        }
    }

    private void buildAcademicQualifications(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        List<ApplicationQualification> qualifications = applicationService.getApplicationExportQualifications(application);
        int qualificationCount = qualifications.size();
        
        if (qualificationCount > 1) {
            for (int i = 0; i < qualificationCount; i ++) {
                String filename = getRandomFilename();
                zos.putNextEntry(new ZipEntry(filename));
                Document transcript = qualifications.get(qualificationCount).getDocument();
                zos.write(getFileContents(application, transcript));
                zos.closeEntry();
                int qualificationNumberId = i + 1;
                contentsProperties.put("transcript." + qualificationNumberId + ".serverFilename", filename);
                contentsProperties.put("transcript." + qualificationNumberId + ".applicationFilename", transcript.getFileName());
            }
        } else {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(applicationQualificationTranscriptBuilder.build(application));
            zos.closeEntry();
            contentsProperties.put("transcript.1.serverFilename", filename);
            contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
        }
    }

    private void buildStandaloneApplication(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "ApplicationForm" + application.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            pdfDocumentBuilder.build(
                    new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true).includeAttachments(false), zos,
                    application);
        } catch (Exception e) {
            zos.write(getAlternativeMergedFileContents(application).getBytes());
        }
        zos.closeEntry();
        contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
        contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
    }

    private void buildMergedApplication(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "MergedApplicationForm" + application.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            pdfDocumentBuilder.build(new PdfModelBuilder().includeReferences(true), zos, application);
        } catch (Exception e) {
            zos.write(getAlternativeMergedFileContents(application).getBytes());
        }
        zos.closeEntry();
        contentsProperties.put("mergedApplication.1.serverFilename", serverfilename);
        contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);
    }

    private String getRandomFilename() {
        return UUID.randomUUID() + ".pdf";
    }

    private byte[] getFileContents(Application application, Document document) {
        if (document != null) {
            return document.getContent();
        }
        throw new Error("Document was missing for export of application: " + application.getCode());
    }

    private String getAlternativeMergedFileContents(Application application) {
        return ("Due to a technical error were unable to read and merge the contents of this document. Please contact us at "
                + application.getProgram().getUser() + " to obtain an original copy, quoting our application reference number: " + application.getCode() + ".");
    }

}
