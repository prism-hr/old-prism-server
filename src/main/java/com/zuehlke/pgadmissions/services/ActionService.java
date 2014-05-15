package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Service
@Transactional
public class ActionService {

    @Autowired
    private ActionDAO actionDAO;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationFormService applicationService;

    @Autowired
    private EntityCreationService entityCreationService;

    @Deprecated
    public void validateAction(final ApplicationForm application, final User user, final ApplicationFormAction action) {
        if (!checkActionAvailable(application, user, action)) {
            throw new ActionNoLongerRequiredException(application.getApplicationNumber());
        }
    }

    public boolean checkActionAvailable(final ApplicationForm application, final User user, final ApplicationFormAction action) {
        return !actionDAO.getUserActionById(application.getId(), user.getId(), action).isEmpty();
    }

    public List<ActionDefinition> getUserActions(Integer applicationFormId, Integer userId) {
        return actionDAO.getUserActions(applicationFormId, userId);
    }

    public String executeAction(User user, ApplicationFormAction action, Integer scopeId) {
        String actionName = action.name();
        String scopeName = actionName.substring(0, actionName.indexOf('_'));

        Class<? extends PrismScope> scopeClass = null;
        if ("APPLICATION".equals(scopeName)) {
            scopeClass = ApplicationForm.class;
        } else if ("PROJECT".equals(scopeName)) {
            scopeClass = Project.class;
        } else if ("PROGRAM".equals(scopeName)) {
            scopeClass = Program.class;
        }

        PrismScope scope = entityService.getById(scopeClass, scopeId);
        return executeAction(user, action, scope);
    }

    public String executeAction(User user, ApplicationFormAction action, PrismScope scope) {
        Pattern createPattern = Pattern.compile("([A-Z]+)_CREATE_([A-Z]+)");
        Matcher createMatcher = createPattern.matcher(action.name());
        
        if (createMatcher.matches()) {
            String newScope = createMatcher.group(2).toLowerCase();
            entityCreationService.create(user, scope, newScope);

        }


        // ApplicationForm application = applicationService.getOrCreateApplication(user, ((Advert) scope).getId());

        String nextActionId = null;
        return "";
    }

}
