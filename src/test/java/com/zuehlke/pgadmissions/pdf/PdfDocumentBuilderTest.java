package com.zuehlke.pgadmissions.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.exporters.PorticoAttachmentsZipCreator;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;

public class PdfDocumentBuilderTest {

    private ValidApplicationFormBuilder builder;
    
    private PdfDocumentBuilder pdfDocumentBuilder;
    
    private CombinedReferencesPdfBuilder combinedReferencesPdfBuilder;
    
    private Transcript1PdfBuilder transcript1PdfBuilder;
    
    private PorticoAttachmentsZipCreator attachmentsZipCreator;
    
    public PdfDocumentBuilderTest() {
    }
    
    @Before
    public void setup() {
        builder = new ValidApplicationFormBuilder();
        pdfDocumentBuilder = new PdfDocumentBuilder();
        combinedReferencesPdfBuilder = new CombinedReferencesPdfBuilder();
        transcript1PdfBuilder = new Transcript1PdfBuilder();
        attachmentsZipCreator = new PorticoAttachmentsZipCreator(
                pdfDocumentBuilder, combinedReferencesPdfBuilder, transcript1PdfBuilder, "test@test.com");
    }
    
    @Test
    @Ignore
    public void createPdfForApplicant() throws FileNotFoundException {
        PdfModelBuilder modelBuilder = new PdfModelBuilder().includeCriminialConvictions(true).includeDisability(true).includeEthnicity(true);
        pdfDocumentBuilder.build(modelBuilder, new FileOutputStream(new File("applicant_form.pdf")), builder.build());
    }
    
    @Test
    @Ignore
    public void createPdfForAdmin() throws FileNotFoundException {
        PdfModelBuilder modelBuilder = new PdfModelBuilder().includeReferences(true);
        pdfDocumentBuilder.build(modelBuilder, new FileOutputStream(new File("admin_form.pdf")), builder.build());
    }
    
    @Test
    @Ignore
    public void createZipForPortico() throws FileNotFoundException, IOException, CouldNotCreateAttachmentsPack {
        attachmentsZipCreator.writeZipEntries(builder.build(), "007", new FileOutputStream(new File("PdfDocumentBuilderTest.zip")));
    }
}