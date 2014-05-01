package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserPropertyEditor extends PropertyEditorSupport {

    @Autowired
    private UserService userService;

    @Override
    public void setAsText(String jsonString) throws IllegalArgumentException {
            User user = new Gson().fromJson(jsonString, User.class);
            User persistedUser = userService.getUserByEmail(user.getEmail());
            setValue(Objects.firstNonNull(persistedUser, user));
    }

    @Override
    public String getAsText() {
        throw new UnsupportedOperationException();
    }
}
