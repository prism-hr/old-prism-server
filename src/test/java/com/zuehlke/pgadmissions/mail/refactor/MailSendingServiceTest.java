package com.zuehlke.pgadmissions.mail.refactor;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_PASSWORD_CONFIRMATION;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.AssertTrue;

import junit.framework.Assert;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.PrismMailMessageException;
import com.zuehlke.pgadmissions.utils.Environment;

public class MailSendingServiceTest {
	
	private MailSendingService service;
	
	private TemplateAwareMailSender mockMailSender;
	
	@Before
	public void setup() {
		mockMailSender = createMock(TemplateAwareMailSender.class);
		service = new MailSendingService(mockMailSender);
	}
	
	@Test
	public void resetPasswordShouldSuccessfullySendMessage() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		String newPassword = "password";
		Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("newPassword", newPassword);
        model.put("host", Environment.getInstance().getApplicationHostName());
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		expect(mockMailSender.resolveMessage("user.password.reset", (Object[])null)).andReturn("New Password for UCL Prism");
		
		replay(mockMailSender);
		service.sendResetPasswordMessage(user, newPassword);
		verify(mockMailSender);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		assertEquals("New Password for UCL Prism", message.getSubjectCode());
		assertModelEquals(model, message.getModel());
	}
	
	@Test(expected = PrismMailMessageException.class)
	public void resetPasswordShouldThrowExceptionIfUserIsNull() throws Exception {
		String newPassword = "password";
		
		mockMailSender.sendEmail(isA(PrismEmailMessage.class));
		
		expect(mockMailSender.resolveMessage("user.password.reset", (Object[])null)).andReturn("New Password for UCL Prism");
		
		replay(mockMailSender);
		service.sendResetPasswordMessage(null, newPassword);
		verify(mockMailSender);
	}
	
	@Test(expected = PrismMailMessageException.class)
	public void resetPasswordShouldThrowExceptionIfSenderFails() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		String newPassword = "password";
		
		expect(mockMailSender.resolveMessage("user.password.reset", (Object[])null)).andReturn("New Password for UCL Prism");

		mockMailSender.sendEmail(isA(PrismEmailMessage.class));
		expectLastCall().andThrow(new Exception());
		
		replay(mockMailSender);
		service.sendResetPasswordMessage(user, newPassword);
		verify(mockMailSender);
	}
	
	private void assertModelEquals(Map<String, Object> expected, Map<String, Object> actual) {
		for (Map.Entry<String, Object> entry: expected.entrySet()) {
			assertTrue(actual.containsKey(entry.getKey()));
			assertEquals(entry.getValue(), actual.get(entry.getKey()));
		}
	}

}
