package com.zuehlke.pgadmissions.domain.enums;

public enum PrismActionEnhancementType {

    APPLICATION_EDIT_ALL_DATA(PrismScope.APPLICATION), //
    APPLICATION_EDIT_EXPORT_DATA(PrismScope.APPLICATION),
    APPLICATION_EDIT_REFERENCE_DATA(PrismScope.APPLICATION), //
    APPLICATION_VIEW_CREATOR_DATA(PrismScope.APPLICATION), //
    APPLICATION_VIEW_EXPORT_DATA(PrismScope.APPLICATION),
    APPLICATION_VIEW_REFERENCE_DATA(PrismScope.APPLICATION);
    
    private PrismScope scope;
    
    private PrismActionEnhancementType(PrismScope scope) {
        this.scope = scope;
    }

    public PrismScope getScope() {
        return scope;
    }

}
