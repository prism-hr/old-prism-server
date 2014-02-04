package com.zuehlke.pgadmissions.utils;

import org.easymock.EasyMock;
import org.springframework.mail.javamail.JavaMailSender;

public class EasyMockFactory {

	public static JavaMailSender mockMailSender() {
		return EasyMock.createNiceMock(JavaMailSender.class);
	}
}
