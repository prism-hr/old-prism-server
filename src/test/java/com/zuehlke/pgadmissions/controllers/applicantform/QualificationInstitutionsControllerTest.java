package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.io.IOException;
import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationInstitutionsControllerTest {

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelper;

    @Mock
    @InjectIntoByType
    private DomicileDAO domicileDAO;

    @Mock
    @InjectIntoByType
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @TestedObject
    private QualificationInstitutionsController controller;

    @Test
    public void shouldReturnInstitutionsAsJson() throws IOException {
        Domicile domicile = new DomicileBuilder().id(0).code("UK").enabled(true).name("United Kingdom").build();
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().id(2).enabled(true).name("University of London").domicileCode("UK")
                .code("ABC").build();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().id(3).enabled(true).name("University of Cambridge").domicileCode("UK")
                .code("ABCD").build();

        EasyMock.expect(encryptionHelper.decryptToInteger("0")).andReturn(0);
        EasyMock.expect(domicileDAO.getDomicileById(0)).andReturn(domicile);
        EasyMock.expect(qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode(domicile.getCode())).andReturn(
                Arrays.asList(institution1, institution2));

        replay();
        String institutions = controller.getInstitutions("0");
        verify();

        assertEquals("[{\"code\":\"ABC\",\"name\":\"University of London\"},{\"code\":\"ABCD\",\"name\":\"University of Cambridge\"}]", institutions);

    }

}
