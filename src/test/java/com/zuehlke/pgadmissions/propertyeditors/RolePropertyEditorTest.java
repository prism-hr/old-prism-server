package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.RoleService;

public class RolePropertyEditorTest {
	
	private RolePropertyEditor editor;
	private RoleService roleServiceMock;
	
	@Test	
	public void shouldLoadByAuthorityAndSetAsValue(){
		Role role = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
		EasyMock.expect(roleServiceMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role);
		EasyMock.replay(roleServiceMock);
		
		editor.setAsText("ADMINISTRATOR");
		assertSame(role, editor.getValue());
	}
	
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnNullIfValueIdIsNull(){			
		editor.setValue(new RoleBuilder().build());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnAuthorityAsEnum(){			
		editor.setValue(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build());
		assertEquals("ADMINISTRATOR", editor.getAsText());
	}
	
	@Before
	public void setup(){
		roleServiceMock = EasyMock.createMock(RoleService.class);
		editor = new RolePropertyEditor(roleServiceMock);
	}
}
