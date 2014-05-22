package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.Document;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class DocumentServiceTest {

    @Mock
    @InjectIntoByType
    private DocumentDAO documentDAOMock;

    @TestedObject
    private DocumentService documentService;

    @Test
    public void shouldDelegateSaveToDAO() {
        Document document = new Document();
        documentDAOMock.save(document);

        replay();
        documentService.save(document);
    }
    @Test
    public void shouldDeleteOrphanDocuments() {
        documentDAOMock.deleteOrphanDocuments();
        
        replay();
        documentService.deleteOrphanDocuments();
    }

    @Test
    public void shouldGetDocumentFroMDAO() {
        Document document = new Document().withId(1);

        EasyMock.expect(documentDAOMock.getDocumentbyId(1)).andReturn(document);

        replay();
        Document loadedDocument = documentService.getByid(1);
        assertEquals(document, loadedDocument);
    }
    
   

}
