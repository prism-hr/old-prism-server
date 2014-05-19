package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class CommentAssignedUserPropertyEditor extends PropertyEditorSupport {

    private final UserService userService;
    private final ApplicationFormService applicationsService;
    private final EncryptionHelper encryptionHelper;

    public CommentAssignedUserPropertyEditor() {
        this(null, null, null);
    }

    @Autowired
    public CommentAssignedUserPropertyEditor(UserService userService, ApplicationFormService applicationsService,//
            EncryptionHelper encryptionHelper) {
        this.userService = userService;
        this.applicationsService = applicationsService;
        this.encryptionHelper = encryptionHelper;
    }

    @Override
    public String getAsText() {
        return null;
    }

    @Override
    public void setAsText(String strAppAndUserId) throws IllegalArgumentException {
        if (StringUtils.isBlank(strAppAndUserId)) {
            setValue(null);
            return;
        }
        String[] split = strAppAndUserId.split("\\|");
        if (split.length < 2 || split.length > 3) {
            throw new IllegalArgumentException();
        }
        String appId = split[0];
        Integer userId = encryptionHelper.decryptToInteger(split[1]);
        User user = userService.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("no such user: " + split[1]);
        }

        Application applicationForm = applicationsService.getByApplicationNumber(appId);
        if (applicationForm == null) {
            throw new IllegalArgumentException("no such applications: " + split[0]);
        }

        CommentAssignedUser assignedUser = new CommentAssignedUser();
        assignedUser.setUser(user);
        if (split.length == 3 && "primary".equals(split[2])) {
            assignedUser.setPrimary(true);
        }

        setValue(assignedUser);
    }

}
