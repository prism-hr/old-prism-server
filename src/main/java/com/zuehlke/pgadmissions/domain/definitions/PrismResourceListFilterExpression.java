package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismResourceListFilterExpression {

	BETWEEN(true), //
	CONTAIN(true), //
	EQUAL(true), //
	GREATER(true), //
	LESSER(true), //
	NOT_SPECIFIED(false);

	private boolean negatable;

	private PrismResourceListFilterExpression(boolean negatable) {
		this.negatable = negatable;
	}

	public boolean isNegatable() {
		return negatable;
	}

}
