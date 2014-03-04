package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.AddressSectionDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.AddressUtils;
import com.zuehlke.pgadmissions.validators.AddressSectionDTOValidator;

@RequestMapping("/update")
@Controller
public class AddressController {

    private static final String APPLICATION_ADDRESS_VIEW = "/private/pgStudents/form/components/address_details";
    private final ApplicationsService applicationService;
    private final AddressSectionDTOValidator addressSectionDTOValidator;
    private final DomicileService domicileService;
    private final UserService userService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    private final DomicilePropertyEditor domicilePropertyEditor;

    public AddressController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public AddressController(ApplicationsService applicationService, UserService userService, DomicileService domicileService,
            DomicilePropertyEditor domicilePropertyEditor, AddressSectionDTOValidator addressSectionDTOValidator,
            ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.domicileService = domicileService;
        this.domicilePropertyEditor = domicilePropertyEditor;
        this.addressSectionDTOValidator = addressSectionDTOValidator;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    @RequestMapping(value = "/editAddress", method = RequestMethod.POST)
    public String editAddresses(@Valid AddressSectionDTO addressSectionDTO, BindingResult result, @ModelAttribute ApplicationForm applicationForm) {
 
        if (applicationForm.isDecided()) {
            throw new CannotUpdateApplicationException(applicationForm.getApplicationNumber());
        }
        
        if (result.hasErrors()) {
            return APPLICATION_ADDRESS_VIEW;
        }
        
        Address contactAddress = applicationForm.getContactAddress();
        
        if (contactAddress == null) {
            contactAddress = new Address();
            applicationForm.setContactAddress(contactAddress);
        }
        
        contactAddress.setDomicile(addressSectionDTO.getContactAddressDomicile());
        contactAddress.setAddress1(addressSectionDTO.getContactAddress1());
        contactAddress.setAddress2(addressSectionDTO.getContactAddress2());
        contactAddress.setAddress3(addressSectionDTO.getContactAddress3());
        contactAddress.setAddress4(addressSectionDTO.getContactAddress4());
        contactAddress.setAddress5(addressSectionDTO.getContactAddress5());

        Address currentAddress = applicationForm.getCurrentAddress();
        
        if (currentAddress == null) {
            currentAddress = new Address();
            applicationForm.setCurrentAddress(currentAddress);
        }
        
        currentAddress.setDomicile(addressSectionDTO.getCurrentAddressDomicile());
        currentAddress.setAddress1(addressSectionDTO.getCurrentAddress1());
        currentAddress.setAddress2(addressSectionDTO.getCurrentAddress2());
        currentAddress.setAddress3(addressSectionDTO.getCurrentAddress3());
        currentAddress.setAddress4(addressSectionDTO.getCurrentAddress4());
        currentAddress.setAddress5(addressSectionDTO.getCurrentAddress5());

        applicationService.save(applicationForm);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, getCurrentUser(), ApplicationUpdateScope.ALL_USERS);

        return "redirect:/update/getAddress?applicationId=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/getAddress", method = RequestMethod.GET)
    public String getAddressView() {
        return APPLICATION_ADDRESS_VIEW;
    }

    @InitBinder(value = "addressSectionDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(addressSectionDTOValidator);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException();
        }
        return application;
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("addressSectionDTO")
    public AddressSectionDTO getAddressDTO(String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        AddressSectionDTO sectionDTO = new AddressSectionDTO();
        sectionDTO.setApplication(applicationForm);
        Address contactAddress = applicationForm.getContactAddress();
        if (contactAddress != null) {
            sectionDTO.setContactAddressDomicile(contactAddress.getDomicile());
            sectionDTO.setContactAddress1(contactAddress.getAddress1());
            sectionDTO.setContactAddress2(contactAddress.getAddress2());
            sectionDTO.setContactAddress3(contactAddress.getAddress3());
            sectionDTO.setContactAddress4(contactAddress.getAddress4());
            sectionDTO.setContactAddress5(contactAddress.getAddress5());
        }

        Address currentAddress = applicationForm.getCurrentAddress();
        if (currentAddress != null) {
            sectionDTO.setCurrentAddressDomicile(currentAddress.getDomicile());
            sectionDTO.setCurrentAddress1(currentAddress.getAddress1());
            sectionDTO.setCurrentAddress2(currentAddress.getAddress2());
            sectionDTO.setCurrentAddress3(currentAddress.getAddress3());
            sectionDTO.setCurrentAddress4(currentAddress.getAddress4());
            sectionDTO.setCurrentAddress5(currentAddress.getAddress5());
        }
        if (AddressUtils.addressesEqual(contactAddress, currentAddress)) {
            sectionDTO.setSameAddress(true);
        }
        return sectionDTO;
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomiciles();
    }

}