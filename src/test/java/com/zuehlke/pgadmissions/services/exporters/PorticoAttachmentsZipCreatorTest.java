package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.pdf.CombinedReferencesPdfBuilder;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.Transcript1PdfBuilder;
import com.zuehlke.pgadmissions.services.exporters.SftpAttachmentsSendingService.CouldNotCreateAttachmentsPack;

@RunWith(PowerMockRunner.class)
public class PorticoAttachmentsZipCreatorTest {

    private String uclBookingReferenceNumber = "P123456";
    
    private PorticoAttachmentsZipCreator attachmentsZipCreator;

    private ApplicationForm applicationForm;

    private PdfDocumentBuilder pdfDocumentBuilder;
    
    private CombinedReferencesPdfBuilder combinedReferenceBuilder;
    
    private Transcript1PdfBuilder transcriptBuilder;
    
    private Map<String, String> expectedValues = new HashMap<String, String>();
    
    private Set<String> usedRandomFilenames = new HashSet<String>();

    private static final String RANDOM_FILENAME = "RANDOM_FILENAME";
    
    @Mock 
    private SecurityContextHolder mockSecurityContextHolder;
    
    @Mock
    private SecurityContext mockSecurityContext;
    
    @Mock
    private Authentication authenticationMock;
    
    private RegisteredUser registeredUserMock;
    
    @Test
    @PrepareForTest(SecurityContextHolder.class)
    public void shouldWriteZipFile() throws IOException, CouldNotCreateAttachmentsPack {
        // Given
        registeredUserMock = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(registeredUserMock.hasAdminRightsOnApplication(EasyMock.isA(ApplicationForm.class))).andReturn(true).anyTimes();
        EasyMock.expect(registeredUserMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(true).anyTimes();
        EasyMock.expect(registeredUserMock.isInRole(Authority.APPLICANT)).andReturn(false).anyTimes();
        PowerMock.mockStatic(SecurityContextHolder.class);
        EasyMock.expect(SecurityContextHolder.getContext()).andReturn(mockSecurityContext).anyTimes();
        EasyMock.expect(mockSecurityContext.getAuthentication()).andReturn(authenticationMock).anyTimes();
        EasyMock.expect(authenticationMock.getDetails()).andReturn(registeredUserMock).anyTimes();
        PowerMock.replay(SecurityContextHolder.class);
        EasyMock.replay(mockSecurityContextHolder, mockSecurityContext, authenticationMock, registeredUserMock);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        attachmentsZipCreator = new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder) {
            @Override
            protected String getRandomFilename() {
                String randomFilename = UUID.randomUUID() + ".pdf";
                usedRandomFilenames.add(randomFilename);
                return randomFilename;
            }
        };

        // When
        attachmentsZipCreator.writeZipEntries(applicationForm, uclBookingReferenceNumber, outputStream);

        // Then
        int numberOfFiles = 0;
        Set<String> fileNames = new HashSet<String>();
        Properties contentProperties = new Properties();

        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        ZipEntry entry = null;
        while((entry = zip.getNextEntry()) != null) {
            numberOfFiles++;
            fileNames.add(entry.getName());

            if (entry.getName().equalsIgnoreCase("P123456Contents.txt")) {
                contentProperties.load(zip);
            }
        }

        assertEquals("There are to few or to many files in the Zip", 10, numberOfFiles);
        assertEquals("There are duplicate file names", 10, fileNames.size());
        assertTrue("The contents file is missing.", fileNames.contains("P123456Contents.txt"));
        assertEquals("The contents file has to few or to many entries", 20, contentProperties.size());

        for (Object keyObj : contentProperties.keySet()) {
            String key = (String) keyObj;
            if (expectedValues.containsKey(key)) {
                String value = contentProperties.getProperty(key);
                String expectedValue = expectedValues.get(key);
                
                if (RANDOM_FILENAME.equals(expectedValue)) {
                    Assert.assertTrue(String.format("The contents file contains an unexpected value [key=%s, value=%s]", key, value), usedRandomFilenames.contains(value));                    
                } else {
                    Assert.assertEquals("The contents file contains an unexpected value", expectedValues.get(key), value);
                }
            } else {
                Assert.fail(String.format("Unexpected entry in the contents file: [key=%s, value=%s]", key, contentProperties.get(key)));
            }
        }
    }

    @Before
    public void setup() {
        applicationForm = new ValidApplicationFormBuilder().build();
        pdfDocumentBuilder = new PdfDocumentBuilder();
        combinedReferenceBuilder = new CombinedReferencesPdfBuilder();
        transcriptBuilder = new Transcript1PdfBuilder();
        
        attachmentsZipCreator = new PorticoAttachmentsZipCreator(pdfDocumentBuilder, combinedReferenceBuilder, transcriptBuilder);
        
        expectedValues.put("bookingReferenceNumber", "P123456");

        expectedValues.put("applicationNumber", "TMRMBISING01-2012-999999");
        
        expectedValues.put("applicationForm.1.serverFilename", "ApplicationFormP123456.pdf");
        expectedValues.put("applicationForm.1.applicationFilename", "ApplicationFormTMRMBISING01-2012-999999.pdf");

        expectedValues.put("mergedApplication.1.serverFilename", "MergedApplicationFormP123456.pdf");
        expectedValues.put("mergedApplication.1.applicationFilename", "MergedApplicationFormTMRMBISING01-2012-999999.pdf");
        
        expectedValues.put("transcript.1.applicationFilename", "My Proof of Award.pdf");
        expectedValues.put("transcript.1.serverFilename", RANDOM_FILENAME);
        
        expectedValues.put("transcript.2.applicationFilename", "My Proof of Award.pdf");
        expectedValues.put("transcript.2.serverFilename", RANDOM_FILENAME);

        expectedValues.put("reference.1.applicationFilename", "References.1.pdf");
        expectedValues.put("reference.1.serverFilename", RANDOM_FILENAME);
        
        expectedValues.put("reference.2.applicationFilename", "References.2.pdf");
        expectedValues.put("reference.2.serverFilename", RANDOM_FILENAME);
        
        expectedValues.put("researchProposal.1.applicationFilename", "My Personal Statement (v1.0).pdf");
        expectedValues.put("researchProposal.1.serverFilename", RANDOM_FILENAME);

        expectedValues.put("englishLanguageTestCertificate.1.applicationFilename", "Language Qualification - My Name.pdf");
        expectedValues.put("englishLanguageTestCertificate.1.serverFilename", RANDOM_FILENAME);
        
        expectedValues.put("curriculumVitae.1.applicationFilename", "My CV.pdf");
        expectedValues.put("curriculumVitae.1.serverFilename", RANDOM_FILENAME);
    }
}
