package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;

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
    private CountriesDAO countriesDAOMock;

    @Mock
    @InjectIntoByType
    private DomicileDAO domicileDAOMock;

    @Test
    public void shouldGetApplicationById() {
        ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
        EasyMock.expect(applicationFormDAOMock.getById(234)).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getById(234));
        verify();
    }

    @Test
    public void shouldGetApplicationbyApplicationNumber() {
        ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
        EasyMock.expect(applicationFormDAOMock.getByApplicationNumber("ABC")).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getByApplicationNumber("ABC"));
        verify();
    }

}