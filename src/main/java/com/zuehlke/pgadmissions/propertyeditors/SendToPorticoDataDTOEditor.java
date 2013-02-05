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
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;


@Component
public class SendToPorticoDataDTOEditor extends PropertyEditorSupport {

    private final EncryptionHelper encryptionHelper;

    SendToPorticoDataDTOEditor() {
        this(null);
    }

    @Autowired
    public SendToPorticoDataDTOEditor(EncryptionHelper encryptionHelper) {
        this.encryptionHelper = encryptionHelper;

    }

    @Override
    public void setAsText(String sendToPorticoData) throws IllegalArgumentException {
        if (sendToPorticoData == null || StringUtils.isBlank(sendToPorticoData)) {
            setValue(null);
            return;
        }

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(SendToPorticoDataDTO.class, new JsonDeserializer<SendToPorticoDataDTO>() {

            @Override
            public SendToPorticoDataDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();

                List<Integer> references = new ArrayList<Integer>();
                JsonElement referencesArray = json.getAsJsonObject().get("referees");
                if (referencesArray != null) {
                    for (JsonElement referenceString : referencesArray.getAsJsonArray()) {
                        Integer referenceId = encryptionHelper.decryptToInteger(referenceString.getAsString());
                        references.add(referenceId);
                    }
                    sendToPorticoDataDTO.setReferencesSendToPortico(references);
                }

                List<Integer> qualifications = new ArrayList<Integer>();
                JsonElement qualificationsArray = json.getAsJsonObject().get("qualifications");
                if (qualificationsArray != null) {
                    for (JsonElement qualificationString : qualificationsArray.getAsJsonArray()) {
                        Integer qualificationId = encryptionHelper.decryptToInteger(qualificationString.getAsString());
                        qualifications.add(qualificationId);
                    }
                    sendToPorticoDataDTO.setQualificationsSendToPortico(qualifications);
                }

                return sendToPorticoDataDTO;
            }
        });
        SendToPorticoDataDTO dto = gson.create().fromJson(sendToPorticoData, SendToPorticoDataDTO.class);
        setValue(dto);
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }
        // return encryptionHelper.encrypt(((Language)getValue()).getId());
        return null;
    }
}
