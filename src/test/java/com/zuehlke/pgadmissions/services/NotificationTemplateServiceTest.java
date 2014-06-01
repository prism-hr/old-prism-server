package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.NotificationTemplateDAO;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateType;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class NotificationTemplateServiceTest {
    
    @Mock  
    @InjectIntoByType
	private NotificationTemplateDAO daoMock;
    
    @TestedObject
	private NotificationTemplateService service;
    
    @Test
    public void shouldGetById(){
        NotificationTemplate template = new NotificationTemplate();
        
        expect(daoMock.getById(NotificationTemplateType.APPLICATION_TASK_REQUEST)).andReturn(template);
        
        EasyMockUnitils.replay();
        
        NotificationTemplate returned = service.getById(NotificationTemplateType.APPLICATION_TASK_REQUEST);
        assertSame(returned, template);
    }

}
	
