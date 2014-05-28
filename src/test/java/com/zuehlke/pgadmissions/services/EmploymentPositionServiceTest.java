package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EmploymentPositionServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationService applicationFormService;

    @Mock
    @InjectIntoByType
    private EmploymentPositionDAO employmentPositionDAO;

    @Mock
    @InjectIntoByType
    private ApplicationCopyHelper applicationFormCopyHelper;

    @TestedObject
    private EmploymentPositionService service;

    @Test
    public void shouldGetEmploymentFromDAO() {
        EmploymentPosition employmentPosition = new EmploymentPosition().withId(1);
        EasyMock.expect(employmentPositionDAO.getById(1)).andReturn(employmentPosition);
        
        EasyMockUnitils.replay();
        
        EmploymentPosition returnedEmployment = service.getById(1);
        assertEquals(employmentPosition, returnedEmployment);
    }

}
