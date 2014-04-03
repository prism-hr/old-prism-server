package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.QualificationInstitutionService;
import com.zuehlke.pgadmissions.services.UserService;

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
    private QualificationInstitutionService qualificationInstitutionService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @TestedObject
    private QualificationInstitutionsController controller = new QualificationInstitutionsController();

    @Before
    public void setup() {
        controller.customizeJsonSerializer();
    }

    @Test
    public void shouldGetInstitutions() {
        Domicile domicile = new DomicileBuilder().id(0).code("UK").enabled(true).name("United Kingdom").build();
        Institution institution1 = new QualificationInstitutionBuilder().id(2).enabled(true).name("University of London").domicileCode("UK")
                .code("ABC").build();
        Institution institution2 = new QualificationInstitutionBuilder().id(3).enabled(true).name("University of Cambridge").domicileCode("UK")
                .code("ABCD").build();

        expect(encryptionHelper.decryptToInteger("0")).andReturn(0);
        expect(domicileDAO.getDomicileById(0)).andReturn(domicile);
        expect(qualificationInstitutionService.getEnabledInstitutionsByDomicileCode(domicile.getCode())).andReturn(Arrays.asList(institution1, institution2));

        replay();
        String institutions = controller.getInstitutions("0");
        verify();

        assertEquals("[{\"code\":\"ABC\",\"name\":\"University of London\"},{\"code\":\"ABCD\",\"name\":\"University of Cambridge\"}]", institutions);
    }

    @Test
    public void shouldGetUserCategorizedInstitutions() {
        Domicile domicile = new DomicileBuilder().id(0).code("UK").enabled(true).name("United Kingdom").build();
        Institution institution1 = new QualificationInstitutionBuilder().id(2).enabled(true).name("University of London").domicileCode("UK")
                .code("ABC").build();
        Institution institution2 = new QualificationInstitutionBuilder().id(3).enabled(true).name("University of Cambridge").domicileCode("UK")
                .code("ABCD").build();
        RegisteredUser user = new RegisteredUser();

        expect(encryptionHelper.decryptToInteger("0")).andReturn(0);
        expect(domicileDAO.getDomicileById(0)).andReturn(domicile);
        expect(userService.getCurrentUser()).andReturn(user);
        expect(qualificationInstitutionService.getEnabledInstitutionsByDomicileCode(domicile.getCode())).andReturn(
                Lists.newArrayList(institution1, institution2));

        replay();
        String institutions = controller.getAdministratorInstitutions("0");
        verify();

        assertEquals(
                "{\"userInstitutions\":[{\"code\":\"ABC\",\"name\":\"University of London\"}],\"otherInstitutions\":[{\"code\":\"ABCD\",\"name\":\"University of Cambridge\"}]}",
                institutions);
    }

}
