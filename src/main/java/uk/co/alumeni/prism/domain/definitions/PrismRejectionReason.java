package uk.co.alumeni.prism.domain.definitions;

public enum PrismRejectionReason implements PrismLocalizableDefinition {

    POSITION,
    COMPETITION,
    APPLICATION_INFORMATION,
    APPLICATION_INTERVIEW_ATTENDANCE,
    APPLICATION_WITHDRAWAL,
    PARTNER,
    ELIGIBILITY;

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_REJECTION_REASON_" + this.name());
    }

}
