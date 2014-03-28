package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.RoleService;

@Component
public class RolePropertyEditor extends PropertyEditorSupport {

    private final RoleService roleService;

    public RolePropertyEditor() {
        this(null);
    }

    @Autowired
    public RolePropertyEditor(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void setAsText(String authority) throws IllegalArgumentException {
        Authority authorityEnum = Authority.valueOf(authority);
        if (authorityEnum == null) {
            setValue(null);
            return;
        }
        Role role = roleService.getById(authorityEnum);
        setValue(role);
    }

    @Override
    public String getAsText() {
        if (getValue() == null || ((Role) getValue()).getAuthority() == null) {
            return null;
        }
        return ((Role) getValue()).getAuthority().toString();
    }
}
