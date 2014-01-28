package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;

import org.easymock.EasyMock;
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
import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicationsServiceTest {

    @TestedObject
    private ApplicationsService applicationsService;

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private MailSendingService mailServiceMock;

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
    public void shouldSendSubmissionsConfirmationToApplicant() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).build();

        mailServiceMock.sendSubmissionConfirmationToApplicant(application);

        applicationFormDAOMock.save(application);

        replay();
        applicationsService.sendSubmissionConfirmationToApplicant(application);
        verify();
    }

    @Test
    public void shouldGetApplicationById() {
        ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
        EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getApplicationById(234));
        verify();
    }

    @Test
    public void shouldGetApplicationbyApplicationNumber() {
        ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
        EasyMock.expect(applicationFormDAOMock.getApplicationByApplicationNumber("ABC")).andReturn(application);

        replay();
        Assert.assertEquals(application, applicationsService.getApplicationByApplicationNumber("ABC"));
        verify();
    }

    @Test
    public void shouldFastTrackApplicationByClearingTheBatchDeadline() {
        ApplicationForm form = new ApplicationFormBuilder().applicationNumber("XXXXX").batchDeadline(new Date()).build();
        EasyMock.expect(applicationFormDAOMock.getApplicationByApplicationNumber(form.getApplicationNumber())).andReturn(form);
        replay();
        applicationsService.fastTrackApplication(form.getApplicationNumber());
        verify();
        Assert.assertNull(form.getBatchDeadline());
    }

 

    @Test
    public void shouldGetBatchDeadlineForApplication() {
        Program program = new ProgramBuilder().code("KLOP").id(1).build();
        ApplicationForm form = new ApplicationFormBuilder().program(program).build();
        Date deadline = new Date();

        EasyMock.expect(programDAOMock.getNextClosingDateForProgram(EasyMock.eq(program), EasyMock.isA(Date.class))).andReturn(deadline);

        replay();
        Date returnedDeadline = applicationsService.getBatchDeadlineForApplication(form);
        verify();

        assertSame(deadline, returnedDeadline);
    }
    
    @Test
    public void shouldTransFormUKCountriesAndDomiciles() {
        Country validCountry = new CountryBuilder().code("XK").enabled(true).build();
        Domicile validDomicile = new DomicileBuilder().code("XK").enabled(true).build();
        
        Country invalidCountry = new CountryBuilder().code("XF").enabled(true).build();
        Domicile invalidDomicile1 = new DomicileBuilder().code("XF").enabled(true).build();
        Domicile invalidDomicile2 = new DomicileBuilder().code("XI").enabled(true).build();
        Domicile invalidDomicile3 = new DomicileBuilder().code("XH").enabled(true).build();
        Domicile invalidDomicile4 = new DomicileBuilder().code("8826").enabled(true).build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().country(invalidCountry).residenceDomicile(invalidDomicile1).build();
        Address address = new AddressBuilder().domicile(invalidDomicile2).build();
        Qualification qualification1 = new QualificationBuilder().institutionCountry(invalidDomicile3).build();
        Qualification qualification2 = new QualificationBuilder().institutionCountry(invalidDomicile4).build();
        EmploymentPosition position1 = new EmploymentPositionBuilder().domicile(invalidDomicile1).toEmploymentPosition();
        EmploymentPosition position2 = new EmploymentPositionBuilder().domicile(invalidDomicile2).toEmploymentPosition();
        Referee referee1 = new RefereeBuilder().addressDomicile(invalidDomicile3).build();
        Referee referee2 = new RefereeBuilder().addressDomicile(invalidDomicile4).build();
        
        ApplicationForm application = new ApplicationFormBuilder().personalDetails(personalDetails).contactAddress(address)
                .currentAddress(address).qualifications(qualification1, qualification2).employmentPositions(position1, position2)
                .referees(referee1, referee2).build();
        
        EasyMock.expect(countriesDAOMock.getEnabledCountryByCode("XK")).andReturn(validCountry);
        EasyMock.expect(domicileDAOMock.getEnabledDomicileByCode("XK")).andReturn(validDomicile);
        
        replay();
        applicationsService.transformUKCountriesAndDomiciles(application);
        verify();
        
        assertEquals(application.getPersonalDetails().getCountry().getCode(), validCountry.getCode());
        assertEquals(application.getPersonalDetails().getResidenceCountry().getCode(), validDomicile.getCode());
        assertEquals(application.getCurrentAddress().getDomicile().getCode(), validDomicile.getCode());
        assertEquals(application.getContactAddress().getDomicile().getCode(), validDomicile.getCode());
        
        for (Qualification qualification : application.getQualifications()) {
            assertEquals(qualification.getInstitutionCountry().getCode(), validDomicile.getCode());
        }
        
        for (EmploymentPosition position : application.getEmploymentPositions()) {
            assertEquals(position.getEmployerAddress().getDomicile().getCode(), validDomicile.getCode());
        }
        
        for (Referee referee : application.getReferees()) {
            assertEquals(referee.getAddressLocation().getDomicile().getCode(), validDomicile.getCode());
        }

    }

}