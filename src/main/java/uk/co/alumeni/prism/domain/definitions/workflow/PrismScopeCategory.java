package uk.co.alumeni.prism.domain.definitions.workflow;

public enum PrismScopeCategory {

    APPLICATION, //
    OPPORTUNITY, //
    ORGANIZATION, //
    SYSTEM; //

    public boolean hasOpportunityTypeConfigurations() {
        return this.equals(APPLICATION) || this.equals(OPPORTUNITY);
    }

}
