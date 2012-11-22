package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml") 
public class PorticoAttachmentsZipCreatorTest extends UclIntegrationBaseTest {

    private PorticoAttachmentsZipCreator attachmentsZipCreator;

    private ApplicationForm applicationForm;

    private PdfDocumentBuilder pdfDocumentBuilder;

    private File zipFile;

    @Test
    public void shouldWriteZipFile() throws IOException, CouldNotCreateAttachmentsPack {

        zipFile = File.createTempFile(uclBookingReferenceNumber,".zip");
        zipFile.deleteOnExit();

        FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
        attachmentsZipCreator.writeZipEntries(applicationForm, uclBookingReferenceNumber, fileOutputStream);

        int numberOfFiles = 0;
        Set<String> fileNames = new HashSet<String>();
        Properties contentProperties = new Properties();

        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) e.nextElement();

            numberOfFiles++;
            fileNames.add(entry.getName());

            if (entry.getName().equalsIgnoreCase("P123456Contents.txt")) {
                InputStream is = zip.getInputStream(entry);
                contentProperties.load(is);
            }
        }

        assertEquals("There are to few or to many files in the Zip", 9, numberOfFiles);
        assertEquals("There are duplicate file names", 9, fileNames.size());
        assertTrue("The contents file is missing.", fileNames.contains("P123456Contents.txt"));
        assertEquals("The contents file has to few or to many entries", 18, contentProperties.size());

        assertEquals("TMRMBISING01-2012-999999", contentProperties.get("applicationNumber"));
        assertEquals("References.1.pdf", contentProperties.get("reference.1.applicationFilename"));
        assertEquals("My Proof of Award.pdf", contentProperties.get("transcript.1.applicationFilename"));
        assertEquals("Language Qualification - My Name.pdf", contentProperties.get("englishLanguageTestCertificate.1.applicationFilename"));
        assertEquals("P123456", contentProperties.get("bookingReferenceNumber"));
        assertEquals("My Personal Statement (v1.0).pdf", contentProperties.get("researchProposal.1.applicationFilename"));
        assertEquals("My CV.pdf", contentProperties.get("curriculumVitae.1.applicationFilename"));
        assertEquals("References.2.pdf", contentProperties.get("reference.2.applicationFilename"));
        assertEquals("ApplicationFormP123456.pdf", contentProperties.get("applicationForm.1.serverFilename"));
        assertEquals("ApplicationFormTMRMBISING01-2012-999999.pdf", contentProperties.get("applicationForm.1.applicationFilename"));
        assertEquals("MergedApplicationFormP123456.pdf", contentProperties.get("mergedApplication.1.serverFilename"));
        assertEquals("MergedApplicationFormTMRMBISING01-2012-999999.pdf", contentProperties.get("mergedApplication.1.applicationFilename"));
    }

    @Before
    public void setup() {
        applicationForm = getValidApplicationForm();
        pdfDocumentBuilder = new PdfDocumentBuilder();
        attachmentsZipCreator = new PorticoAttachmentsZipCreator(pdfDocumentBuilder);
    }
}
