package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AddressSectionDTOValidator;

public class AddressControllerTest {

    private RegisteredUser currentUser;
    private ApplicationsService applicationsServiceMock;
    private AddressSectionDTOValidator addressSectionValidatorMock;
    private AddressController controller;

    private DomicileService domicileServiceMock;
    private DomicilePropertyEditor domicilePropertyEditor;
    private UserService userServiceMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).build();

        AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.replay(applicationsServiceMock, errors);
        controller.editAddresses(addressSectionDTO, errors, applicationForm);
        EasyMock.verify(applicationsServiceMock, errors);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
        currentUser.getRoles().clear();
        controller.editAddresses(null, null, null);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
        currentUser.getRoles().clear();
        controller.getAddressView();
    }

    @Test
    public void shouldReturnAddressView() {
        assertEquals("/private/pgStudents/form/components/address_details", controller.getAddressView());
    }

    @Test
    public void shouldReturnApplicationForm() {
        currentUser = EasyMock.createMock(RegisteredUser.class);

        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("1");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserCAnnotSeeApplFormOnGet() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
        EasyMock.replay(applicationsServiceMock, currentUser);
        controller.getApplicationForm("1");

    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(addressSectionValidatorMock);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldPopulateDTOFromApplicationFromAddresses() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        Domicile domicileOne = new DomicileBuilder().id(1).build();
        Address addressOne = new AddressBuilder().id(1).address1("location1").address2("location1-line2").domicile(domicileOne).build();

        Domicile domicileTwo = new DomicileBuilder().id(2).build();
        Address addressTwo = new AddressBuilder().id(2).address1("location2").address2("location2-line2").domicile(domicileTwo).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).contactAddress(addressOne).currentAddress(addressTwo).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);
        AddressSectionDTO returnedAddress = controller.getAddressDTO("1");
        assertEquals("location1", returnedAddress.getContactAddress1());
        assertEquals("location1-line2", returnedAddress.getContactAddress2());
        assertEquals(domicileOne, returnedAddress.getContactAddressDomicile());

        assertEquals("location2", returnedAddress.getCurrentAddress1());
        assertEquals("location2-line2", returnedAddress.getCurrentAddress2());
        assertEquals(domicileTwo, returnedAddress.getCurrentAddressDomicile());
        assertFalse(returnedAddress.isSameAddress());
    }

    @Test
    public void shouldGetDTOWithSameFlagSetIfAddressesIdentical() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        Domicile domicileOne = new DomicileBuilder().id(1).build();
        Address addressOne = new AddressBuilder().id(1).address1("location1").domicile(domicileOne).build();

        Address addressTwo = new AddressBuilder().id(2).address1("location1").domicile(domicileOne).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).contactAddress(addressOne).currentAddress(addressTwo).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);

        AddressSectionDTO returnedAddress = controller.getAddressDTO("1");

        assertEquals("location1", returnedAddress.getContactAddress1());
        assertEquals(domicileOne, returnedAddress.getContactAddressDomicile());

        assertEquals("location1", returnedAddress.getCurrentAddress1());
        assertEquals(domicileOne, returnedAddress.getCurrentAddressDomicile());
        assertTrue(returnedAddress.isSameAddress());
    }

    @Test
    public void shouldPopulateDTOWithNoDataIfApplicationFormAddressesNull() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);

        AddressSectionDTO returnedAddress = controller.getAddressDTO("1");
        assertEquals(applicationForm, returnedAddress.getApplication());
        assertNull(returnedAddress.getContactAddress1());
        assertNull(returnedAddress.getContactAddressDomicile());

        assertNull(returnedAddress.getCurrentAddress1());
        assertNull(returnedAddress.getCurrentAddressDomicile());
        assertFalse(returnedAddress.isSameAddress());
    }

    @Test
    public void shouldReturnMessage() {
        assertEquals("bob", controller.getMessage("bob"));

    }

    @Test
    public void shouldCreateAndSetNewAddressesAndSaveIfNoErrors() {
        Domicile domicileOne = new DomicileBuilder().id(1).build();
        Domicile domicileTwo = new DomicileBuilder().id(2).build();

        AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
        addressSectionDTO.setContactAddressDomicile(domicileOne);
        addressSectionDTO.setContactAddress1("location1");

        addressSectionDTO.setCurrentAddressDomicile(domicileTwo);
        addressSectionDTO.setCurrentAddress1("location2");

        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(false);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();

        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(applicationsServiceMock, errors);
        String view = controller.editAddresses(addressSectionDTO, errors, applicationForm);
        EasyMock.verify(applicationsServiceMock, errors);

        assertEquals("location1", applicationForm.getContactAddress().getAddress1());
        assertEquals(domicileOne, applicationForm.getContactAddress().getDomicile());

        assertEquals("location2", applicationForm.getCurrentAddress().getAddress1());
        assertEquals(domicileTwo, applicationForm.getCurrentAddress().getDomicile());

        assertEquals("redirect:/update/getAddress?applicationId=ABC", view);
    }

    @Test
    public void shouldUpdateExistingAddressesAndSaveIfNoErrors() {
        Domicile domicileOne = new DomicileBuilder().id(1).build();
        Domicile domicileTwo = new DomicileBuilder().id(2).build();
        Domicile domicileThree = new DomicileBuilder().id(3).build();
        Domicile domicileFour = new DomicileBuilder().id(4).build();

        Address addressOne = new AddressBuilder().id(1).address1("location3").domicile(domicileThree).build();

        Address addressTwo = new AddressBuilder().id(2).address1("location4").domicile(domicileFour).build();

        AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
        addressSectionDTO.setContactAddressDomicile(domicileOne);
        addressSectionDTO.setContactAddress1("location1");

        addressSectionDTO.setCurrentAddressDomicile(domicileTwo);
        addressSectionDTO.setCurrentAddress1("location2");

        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(false);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").currentAddress(addressOne).contactAddress(addressTwo)
                .build();

        applicationsServiceMock.save(applicationForm);
        
        EasyMock.replay(applicationsServiceMock, errors);
        String view = controller.editAddresses(addressSectionDTO, errors, applicationForm);
        EasyMock.verify(applicationsServiceMock, errors);

        assertEquals("location1", applicationForm.getContactAddress().getAddress1());
        assertEquals(domicileOne, applicationForm.getContactAddress().getDomicile());
        assertSame(addressTwo, applicationForm.getContactAddress());

        assertEquals("location2", applicationForm.getCurrentAddress().getAddress1());
        assertEquals(domicileTwo, applicationForm.getCurrentAddress().getDomicile());
        assertSame(addressOne, applicationForm.getCurrentAddress());

        assertEquals("redirect:/update/getAddress?applicationId=ABC", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        AddressSectionDTO addressSectionDTO = new AddressSectionDTO();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(true);
        EasyMock.replay(applicationsServiceMock, errors);
        String view = controller.editAddresses(addressSectionDTO, errors, new ApplicationFormBuilder().build());
        EasyMock.verify(applicationsServiceMock);
        assertEquals("/private/pgStudents/form/components/address_details", view);
    }

    @Test
    public void shouldReturnAllEnabledCountries() {
        List<Domicile> domicileList = Arrays.asList(new DomicileBuilder().id(1).enabled(true).build(), new DomicileBuilder().id(2).enabled(false).build());
        EasyMock.expect(domicileServiceMock.getAllEnabledDomiciles()).andReturn(Collections.singletonList(domicileList.get(0)));
        EasyMock.replay(domicileServiceMock);
        List<Domicile> allDomicile = controller.getAllEnabledDomiciles();
        assertEquals(1, allDomicile.size());
        assertEquals(domicileList.get(0), allDomicile.get(0));
    }

    @Before
    public void setUp() {
        domicilePropertyEditor = EasyMock.createMock(DomicilePropertyEditor.class);
        domicileServiceMock = EasyMock.createMock(DomicileService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);

        addressSectionValidatorMock = EasyMock.createMock(AddressSectionDTOValidator.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        controller = new AddressController(applicationsServiceMock, userServiceMock, domicileServiceMock, domicilePropertyEditor, addressSectionValidatorMock,
                applicationFormUserRoleServiceMock);

        currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);

    }

}
