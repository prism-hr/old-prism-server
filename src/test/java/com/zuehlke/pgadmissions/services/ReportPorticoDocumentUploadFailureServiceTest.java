package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;

public class ReportPorticoDocumentUploadFailureServiceTest {

    private ReportPorticoDocumentUploadFailureService service;
    
    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAOMock;
    
    private ApplicationFormTransferDAO applicationFormTransferDAOMock;
    
    private MailSendingService mailServiceMock;
    
    private UserService userServiceMock;
    
    private boolean saveCalled = false;

    @Before
    public void setup() {
        applicationFormTransferErrorDAOMock = EasyMock.createMock(ApplicationFormTransferErrorDAO.class);
        applicationFormTransferDAOMock = createMock(ApplicationFormTransferDAO.class);
        mailServiceMock = createMock(MailSendingService.class);
        userServiceMock= createMock(UserService.class);
        service = new ReportPorticoDocumentUploadFailureService(
                applicationFormTransferDAOMock, applicationFormTransferErrorDAOMock, mailServiceMock, userServiceMock);
        saveCalled = false;
    }
    
    @Test
    public void shouldCreateANewApplicationFormTransferObject() {
        String bookingReferenceNumber = "P000001";
        final ApplicationFormTransfer applicationFormTransfer = new ApplicationFormTransfer();
        applicationFormTransfer.setApplicationForm(new ApplicationFormBuilder().applicationNumber("abcdefgh").build());
        service = new ReportPorticoDocumentUploadFailureService(applicationFormTransferDAOMock, new ApplicationFormTransferErrorDAO(null) {
            @Override
            public void save(ApplicationFormTransferError transferError) {
                saveCalled = true;
                Assert.assertEquals(applicationFormTransfer, transferError.getTransfer());
            }
        }, mailServiceMock, userServiceMock);

        RegisteredUser superadmin1 = new RegisteredUserBuilder().id(12).build();
        RegisteredUser superadmin2 = new RegisteredUserBuilder().id(13).build();
        List<RegisteredUser> superadmins = Arrays.asList(superadmin1, superadmin2);
        EasyMock.expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR)).andReturn(superadmins);
        
        String messageCode = "Portico reported that there was an error uploading the documents for application abcdefgh [errorCode=110, bookingReference=P000001]: Document file, /u02/uat/docs/U_AD_REF_DOC/P000043~REF_DOC~1.PDF, for Reference 1 already exists for application with Booking Reference P000043";
        mailServiceMock.sendExportErrorMessage(eq(superadmins), eq(messageCode), EasyMock.isA(Date.class), applicationFormTransfer.getApplicationForm());
        EasyMock.expect(applicationFormTransferDAOMock.getByReceivedBookingReferenceNumber(bookingReferenceNumber)).andReturn(applicationFormTransfer);
        EasyMock.replay(applicationFormTransferDAOMock, mailServiceMock, userServiceMock);
        
        service.reportPorticoUploadError(bookingReferenceNumber, "110", "Document file, /u02/uat/docs/U_AD_REF_DOC/P000043~REF_DOC~1.PDF, for Reference 1 already exists for application with Booking Reference P000043");
        
        EasyMock.verify(applicationFormTransferDAOMock, mailServiceMock, userServiceMock);
        Assert.assertTrue("ApplicationFormTransferErrorDAO.save() has not been called!", saveCalled);
    }
    
}
