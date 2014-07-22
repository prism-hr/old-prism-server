package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Arrays;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationServiceTest {

    @Mock
    @InjectIntoByType
    private EntityDAO entityDAOMock;

    @Mock
    @InjectIntoByType
    private ApplicationService applicationService;

    @TestedObject
    private ApplicationQualificationService service;

    @Test
    public void shouldSetFlagSendToPorticoOnSelectedQualifications() {
        ApplicationQualification qualification1 = new ApplicationQualification().withId(1).withIncludeInExport(true);
        ApplicationQualification qualification2 = new ApplicationQualification().withId(2).withIncludeInExport(true);
        ApplicationQualification qualification3 = new ApplicationQualification().withId(3).withIncludeInExport(false);
        ApplicationQualification qualification4 = new ApplicationQualification().withId(4).withIncludeInExport(false);

        Application application = new Application().withQualifications(qualification1, qualification2, qualification3, qualification4);

        EasyMock.expect(applicationService.getById(8)).andReturn(application);

        replay();
        service.selectForSendingToPortico(8, Arrays.asList(new Integer[] { 3, 4 }));

        assertTrue("SendToUcl flag has not been updated to true", qualification3.isIncludeInExport());
        assertTrue("SendToUcl flag has not been updated to true", qualification4.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.isIncludeInExport());
    }

    @Test
    public void shouldSetNoFlagSendToPorticoOnQualifications() {
        ApplicationQualification qualification1 = new ApplicationQualification().withId(1).withIncludeInExport(true);
        ApplicationQualification qualification2 = new ApplicationQualification().withId(2).withIncludeInExport(true);
        ApplicationQualification qualification3 = new ApplicationQualification().withId(3).withIncludeInExport(false);
        ApplicationQualification qualification4 = new ApplicationQualification().withId(4).withIncludeInExport(false);

        Application application = new Application().withQualifications(qualification1, qualification2, qualification3, qualification4);

        EasyMock.expect(applicationService.getById(8)).andReturn(application);

        replay();
        service.selectForSendingToPortico(8, Collections.<Integer> emptyList());

        assertFalse("SendToUcl flag has not been updated to false", qualification3.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification4.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.isIncludeInExport());
    }

}
