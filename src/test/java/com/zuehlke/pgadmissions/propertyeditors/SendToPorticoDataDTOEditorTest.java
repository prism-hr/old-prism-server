package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

public class SendToPorticoDataDTOEditorTest {

    private SendToPorticoDataDTOEditor editor;

    private EncryptionHelper encryptionHelper;

    @Before
    public void initialize() {
        encryptionHelper = EasyMock.createMock(EncryptionHelper.class);

        editor = new SendToPorticoDataDTOEditor(encryptionHelper);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldDeserializeTwoElements() {
        EasyMock.expect(encryptionHelper.decryptToInteger("ref-1")).andReturn(1);
        EasyMock.expect(encryptionHelper.decryptToInteger("ref-2")).andReturn(2);
        EasyMock.replay(encryptionHelper);
        
        editor.setAsText("[\"ref-1\",\"ref-2\"]");
        
        List<Integer> list = (List<Integer>) editor.getValue();
        assertThat(list, hasItems(1, 2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldDeserializeEmptyList() {
        editor.setAsText("[]");
        List<Integer> list = (List<Integer>) editor.getValue();
        assertTrue(list.isEmpty());
    }

}
