package com.zuehlke.pgadmissions.utils;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.domain.Address;

public class AddressUtils {
	public static boolean addressesEqual(Address contactAddress, Address currentAddress) {
		return contactAddress != null && currentAddress != null && contactAddress.getCountry().getId().equals(currentAddress.getCountry().getId())
				&& StringUtils.equals(contactAddress.getAddress1(), currentAddress.getAddress1())
				&& StringUtils.equals(contactAddress.getAddress2(), currentAddress.getAddress2())
				&& StringUtils.equals(contactAddress.getAddress3(), currentAddress.getAddress3())
				&& StringUtils.equals(contactAddress.getAddress4(), currentAddress.getAddress4())
				&& StringUtils.equals(contactAddress.getAddress5(), currentAddress.getAddress5());
	}
}
