package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RolePropertyEditorTest {
	
	private RolePropertyEditor editor;
	private RoleDAO roleDAOMock;
	
	@Test	
	public void shouldLoadByAuthorityAndSetAsValue(){
		Role role = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		EasyMock.expect(roleDAOMock.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(role);
		EasyMock.replay(roleDAOMock);
		
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
		editor.setValue(new RoleBuilder().toRole());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnAuthorityAsEnum(){			
		editor.setValue(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole());
		assertEquals("ADMINISTRATOR", editor.getAsText());
	}
	
	@Before
	public void setup(){
		roleDAOMock = EasyMock.createMock(RoleDAO.class);
		editor = new RolePropertyEditor(roleDAOMock);
	}
}
