package com.zuehlke.pgadmissions.domain.imported;

import com.zuehlke.pgadmissions.domain.WeightedRelation;

public abstract class WeightedRelationImported extends WeightedRelation {

    public abstract Boolean getEnabled();

    public abstract void setEnabled(Boolean enabled);

}
