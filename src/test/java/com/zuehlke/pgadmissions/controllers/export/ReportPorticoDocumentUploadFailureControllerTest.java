package com.zuehlke.pgadmissions.controllers.export;

import junit.framework.Assert;

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
    public void shouldReturnOKIfActivationCodeIsCorrect() {
        serviceMock.reportPorticoUploadError("P000001", "110", "fooBar");
        EasyMock.replay(serviceMock);
        Assert.assertEquals("OK", controller.reportError("P000001", "110", "fooBar", "6a219fb0-6acb-11e2-bcfd-0800200c9a66"));
        EasyMock.verify(serviceMock);
    }

}
