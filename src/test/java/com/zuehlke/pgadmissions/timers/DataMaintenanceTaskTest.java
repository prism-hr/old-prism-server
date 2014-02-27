package com.zuehlke.pgadmissions.timers;

import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.services.DocumentService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class DataMaintenanceTaskTest {
    
    @Mock
    @InjectIntoByType
    private DocumentService documentService;

    @TestedObject
    private DataMaintenanceTask dataMaintenanceTask;

    @Test
    public void shouldMaintainData() {
        documentService.deleteOrphanDocuments();

        replay();
        dataMaintenanceTask.maintainData();
        verify();
    }

}
