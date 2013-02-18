package com.zuehlke.pgadmissions.controllers.export;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.services.ReportPorticoDocumentUploadFailureService;

public class ReportPorticoDocumentUploadFailureControllerTest {
    
    private ReportPorticoDocumentUploadFailureController controller;
    
    private ReportPorticoDocumentUploadFailureService serviceMock;
    
    @Before
    public void setup() {
        serviceMock = EasyMock.createMock(ReportPorticoDocumentUploadFailureService.class);
        controller = new ReportPorticoDocumentUploadFailureController(serviceMock);
    }
    
    @Test
    public void shouldReturnNOKIfActivationCodeIsWrong() {
        Assert.assertEquals("NOK", controller.reportError("P000001", "110", "fooBar", "121212"));
    }
    
    @Test
    public void shouldSaveErrorsAndSendEmailToSuperAdministrators() {
        String activationCode = "6a219fb0-6acb-11e2-bcfd-0800200c9a66";
        String bookingReference = "P000001";
        String errorCode = "110";
        String message = "fooBar";
        
        serviceMock.saveDocumentUploadError(bookingReference, errorCode, message);
        serviceMock.sendErrorMessageToSuperAdministrators(String
                        .format("Portico reported that there was an error uploading the documents [errorCode=%s, bookingReference=%s]: %s",
                                StringUtils.trimToEmpty(errorCode), StringUtils.trimToEmpty(bookingReference),
                                StringUtils.trimToEmpty(message)));
        EasyMock.replay(serviceMock);

        Assert.assertEquals("OK", controller.reportError(bookingReference, errorCode, message, activationCode));
        
        EasyMock.verify(serviceMock);
    }
}
