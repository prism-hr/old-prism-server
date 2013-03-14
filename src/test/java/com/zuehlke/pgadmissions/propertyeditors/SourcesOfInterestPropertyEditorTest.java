package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.SourcesOfInterestService;

public class SourcesOfInterestPropertyEditorTest {

    private SourcesOfInterestPropertyEditor editor;
    private SourcesOfInterestService sourcesOfInterestServiceMock;
    private EncryptionHelper encryptionHelperMock;
    
    @Test   
    public void shouldLoadByIdAndSetAsValue() {
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).code("OTHER").name("Other").enabled(true).build();
        EasyMock.expect(encryptionHelperMock.decryptToInteger("1")).andReturn(1);
        EasyMock.expect(sourcesOfInterestServiceMock.getSourcesOfInterestById(1)).andReturn(interest);
        EasyMock.replay(sourcesOfInterestServiceMock, encryptionHelperMock);
        editor.setAsText("1");
        assertSame(interest, editor.getValue());
    }
    
    @Test   
    public void shouldReturnNullIfValueIsNull(){            
        editor.setValue(null);
        assertNull(editor.getAsText());
    }
    
    @Test   
    public void shouldReturnNullIfValueIdIsNull(){
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(null).code("OTHER").name("Other").enabled(true).build();
        editor.setValue(interest);
        assertNull(editor.getAsText());
    }
    
    @Test   
    public void shouldReturnAuthorityAsEnum() {
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).code("OTHER").name("Other").enabled(true).build();
        editor.setValue(interest);
        assertEquals("1", editor.getAsText());
    }
    
    @Before
    public void setup(){
        sourcesOfInterestServiceMock = EasyMock.createMock(SourcesOfInterestService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        editor = new SourcesOfInterestPropertyEditor(sourcesOfInterestServiceMock, encryptionHelperMock);
    }
    
}
