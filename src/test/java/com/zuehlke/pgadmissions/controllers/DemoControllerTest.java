package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.temporary.User;

public class DemoControllerTest {

	@Test
	public void shouldReturnJspViewForEmptyPath() {
		String jspViewName = "view";
		DemoController controller = new DemoController(jspViewName, null);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/");
		assertEquals(jspViewName, controller.getPage(request, new ModelMap()));
	}
	
	@Test
	public void shouldReturnVeloCityViewForHomePath() {
		String velocityViewName = "view";
		DemoController controller = new DemoController(null, velocityViewName);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/home");
		assertEquals(velocityViewName, controller.getPage(request, new ModelMap()));
	}


	@Test
	public void shoudAddUserObjectToModel(){
		DemoController controller = new DemoController(null, null);
		ModelMap modelMap = new ModelMap();
		controller.getPage(new MockHttpServletRequest(), modelMap);
		assertNotNull(modelMap.get("user"));
		User user = (User) modelMap.get("user");
		assertEquals("bob", user.getFirstName());
		assertEquals("smith", user.getLastName());
		assertEquals(3, user.getPhoneNumbers().size());
		
		assertEquals("office", user.getPhoneNumbers().get(0).getName());
		assertEquals("0123 456 789", user.getPhoneNumbers().get(0).getNumber());
		
		assertEquals("home", user.getPhoneNumbers().get(1).getName());
		assertEquals("0123 567 890", user.getPhoneNumbers().get(1).getNumber());
		
		assertEquals("mobile", user.getPhoneNumbers().get(2).getName());
		assertEquals("0123 678 901", user.getPhoneNumbers().get(2).getNumber());
	}
	
	
	
}
