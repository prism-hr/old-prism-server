package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EmploymentPositionServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationFormService;

    @Mock
    @InjectIntoByType
    private EmploymentPositionDAO employmentPositionDAO;

    @Mock
    @InjectIntoByType
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    @TestedObject
    private EmploymentPositionService service;

    @Test
    public void shouldGetEmploymentFromDAO() {
        EmploymentPosition employmentPosition = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
        EasyMock.expect(employmentPositionDAO.getById(1)).andReturn(employmentPosition);
        EasyMock.replay(employmentPositionDAO);
        EmploymentPosition returnedEmployment = service.getById(1);
        assertEquals(employmentPosition, returnedEmployment);
    }

}
