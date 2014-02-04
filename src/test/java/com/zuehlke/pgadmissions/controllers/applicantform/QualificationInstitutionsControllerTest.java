package com.zuehlke.pgadmissions.controllers.applicantform;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

public class QualificationInstitutionsControllerTest {

    @Test
    public void shouldReturnInstitutionsAsJson() throws IOException {
        DomicileDAO domicileDAO = EasyMock.createMock(DomicileDAO.class);
        QualificationInstitutionDAO qualificationInstitutionDAO = EasyMock.createMock(QualificationInstitutionDAO.class);
        Domicile domicile = new DomicileBuilder().id(0).code("UK").enabled(true).name("United Kingdom").build();
        EncryptionHelper encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().id(2).enabled(true).name("University of London").domicileCode("UK").code("ABC").build();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().id(3).enabled(true).name("University of Cambridge").domicileCode("UK").code("ABCD").build();
        
        EasyMock.expect(encryptionHelper.decryptToInteger("0")).andReturn(0);
        EasyMock.expect(domicileDAO.getDomicileById(0)).andReturn(domicile);
        EasyMock.expect(qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode(domicile.getCode())).andReturn(Arrays.asList(institution1, institution2));
        EasyMock.expect(encryptionHelper.encrypt(2)).andReturn("2");
        EasyMock.expect(encryptionHelper.encrypt(3)).andReturn("3");
        
        EasyMock.replay(domicileDAO, qualificationInstitutionDAO, encryptionHelper);
        
        QualificationInstitutionsController controller = new QualificationInstitutionsController(encryptionHelper, domicileDAO, qualificationInstitutionDAO);
        String institutions = controller.getInstitutions("0");
        
        Assert.assertEquals("[[\"2\",\"ABC\",\"University of London\",\"UK\"],[\"3\",\"ABCD\",\"University of Cambridge\",\"UK\"]]", institutions);
        
        EasyMock.verify(domicileDAO, qualificationInstitutionDAO, encryptionHelper);
    }
    
}
