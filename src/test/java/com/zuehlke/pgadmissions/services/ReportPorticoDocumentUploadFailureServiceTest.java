package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.eq;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.unitils.easymock.EasyMockUnitils;

import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;

public class ReportPorticoDocumentUploadFailureServiceTest {

    
    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAOMock;
    
    private ApplicationFormTransferDAO applicationFormTransferDAOMock;
    
    private MailSendingService mailServiceMock;
    
    private RoleService roleServiceMock;
    
    private ReportPorticoDocumentUploadFailureService service;
    
    private boolean saveCalled = false;

    @Test
    public void shouldCreateANewApplicationFormTransferObject() {
        String bookingReferenceNumber = "P000001";
        final ApplicationFormTransfer applicationFormTransfer = new ApplicationFormTransfer();
        applicationFormTransfer.setApplicationForm(new ApplicationFormBuilder().applicationNumber("abcdefgh").build());

        User superadmin1 = new UserBuilder().id(12).build();
        User superadmin2 = new UserBuilder().id(13).build();
        List<User> superadmins = Arrays.asList(superadmin1, superadmin2);
        EasyMock.expect(roleServiceMock.getUsersInSystemRole(Authority.SUPERADMINISTRATOR)).andReturn(superadmins);
        
        String messageCode = "Portico reported that there was an error uploading the documents for application abcdefgh [errorCode=110, bookingReference=P000001]: Document file, /u02/uat/docs/U_AD_REF_DOC/P000043~REF_DOC~1.PDF, for Reference 1 already exists for application with Booking Reference P000043";
        mailServiceMock.sendExportErrorMessage(eq(superadmins), eq(messageCode), EasyMock.isA(Date.class), applicationFormTransfer.getApplicationForm());
        EasyMock.expect(applicationFormTransferDAOMock.getByReceivedBookingReferenceNumber(bookingReferenceNumber)).andReturn(applicationFormTransfer);
        EasyMockUnitils.replay();
        
        service.reportPorticoUploadError(bookingReferenceNumber, "110", "Document file, /u02/uat/docs/U_AD_REF_DOC/P000043~REF_DOC~1.PDF, for Reference 1 already exists for application with Booking Reference P000043");
        
        Assert.assertTrue("ApplicationFormTransferErrorDAO.save() has not been called!", saveCalled);
    }
    
}
