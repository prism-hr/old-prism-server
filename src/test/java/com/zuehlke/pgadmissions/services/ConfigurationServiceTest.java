package com.zuehlke.pgadmissions.services;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ConfigurationServiceTest {
    // TODO write tests

    @Mock
    @InjectIntoByType
    private StateDAO stateDAOMock;

    @TestedObject
    private ConfigurationService service;
    
    public void shouldSaveServiceLevelsDTO(){
        
        ServiceLevelsDTO serviceLevelsDTO = new ServiceLevelsDTO();
        service.saveServiceLevels(serviceLevelsDTO);
    }

}
