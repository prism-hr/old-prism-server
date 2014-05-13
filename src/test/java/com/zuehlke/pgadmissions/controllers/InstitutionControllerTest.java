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

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class InstitutionControllerTest {

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelper;

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private InstitutionService institutionService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @TestedObject
    private InstitutionController controller = new InstitutionController();

    @Before
    public void setup() {
        controller.customizeJsonSerializer();
    }

    @Test
    public void shouldGetInstitutions() {
        Domicile domicile = new DomicileBuilder().id(0).code("UK").enabled(true).name("United Kingdom").build();
        ImportedInstitution institution1 = new ImportedInstitution().withId(2).withEnabled(true).withName("University of London").withDomicile(domicile)
                .withCode("ABC");
        ImportedInstitution institution2 = new ImportedInstitution().withId(3).withEnabled(true).withName("University of Cambridge").withDomicile(domicile)
                .withCode("ABCD");

        expect(importedEntityService.getDomicileById(0)).andReturn(domicile);
        expect(institutionService.getEnabledImportedInstitutionsByDomicile(domicile)).andReturn(Arrays.asList(institution1, institution2));

        replay();
        String institutions = controller.getInstitutions(0);
        verify();

        assertEquals("[{\"code\":\"ABC\",\"name\":\"University of London\"},{\"code\":\"ABCD\",\"name\":\"University of Cambridge\"}]", institutions);
    }

}
