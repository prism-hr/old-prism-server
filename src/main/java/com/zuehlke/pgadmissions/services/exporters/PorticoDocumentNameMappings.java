package com.zuehlke.pgadmissions.services.exporters;

/**
 * @deprecated This name schema for sending files to PORTICO has been deprecated on 21/11/2012
 */
@Deprecated
public class PorticoDocumentNameMappings {

    private static final String TILDA = "~";
    
    private static final String TRANSCRIPT = "UA_TRAN";
    
    private static final String RESEARCH_PROPOSAL = "UA_RESP";
    
    private static final String ENGLISH_LANGUAGE_TEST_CERTIFICATE = "UA_EQUAL1";
    
    private static final String CV = "UA_CV";
    
    private static final String ADDITIONAL_DOCUMENT = "UA_PERS";
    
    private static final String REFERENCE = "REF_DOC";
    
    private PorticoDocumentNameMappings() {
    }
    
    public static String getTranscriptFilename(String bookingReferenceNumber, int idx) {
        if (idx <= 0 || idx > 2) {
            throw new IllegalArgumentException(String.format("The supplied idx: [%s] is not valid. Index must be either 1 or 2", idx));
        }
        return String.format("%s%s%s%s%s", bookingReferenceNumber, TILDA, TRANSCRIPT, idx, TILDA);
    }
    
    public static String getResearchProposalFilename(String bookingReferenceNumber) {
        return String.format("%s%s%s%s", bookingReferenceNumber, TILDA, RESEARCH_PROPOSAL, TILDA);
    }
    
    public static String getEnglishLanguageCertificateFilename(String bookingReferenceNumber) {
        return String.format("%s%s%s%s", bookingReferenceNumber, TILDA, ENGLISH_LANGUAGE_TEST_CERTIFICATE, TILDA);
    }
    
    public static String getCVFilename(String bookingReferenceNumber) {
        return String.format("%s%s%s%s", bookingReferenceNumber, TILDA, CV, TILDA);
    }
    
    public static String getAdditionalDocumentFilename(String bookingReferenceNumber) {
        return String.format("%s%s%s%s", bookingReferenceNumber, TILDA, ADDITIONAL_DOCUMENT, TILDA);
    }
    
    public static String getReferenceFilename(String bookingReferenceNumber, int idx) {
        if (idx <= 0 || idx > 2) {
            throw new IllegalArgumentException(String.format("The supplied idx: [%s] is not valid. Index must be either 1 or 2", idx));
        }
        return String.format("%s%s%s%s%s", bookingReferenceNumber, TILDA, REFERENCE, TILDA, idx);
    }
}
