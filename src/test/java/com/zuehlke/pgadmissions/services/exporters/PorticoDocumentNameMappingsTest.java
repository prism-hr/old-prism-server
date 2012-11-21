package com.zuehlke.pgadmissions.services.exporters;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PorticoDocumentNameMappingsTest {

    private static final String BOOKING_REF = "0035965";
    
    @Test
    public void getTranscriptFilenameTest() {
        Assert.assertEquals("0035965~UA_TRAN1~", PorticoDocumentNameMappings.getTranscriptFilename(BOOKING_REF, 1));
        Assert.assertEquals("0035965~UA_TRAN2~", PorticoDocumentNameMappings.getTranscriptFilename(BOOKING_REF, 2));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getTranscriptFilenameTestIdxTooBig() {
        Assert.assertEquals("0035965~UA_TRAN1~", PorticoDocumentNameMappings.getTranscriptFilename(BOOKING_REF, 3));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getTranscriptFilenameTestIdxTooLow() {
        Assert.assertEquals("0035965~UA_TRAN1~", PorticoDocumentNameMappings.getTranscriptFilename(BOOKING_REF, 0));
    }

    @Test
    public void getResearchProposalFilenameTest() {
        Assert.assertEquals("0035965~UA_RESP~", PorticoDocumentNameMappings.getResearchProposalFilename(BOOKING_REF));
    }

    @Test
    public void getEnglishLanguageCertificateFilenameTest() {
        Assert.assertEquals("0035965~UA_EQUAL1~", PorticoDocumentNameMappings.getEnglishLanguageCertificateFilename(BOOKING_REF));
    }

    @Test
    public void getCVFilenameTest() {
        Assert.assertEquals("0035965~UA_CV~", PorticoDocumentNameMappings.getCVFilename(BOOKING_REF));
    }

    @Test
    public void getAdditionalDocumentFilenameTest() {
        Assert.assertEquals("0035965~UA_PERS~", PorticoDocumentNameMappings.getAdditionalDocumentFilename(BOOKING_REF));
    }

    @Test
    public void getReferenceFilenameTest() {
        Assert.assertEquals("0035965~REF_DOC~1", PorticoDocumentNameMappings.getReferenceFilename(BOOKING_REF, 1));
        Assert.assertEquals("0035965~REF_DOC~2", PorticoDocumentNameMappings.getReferenceFilename(BOOKING_REF, 2));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getReferenceFilenameTestIdxTooBig() {
        Assert.assertEquals("0035965~REF_DOC~1", PorticoDocumentNameMappings.getReferenceFilename(BOOKING_REF, 3));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getReferenceFilenameTestIdxTooLow() {
        Assert.assertEquals("0035965~REF_DOC~1", PorticoDocumentNameMappings.getReferenceFilename(BOOKING_REF, 0));
    }

}
