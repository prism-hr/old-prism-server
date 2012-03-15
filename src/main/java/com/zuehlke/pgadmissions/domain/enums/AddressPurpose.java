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
		
		public static AddressPurpose fromString(String text) {
		    if (text != null) {
		      for (AddressPurpose b : AddressPurpose.values()) {
		        if (text.equalsIgnoreCase(b.displayValue)) {
		          return b;
		        }
		      }
		    }
		    return null;
		  }

		

}
