package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserPropertyEditor extends PropertyEditorSupport {

    private final UserService userService;
    private final EncryptionHelper encryptionHelper;

    public UserPropertyEditor() {
        this(null, null);
    }

    @Autowired
    public UserPropertyEditor(UserService userService, EncryptionHelper encryptionHelper) {
        this.userService = userService;
        this.encryptionHelper = encryptionHelper;

    }

    @Override
    public void setAsText(String strId) throws IllegalArgumentException {
        if (StringUtils.isBlank(strId)) {
            setValue(null);
            return;
        }
        setValue(userService.getById(encryptionHelper.decryptToInteger(strId)));

    }

    @Override
    public String getAsText() {
        if (getValue() == null || ((RegisteredUser) getValue()).getId() == null) {
            return null;
        }
        return encryptionHelper.encrypt(((RegisteredUser) getValue()).getId());
    }

}
