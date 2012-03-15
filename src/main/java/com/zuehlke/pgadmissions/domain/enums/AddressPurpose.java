package com.zuehlke.pgadmissions.domain.enums;

public enum AddressPurpose {


		RESIDENCE("Residence"), WORK("Work"), EDUCATION("Education"), MARRIAGE("Marriage/Partnership"),
		TRAVELLING("Travelling(excluding holiday)");

		private final String displayValue;

		private AddressPurpose(String displayValue) {
			this.displayValue = displayValue;
		}

		public String getDisplayValue() {
			return displayValue;
		}

		

}
