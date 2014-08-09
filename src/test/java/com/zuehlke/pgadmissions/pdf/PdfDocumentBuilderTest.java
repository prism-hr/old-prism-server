package com.zuehlke.pgadmissions.pdf;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.integration.providers.ApplicationTestDataProvider;
import com.zuehlke.pgadmissions.services.exporters.ApplicationDocumentExportBuilder;

public class PdfDocumentBuilderTest {
    
    private PdfDocumentBuilder pdfDocumentBuilder;
    
    private ApplicationDocumentExportBuilder attachmentsZipCreator;
    
    @Autowired
    private ApplicationTestDataProvider applicationTestDataProvider;
    
    @Test
    public void createPdfForApplicant() throws Exception {
        Application application = new Application();
        applicationTestDataProvider.fillWithData(application);
        PdfModelBuilder modelBuilder = new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true);
        pdfDocumentBuilder.build(modelBuilder, new FileOutputStream(new File("applicant_form.pdf")), application);
    }
    
    @Test
    public void createPdfForAdmin() throws Exception {
        Application application = new Application();
        applicationTestDataProvider.fillWithData(application);
        PdfModelBuilder modelBuilder = new PdfModelBuilder().includeReferences(true);
        pdfDocumentBuilder.build(modelBuilder, new FileOutputStream(new File("admin_form.pdf")), application);
    }
    
    @Test
    public void createZipForPortico() throws Exception {
        Application application = new Application();
        applicationTestDataProvider.fillWithData(application);
        attachmentsZipCreator.getDocuments(application, "007", new FileOutputStream(new File("PdfDocumentBuilderTest.zip")));
    }
}
