package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;

public class TelephoneBuilder {

	private Integer id;
	private PhoneType telephoneType;
	private String telephoneNumber;

	public TelephoneBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public TelephoneBuilder telephoneType(PhoneType telephoneType) {
		this.telephoneType = telephoneType;
		return this;
	}

	public TelephoneBuilder telephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
		return this;
	}

	public Telephone toTelephone() {
		Telephone telephone = new Telephone();
		telephone.setId(id);
		telephone.setTelephoneType(telephoneType);
		telephone.setTelephoneNumber(telephoneNumber);
		return telephone;
	}
}
