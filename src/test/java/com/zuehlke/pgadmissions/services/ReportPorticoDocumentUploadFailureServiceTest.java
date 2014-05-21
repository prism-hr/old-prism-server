package com.zuehlke.pgadmissions.services;

import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationTransferErrorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ReportPorticoDocumentUploadFailureServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationTransferErrorDAO applicationFormTransferErrorDAOMock;
    
    @Mock
    @InjectIntoByType
    private ApplicationTransferDAO applicationFormTransferDAOMock;
    
    @Mock
    @InjectIntoByType
    private RoleService roleServiceMock;
    
    @TestedObject
    private ReportPorticoDocumentUploadFailureService service;
    
    @Test
    public void shouldCreateANewApplicationFormTransferObject() {
        String bookingReferenceNumber = "P000001";
        final ApplicationTransfer applicationFormTransfer = new ApplicationTransfer();
        applicationFormTransfer.setApplicationForm(new ApplicationFormBuilder().applicationNumber("abcdefgh").build());

        User superadmin1 = new User().withId(12);
        User superadmin2 = new User().withId(13);
        List<User> superadmins = Arrays.asList(superadmin1, superadmin2);
        System prismSystem = new System();
        
        EasyMock.expect(roleServiceMock.getUsersInRole(prismSystem, Authority.SYSTEM_ADMINISTRATOR)).andReturn(superadmins);
        
        String messageCode = "Portico reported that there was an error uploading the documents for application abcdefgh [errorCode=110, bookingReference=P000001]: Document file, /u02/uat/docs/U_AD_REF_DOC/P000043~REF_DOC~1.PDF, for Reference 1 already exists for application with Booking Reference P000043";
        EasyMock.expect(applicationFormTransferDAOMock.getByExternalTransferReference(bookingReferenceNumber)).andReturn(applicationFormTransfer);
        
        replay();
        service.reportPorticoUploadError(bookingReferenceNumber, "110", "Document file, /u02/uat/docs/U_AD_REF_DOC/P000043~REF_DOC~1.PDF, for Reference 1 already exists for application with Booking Reference P000043");
    }
    
}
