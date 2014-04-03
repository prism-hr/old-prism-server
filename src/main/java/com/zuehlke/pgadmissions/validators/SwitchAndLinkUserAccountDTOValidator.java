package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.SwitchAndLinkUserAccountDTO;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Component
public class SwitchAndLinkUserAccountDTOValidator extends AbstractValidator {

    private final UserService userService;
    
    private final EncryptionUtils encryptionUtils;
    
    public SwitchAndLinkUserAccountDTOValidator() {
        this(null, null);
    }
    
    @Autowired
    public SwitchAndLinkUserAccountDTOValidator(final UserService service, EncryptionUtils encryptionUtils) {
        this.userService = service;
        this.encryptionUtils = encryptionUtils;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return SwitchAndLinkUserAccountDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", EMPTY_FIELD_ERROR_MESSAGE);
        
        SwitchAndLinkUserAccountDTO userDTO = (SwitchAndLinkUserAccountDTO) target;
        RegisteredUser currentAccount = userService.getCurrentUser();
        
        if (currentAccount != null && StringUtils.isNotBlank(userDTO.getEmail())) {
            RegisteredUser secondAccount = userService.getUserByEmail(userDTO.getEmail());
            if(currentAccount.getEmail().equals(userDTO.getEmail())){
                errors.rejectValue("email", "account.link.same.email");
            }
            
            if (secondAccount == null && ESAPI.validator().isValidInput("Email", userDTO.getEmail(), "Email", 255, true)) {
                errors.rejectValue("email", "account.not.exists");
            }
            
            if (StringUtils.isNotBlank(userDTO.getCurrentPassword()) && !currentAccount.getPassword().equals(encryptionUtils.getMD5Hash(userDTO.getCurrentPassword()))) {
                errors.rejectValue("currentPassword", "account.currentpassword.notmatch");
            }

            if (StringUtils.isNotBlank(userDTO.getPassword()) && secondAccount!= null && !secondAccount.getPassword().equals(encryptionUtils.getMD5Hash(userDTO.getPassword()))) {
                errors.rejectValue("password", "account.currentpassword.notmatch");            
            }
        }
    }
}
