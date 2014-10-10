package com.zuehlke.pgadmissions.services.builders;

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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.Document;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.services.ApplicationDownloadService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadEquivalentExperienceBuilder;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadReferenceBuilder;

@Component
public class ApplicationDocumentExportBuilder {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDownloadService applicationDownloadService;

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private ApplicationContext applicationContext;

    public void getDocuments(Application application, String exportReference, OutputStream outputStream) throws IOException {
        Properties contentsProperties = new Properties();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(outputStream);
            buildContentsFile(application, exportReference, contentsProperties, zos);
            buildAcademicQualifications(application, exportReference, contentsProperties, zos);
            buildLanguageQualification(application, exportReference, contentsProperties, zos);
            buildPersonalStatement(application, exportReference, contentsProperties, zos);
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

    private void buildAcademicQualifications(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        List<ApplicationQualification> qualifications = applicationService.getApplicationExportQualifications(application);
        int qualificationCount = qualifications.size();
        if (qualificationCount > 0) {
            for (int i = 0; i < qualificationCount; i++) {
                String filename = getRandomFilename();
                zos.putNextEntry(new ZipEntry(filename));
                Document transcript = qualifications.get(i).getDocument();
                zos.write(getFileContents(application, transcript));
                zos.closeEntry();
                int qualificationNumberId = i + 1;
                contentsProperties.put("transcript." + qualificationNumberId + ".serverFilename", filename);
                contentsProperties.put("transcript." + qualificationNumberId + ".applicationFilename", transcript.getFileName());
            }
        } else {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            Comment approvalComment = commentService.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
            zos.write(applicationContext.getBean(ApplicationDownloadEquivalentExperienceBuilder.class).build(application, approvalComment));
            zos.closeEntry();
            contentsProperties.put("transcript.1.serverFilename", filename);
            contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
        }
    }

    private void buildLanguageQualification(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        ApplicationPersonalDetail applicationPersonalDetail = application.getPersonalDetail();
        ApplicationLanguageQualification applicationLanguageQualification = applicationPersonalDetail == null ? null : applicationPersonalDetail
                .getLanguageQualification();
        Document document = applicationLanguageQualification == null ? null : applicationLanguageQualification.getDocument();
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

    private void buildPersonalStatement(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        ApplicationDocument applicationDocument = application.getDocument();
        Document document = applicationDocument == null ? null : applicationDocument.getPersonalStatement();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(application, document));
            zos.closeEntry();
            contentsProperties.put("researchProposal.1.serverFilename", filename);
            contentsProperties.put("researchProposal.1.applicationFilename", document != null ? document.getFileName() : "researchProposal.pdf");
        }
    }

    private void buildCv(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        ApplicationDocument applicationDocument = application.getDocument();
        Document document = applicationDocument == null ? null : applicationDocument.getCv();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(getFileContents(application, document));
            zos.closeEntry();
            contentsProperties.put("curriculumVitae.1.serverFilename", filename);
            contentsProperties.put("curriculumVitae.1.applicationFilename", document != null ? document.getFileName() : "curriculumVitae.pdf");
        }
    }

    private void buildReferences(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        List<ApplicationReferee> referees = applicationService.getApplicationExportReferees(application);
        for (int i = 0; i < 2; i++) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(applicationContext.getBean(ApplicationDownloadReferenceBuilder.class).build(application, referees.get(i).getComment()));
            zos.closeEntry();
            int referenceNumberId = i + 1;
            contentsProperties.put("reference." + referenceNumberId + ".serverFilename", filename);
            contentsProperties.put("reference." + referenceNumberId + ".applicationFilename", "References."  + referenceNumberId + ".pdf");
        }
    }

    private void buildStandaloneApplication(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "ApplicationForm" + application.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            ApplicationDownloadDTO applicationDownloadDTO = new ApplicationDownloadDTO().withApplication(application).withIncludeEqualOpportuntiesData(true);
            applicationDownloadService.build(applicationDownloadDTO, zos);
        } catch (Exception e) {
            throw new Error("Unable to build application document for " + application.getCode(), e);
        }
        zos.closeEntry();
        contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
        contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
    }

    private void buildMergedApplication(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        String serverfilename = "MergedApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "MergedApplicationForm" + application.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            ApplicationDownloadDTO applicationDownloadDTO = new ApplicationDownloadDTO().withApplication(application).withIncludeReferences(true);
            applicationDownloadService.build(applicationDownloadDTO, zos);
        } catch (Exception e) {
            throw new Error("Unable to merged application document for " + application.getCode(), e);
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

}
