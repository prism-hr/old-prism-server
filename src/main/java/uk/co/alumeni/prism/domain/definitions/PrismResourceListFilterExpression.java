package uk.co.alumeni.prism.domain.definitions;

public enum PrismResourceListFilterExpression {

    BETWEEN(true),
    CONTAIN(true),
    EQUAL(true),
    GREATER(true),
    LESSER(true),
    NOT_SPECIFIED(false);

    private boolean negatable;

    PrismResourceListFilterExpression(boolean negatable) {
        this.negatable = negatable;
    }

    public boolean isNegatable() {
        return negatable;
    }

}
