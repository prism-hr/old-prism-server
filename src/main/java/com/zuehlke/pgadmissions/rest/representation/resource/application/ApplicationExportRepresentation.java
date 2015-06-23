package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.UserInstitutionIdentityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceStudyOptionInstanceRepresentation;

public class ApplicationExportRepresentation extends ApplicationRepresentation {

    private UserInstitutionIdentityRepresentation userInstitutionIdentity;

    private ResourceStudyOptionInstanceRepresentation resourceStudyOptionInstance;

    public UserInstitutionIdentityRepresentation getUserInstitutionIdentity() {
        return userInstitutionIdentity;
    }

    public void setUserInstitutionIdentity(UserInstitutionIdentityRepresentation userInstitutionIdentity) {
        this.userInstitutionIdentity = userInstitutionIdentity;
    }

    public ResourceStudyOptionInstanceRepresentation getResourceStudyOptionInstance() {
        return resourceStudyOptionInstance;
    }

    public void setResourceStudyOptionInstance(ResourceStudyOptionInstanceRepresentation resourceStudyOptionInstance) {
        this.resourceStudyOptionInstance = resourceStudyOptionInstance;
    }

}
