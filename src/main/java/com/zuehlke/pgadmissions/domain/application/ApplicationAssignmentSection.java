package com.zuehlke.pgadmissions.domain.application;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAssignment;
import com.zuehlke.pgadmissions.workflow.user.PrismUserReassignmentProcessor;

public abstract class ApplicationAssignmentSection<T extends PrismUserReassignmentProcessor> extends ApplicationAdvertRelationSection implements UniqueEntity,
        UserAssignment<T> {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Application getApplication();

    public abstract void setApplication(Application application);

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract Advert getAdvert();
    
    public abstract void setAdvert(Advert advert);
    
    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("application", getApplication()).addProperty("user", getUser());
    }

}
