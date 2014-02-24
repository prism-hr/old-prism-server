package com.zuehlke.pgadmissions.components;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ImportedDataDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.domain.ProgramInstance;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ImportDataReferenceUpdaterTest {

    @Mock
    @InjectIntoByType
    private ImportedDataDAO importedDataDAO;

    @TestedObject
    private ImportDataReferenceUpdater importDataReferenceUpdater;

    @Test
    public void shouldNotUpdateReferencesIfNotSupportedClassPassed() {
        replay();
        importDataReferenceUpdater.updateReferences(ProgramInstance.class);
        verify();
    }

    @Test
    public void shouldUpdateReferences() {
        Country disabledCountry = new Country();
        Country enabledCountry = new Country();
        
        expect(importedDataDAO.getDisabledImportedObjectsWithoutActiveReference(Country.class)).andReturn(Lists.<ImportedObject> newArrayList(disabledCountry));

        expect(importedDataDAO.getEnabledVersion(disabledCountry)).andReturn(enabledCountry);

        replay();
        importDataReferenceUpdater.updateReferences(Country.class);
        verify();

        assertSame(enabledCountry, disabledCountry.getEnabledObject());
    }

}
