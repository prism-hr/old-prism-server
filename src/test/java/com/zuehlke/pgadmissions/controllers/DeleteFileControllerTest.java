package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;

public class DeleteFileControllerTest {

	
	private DocumentService documentServiceMock;
	private DeleteFileController controller;
	private RegisteredUser currentUser;	
	private UserService userServiceMock;
	private EncryptionHelper encryptionHelperMock;
	private ApplicationsService applicationServiceMock;
	
	

	@Test
	public void shouldGetDocumentAnapplicationAndDeletePersonalStatement(){			

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("applicationNumber")).andReturn(applicationForm);
	
		documentServiceMock.deletePersonalStatement(applicationForm);
		EasyMock.replay( applicationServiceMock, documentServiceMock);
	
		assertEquals("/private/common/ajax_OK", controller.deletePersonalStatement( "applicationNumber"));	
		EasyMock.verify(documentServiceMock);
	}
	

	@Test
	public void shouldGetDocumentAndapplicationAndDeleteCv(){			

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("applicationNumber")).andReturn(applicationForm);
	
		documentServiceMock.deleteCV(applicationForm);
		EasyMock.replay( applicationServiceMock, documentServiceMock);
	
		assertEquals("/private/common/ajax_OK", controller.deleteCV( "applicationNumber"));	
		EasyMock.verify(documentServiceMock);
	}
	
	@Test
	public void shouldGetQualificationAndDelteProofOfAward(){			

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("applicationNumber")).andReturn(applicationForm);
	
		documentServiceMock.deleteCV(applicationForm);
		EasyMock.replay( applicationServiceMock, documentServiceMock);
	
		assertEquals("/private/common/ajax_OK", controller.deleteCV( "applicationNumber"));	
		EasyMock.verify(documentServiceMock);
	}
	
	
	@Test
	public void shouldGetDocumentFromServiceAndDeleteInAsyncDelete(){		
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		Document document = new DocumentBuilder().uploadedBy(currentUser).content("aaaa".getBytes()).id(1).build();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.expect(currentUser.getId()).andReturn(99).anyTimes();
		documentServiceMock.delete(document);
		EasyMock.replay(documentServiceMock, encryptionHelperMock, currentUser);
		
		ModelAndView modelAndView = controller.asyncdelete("encryptedId");
		
		assertEquals("/private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("document.deleted", modelAndView.getModel().get("message"));
		EasyMock.verify(documentServiceMock);
	}
	
	@Test
	public void shouldNOTDeleteDocumentIfUserIsNotUploadingUser(){		
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		EasyMock.expect(currentUser.getId()).andReturn(99).anyTimes();
		Document document = new DocumentBuilder().uploadedBy(new RegisteredUserBuilder().id(8).build()).content("aaaa".getBytes()).id(1).build();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);		
		EasyMock.replay(documentServiceMock, encryptionHelperMock, currentUser);
		ModelAndView modelAndView = controller.asyncdelete("encryptedId");
		assertEquals("/private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("document.deleted", modelAndView.getModel().get("message"));
		EasyMock.verify(documentServiceMock);
	}
	
	@Test
	public void shouldNotFailIfDocumentIsNull(){	
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(null);		
		EasyMock.replay(documentServiceMock, encryptionHelperMock);
		ModelAndView modelAndView = controller.asyncdelete("encryptedId");
		assertEquals("/private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("document.deleted", modelAndView.getModel().get("message"));
		EasyMock.verify(documentServiceMock);
	}
	
	@Before
	public void setup() {
		documentServiceMock = EasyMock.createMock(DocumentService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new DeleteFileController( documentServiceMock, userServiceMock,applicationServiceMock,  encryptionHelperMock);
		
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
