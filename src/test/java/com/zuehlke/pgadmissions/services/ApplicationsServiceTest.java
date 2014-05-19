package com.zuehlke.pgadmissions.services;

import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Application;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicationsServiceTest {

    @TestedObject
    private ApplicationFormService applicationsService;

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private ProgramDAO programDAOMock;

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Test
    public void shouldGetApplicationById() {
        Application application = EasyMock.createMock(Application.class);
        EasyMock.expect(applicationFormDAOMock.getById(234)).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getById(234));
        verify();
    }

    @Test
    public void shouldGetApplicationbyApplicationNumber() {
        Application application = EasyMock.createMock(Application.class);
        EasyMock.expect(applicationFormDAOMock.getByApplicationNumber("ABC")).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getByApplicationNumber("ABC"));
        verify();
    }

}