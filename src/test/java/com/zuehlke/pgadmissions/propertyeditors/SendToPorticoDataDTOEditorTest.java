package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

public class SendToPorticoDataDTOEditorTest {

    private SendToPorticoDataDTOEditor editor;

    private EncryptionHelper encryptionHelper;

    @Before
    public void initialize() {
        encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
        EasyMock.expect(encryptionHelper.decryptToInteger("ref-1")).andReturn(1);
        EasyMock.expect(encryptionHelper.decryptToInteger("ref-2")).andReturn(2);
        
        EasyMock.expect(encryptionHelper.decryptToInteger("qual-1")).andReturn(11);
        EasyMock.expect(encryptionHelper.decryptToInteger("qual-2")).andReturn(12);

        EasyMock.replay(encryptionHelper);
        editor = new SendToPorticoDataDTOEditor(encryptionHelper);
    }

    @Test
    public void shouldDeserializeReferencesAndQualifications() {
        editor.setAsText("{\"references\": [\"ref-1\",\"ref-2\"],\"qualifications\": [\"qual-1\",\"qual-2\"]}");
        SendToPorticoDataDTO dto = (SendToPorticoDataDTO) editor.getValue();
        assertThat(dto.getReferencesSendToPortico(), hasItems(1, 2));
        assertThat(dto.getQualificationsSendToPortico(), hasItems(11, 12));
    }
    
    @Test
    public void shouldDeserializeOnlyReferences() {
        editor.setAsText("{\"references\": [\"ref-1\",\"ref-2\"]}");
        SendToPorticoDataDTO dto = (SendToPorticoDataDTO) editor.getValue();
        assertThat(dto.getReferencesSendToPortico(), hasItems(1, 2));
        assertNull(dto.getQualificationsSendToPortico());
    }
    
    @Test
    public void shouldDeserializeOnlyQualifications() {
        editor.setAsText("{\"qualifications\": [\"qual-1\",\"qual-2\"]}");
        SendToPorticoDataDTO dto = (SendToPorticoDataDTO) editor.getValue();
        assertThat(dto.getQualificationsSendToPortico(), hasItems(11, 12));
        assertNull(dto.getReferencesSendToPortico());
    }

}
