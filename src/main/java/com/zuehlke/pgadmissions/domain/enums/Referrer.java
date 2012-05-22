package com.zuehlke.pgadmissions.domain.enums;

public enum Referrer {
	
	OPTION_1("UCL graduate study website"),
	OPTION_2("UCL graduate study newsletter"),
	OPTION_3("Student forum website"),
	OPTION_4("Facebook alert, friend or page"),
	OPTION_5("Facebook advert"),
	OPTION_6("Study programme webpage"),
	OPTION_7("Study programme newsletter"),
	OPTION_8("Referral by friend or colleague"),
	OPTION_9("Referral by detapartmental administrator"),
	OPTION_10("Referral by UCL tutor/researcher"),
	OPTION_11("Google advert/sponsored link"),
	OPTION_12("Google search query"),
	OPTION_13("FindAPhd.com"),
	OPTION_14("HotCourses.com"),
	OPTION_15("PostgraduateStudentships.co.uk"),
	OPTION_16("Jobs.ac.uk"),
	OPTION_17("FindAScholarship.com"),
	OPTION_18("Prospects.ac.uk"),
	OPTION_19("Prospects.ac.uk newsletter");
	
	private final String displayValue;
	private String freeVal;

	private Referrer(String displayValue) {
		this.displayValue = displayValue;
		this.freeVal = displayValue;
	}

	public String displayValue() {
		return displayValue;
	}
	
	public String getFreeVal() {
		return freeVal;
	}
	
	public void setFreeVal(String freeVal) {
		this.freeVal = freeVal;
	}

	public static Referrer fromString(String text) {
	    if (text != null) {
	      for (Referrer b : Referrer.values()) {
	        if (text.equalsIgnoreCase(b.displayValue)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
}
