package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.mail.DataExportMailSender;

public class ReportPorticoDocumentUploadFailureServiceTest {

    private ReportPorticoDocumentUploadFailureService service;
    
    private DataExportMailSender dataExportMailSenderMock;
    
    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAOMock;
    
    private ApplicationFormTransferDAO applicationFormTransferDAOMock;
    
    private boolean saveCalled = false;

    @Before
    public void setup() {
        dataExportMailSenderMock = EasyMock.createMock(DataExportMailSender.class);
        applicationFormTransferErrorDAOMock = EasyMock.createMock(ApplicationFormTransferErrorDAO.class);
        applicationFormTransferDAOMock = EasyMock.createMock(ApplicationFormTransferDAO.class);
        service = new ReportPorticoDocumentUploadFailureService(dataExportMailSenderMock,
                applicationFormTransferDAOMock, applicationFormTransferErrorDAOMock);
        saveCalled = false;
    }
    
    @Test
    public void shouldCreateANewApplicationFormTransferObject() {
        String bookingReferenceNumber = "P000001";
        final ApplicationFormTransfer applicationFormTransfer = new ApplicationFormTransfer();
        service = new ReportPorticoDocumentUploadFailureService(dataExportMailSenderMock, applicationFormTransferDAOMock, new ApplicationFormTransferErrorDAO(null) {
            @Override
            public void save(ApplicationFormTransferError transferError) {
                saveCalled = true;
                Assert.assertEquals(applicationFormTransfer, transferError.getTransfer());
            }
        });
        
        EasyMock.expect(applicationFormTransferDAOMock.getByReceivedBookingReferenceNumber(bookingReferenceNumber)).andReturn(applicationFormTransfer);
        EasyMock.replay(applicationFormTransferDAOMock);
        
        service.saveDocumentUploadError(bookingReferenceNumber, "110", "Document file, /u02/uat/docs/U_AD_REF_DOC/P000043~REF_DOC~1.PDF, for Reference 1 already exists for application with Booking Reference P000043");
        
        EasyMock.verify(applicationFormTransferDAOMock);
        Assert.assertTrue("ApplicationFormTransferErrorDAO.save() has not been called!", saveCalled);
    }
    
}
