package com.zuehlke.pgadmissions.services.builders;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationDownloadMode;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReferenceDTO;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.ApplicationDownloadService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderHelper;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadEquivalentExperienceBuilder;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadReferenceBuilder;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope(SCOPE_PROTOTYPE)
public class ApplicationDocumentExportBuilder {

    private PropertyLoader propertyLoader;

    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDownloadService applicationDownloadService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ApplicationContext applicationContext;

    public void getDocuments(Application application, String exportReference, OutputStream outputStream) throws Exception {
        Properties contentsProperties = new Properties();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(outputStream);
            buildAcademicQualifications(application, contentsProperties, zos);
            buildLanguageQualification(application, contentsProperties, zos);
            buildPersonalStatement(application, contentsProperties, zos);
            buildCv(application, exportReference, contentsProperties, zos);
            buildReferences(application, contentsProperties, zos);
            buildStandaloneApplication(application, exportReference, contentsProperties, zos);
            buildMergedApplication(application, exportReference, contentsProperties, zos);
            buildContentsFile(application, exportReference, contentsProperties, zos);
        } finally {
            IOUtils.closeQuietly(zos);
        }
    }

    public ApplicationDocumentExportBuilder localize(PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
        this.applicationDownloadBuilderHelper = applicationContext.getBean(ApplicationDownloadBuilderHelper.class).localize(propertyLoader);
        return this;
    }

    private void buildContentsFile(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        contentsProperties.put("applicationNumber", application.getCode());
        contentsProperties.put("bookingReferenceNumber", referenceNumber);
        zos.putNextEntry(new ZipEntry(referenceNumber + "Contents.txt"));
        contentsProperties.store(zos, StringUtils.EMPTY);
        zos.closeEntry();
    }

    private void buildAcademicQualifications(Application application, Properties contentsProperties, ZipOutputStream zos) throws IOException,
            IntegrationException {
        List<ApplicationQualification> qualifications = applicationService.getApplicationExportQualifications(application);
        int qualificationCount = qualifications.size();
        if (qualificationCount > 0) {
            for (int i = 0; i < qualificationCount; i++) {
                String filename = getRandomFilename();
                zos.putNextEntry(new ZipEntry(filename));
                Document document = qualifications.get(i).getDocument();

                if (document == null) {
                    buildSurrogateAcademicQualificationProofOfAward(zos);
                } else {
                    zos.write(documentService.getDocumentContent(document));
                }

                zos.closeEntry();
                int qualificationNumberId = i + 1;
                contentsProperties.put("transcript." + qualificationNumberId + ".serverFilename", filename);
                contentsProperties.put("transcript." + qualificationNumberId + ".applicationFilename",
                        document == null ? "ExplanationOfMissingQualifications.pdf" : document.getExportFilenameSits());
            }
        } else {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            buildSurrogateAcademicQualificationProofOfAward(zos);
            zos.closeEntry();
            contentsProperties.put("transcript.1.serverFilename", filename);
            contentsProperties.put("transcript.1.applicationFilename", "ExplanationOfMissingQualifications.pdf");
        }
    }

    private void buildSurrogateAcademicQualificationProofOfAward(ZipOutputStream zos) throws IOException {
        zos.write(applicationContext.getBean(ApplicationDownloadEquivalentExperienceBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                .build());
    }

    private void buildLanguageQualification(Application application, Properties contentsProperties, ZipOutputStream zos) throws IOException,
            IntegrationException {
        ApplicationPersonalDetail applicationPersonalDetail = application.getPersonalDetail();
        ApplicationLanguageQualification applicationLanguageQualification = applicationPersonalDetail == null ? null : applicationPersonalDetail
                .getLanguageQualification();
        Document document = applicationLanguageQualification == null ? null : applicationLanguageQualification.getDocument();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(documentService.getDocumentContent(document));
            zos.closeEntry();
            contentsProperties.put("englishLanguageTestCertificate.1.serverFilename", filename);
            contentsProperties.put("englishLanguageTestCertificate.1.applicationFilename", document.getExportFilenameSits());
        }
    }

    private void buildPersonalStatement(Application application, Properties contentsProperties, ZipOutputStream zos) throws IOException, IntegrationException {
        ApplicationDocument applicationDocument = application.getDocument();
        Document document = applicationDocument == null ? null : applicationDocument.getPersonalStatement();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(documentService.getDocumentContent(document));
            zos.closeEntry();
            contentsProperties.put("researchProposal.1.serverFilename", filename);
            contentsProperties.put("researchProposal.1.applicationFilename", document.getExportFilenameSits());
        }
    }

    private void buildCv(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException,
            IntegrationException {
        ApplicationDocument applicationDocument = application.getDocument();
        Document document = applicationDocument == null ? null : applicationDocument.getCv();
        if (document != null) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(documentService.getDocumentContent(document));
            zos.closeEntry();
            contentsProperties.put("curriculumVitae.1.serverFilename", filename);
            contentsProperties.put("curriculumVitae.1.applicationFilename", document.getExportFilenameSits());
        }
    }

    private void buildReferences(Application application, Properties contentsProperties, ZipOutputStream zos) throws Exception {
        List<ApplicationReferenceDTO> references = applicationService.getApplicationExportReferees(application);
        for (int i = 0; i < 2; i++) {
            String filename = getRandomFilename();
            zos.putNextEntry(new ZipEntry(filename));
            zos.write(applicationContext.getBean(ApplicationDownloadReferenceBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                    .build(application, references.get(i).getComment()));
            zos.closeEntry();
            int referenceNumberId = i + 1;
            contentsProperties.put("reference." + referenceNumberId + ".serverFilename", filename);
            contentsProperties.put("reference." + referenceNumberId + ".applicationFilename", "References." + referenceNumberId + ".pdf");
        }
    }

    private void buildStandaloneApplication(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos)
            throws IOException {
        String serverfilename = "ApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "ApplicationForm" + application.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverfilename));
        try {
            ApplicationDownloadDTO applicationDownloadDTO = new ApplicationDownloadDTO().withApplication(application)
                    .withDownloadMode(ApplicationDownloadMode.SYSTEM).withIncludeEqualOpportunties(true);
            applicationDownloadService.build(applicationDownloadDTO, propertyLoader, applicationDownloadBuilderHelper, zos);
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
        zos.closeEntry();
        contentsProperties.put("applicationForm.1.serverFilename", serverfilename);
        contentsProperties.put("applicationForm.1.applicationFilename", applicationFilename);
    }

    private void buildMergedApplication(Application application, String referenceNumber, Properties contentsProperties, ZipOutputStream zos) throws IOException {
        String serverFilename = "MergedApplicationForm" + referenceNumber + ".pdf";
        String applicationFilename = "MergedApplicationForm" + application.getCode() + ".pdf";
        zos.putNextEntry(new ZipEntry(serverFilename));
        try {
            ApplicationDownloadDTO applicationDownloadDTO = new ApplicationDownloadDTO().withApplication(application)
                    .withDownloadMode(ApplicationDownloadMode.SYSTEM).withIncludeAssessments(true);
            applicationDownloadService.build(applicationDownloadDTO, propertyLoader, applicationDownloadBuilderHelper, zos);
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
        zos.closeEntry();
        contentsProperties.put("mergedApplication.1.serverFilename", serverFilename);
        contentsProperties.put("mergedApplication.1.applicationFilename", applicationFilename);
    }

    private String getRandomFilename() {
        return UUID.randomUUID() + ".pdf";
    }

}
