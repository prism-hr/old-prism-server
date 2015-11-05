package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.PrismFilterEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class FilterEntityRepresentation {

    private PrismFilterEntity id;

    private PrismScope scope;

    public PrismFilterEntity getId() {
        return id;
    }

    public void setId(PrismFilterEntity id) {
        this.id = id;
    }

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public FilterEntityRepresentation withScope(final PrismScope scope) {
        this.scope = scope;
        return this;
    }

    public FilterEntityRepresentation withId(final PrismFilterEntity id) {
        this.id = id;
        return this;
    }


}
