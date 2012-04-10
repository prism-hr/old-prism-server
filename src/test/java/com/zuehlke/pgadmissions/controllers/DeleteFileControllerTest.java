package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.DocumentService;

public class DeleteFileControllerTest {

	
	private DocumentService documentServiceMock;
	private DeleteFileController controller;
	private RegisteredUser currentUser;
	
	
	@Test
	public void shouldGetDocumentFromServiceAndDeleteInAsyncDelete(){		
		Document document = new DocumentBuilder().uploadedBy(currentUser).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		documentServiceMock.delete(document);
		EasyMock.replay(documentServiceMock);
		ModelAndView modelAndView = controller.asyncdelete(1);
		assertEquals("/private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("ok", modelAndView.getModel().get("message"));
		EasyMock.verify(documentServiceMock);
	}
	
	@Test
	public void shouldNOTDeleteDocumentIfUserIsNotUploadingUser(){		
		Document document = new DocumentBuilder().uploadedBy(new RegisteredUserBuilder().id(8).toUser()).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);		
		EasyMock.replay(documentServiceMock);
		ModelAndView modelAndView = controller.asyncdelete(1);
		assertEquals("/private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("ok", modelAndView.getModel().get("message"));
		EasyMock.verify(documentServiceMock);
	}
	
	@Test
	public void shouldNotFailIfDocumentIsNull(){			
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(null);		
		EasyMock.replay(documentServiceMock);
		ModelAndView modelAndView = controller.asyncdelete(1);
		assertEquals("/private/common/simpleMessage", modelAndView.getViewName());
		assertEquals("ok", modelAndView.getModel().get("message"));
		EasyMock.verify(documentServiceMock);
	}
	@Before
	public void setup() {

		documentServiceMock = EasyMock.createMock(DocumentService.class);
		controller = new DeleteFileController( documentServiceMock);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
