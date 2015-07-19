package com.zuehlke.pgadmissions.domain.user;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.WeightedRelation;

public abstract class UserImportedEntityRelation extends WeightedRelation implements UniqueEntity {

    public abstract User getUser();

    public abstract void setUser(User user);

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("user", getUser());
    }

}
