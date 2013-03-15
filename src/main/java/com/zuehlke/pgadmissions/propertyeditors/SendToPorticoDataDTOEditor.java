package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Component
public class SendToPorticoDataDTOEditor extends PropertyEditorSupport {

    private final EncryptionHelper encryptionHelper;

    public SendToPorticoDataDTOEditor() {
        this(null);
    }

    @Autowired
    public SendToPorticoDataDTOEditor(EncryptionHelper encryptionHelper) {
        this.encryptionHelper = encryptionHelper;

    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAsText(String sendToPorticoData) throws IllegalArgumentException {
        if (sendToPorticoData == null || StringUtils.isBlank(sendToPorticoData)) {
            setValue(null);
            return;
        }

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(List.class, new JsonDeserializer<List<Integer>>() {

            @Override
            public List<Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                List<Integer> decryptedElements = new ArrayList<Integer>();
                for (JsonElement encryptedString : json.getAsJsonArray()) {
                    Integer decryptedId = encryptionHelper.decryptToInteger(encryptedString.getAsString());
                    decryptedElements.add(decryptedId);
                }
                return decryptedElements;
            }
        });
        List<Integer> decrypted = gson.create().fromJson(sendToPorticoData, List.class);
        setValue(decrypted);
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }
        return null;
    }
}
