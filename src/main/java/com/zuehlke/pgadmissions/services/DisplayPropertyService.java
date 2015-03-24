package com.zuehlke.pgadmissions.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.display.DisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class DisplayPropertyService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private SystemService systemService;

    public DisplayPropertyDefinition getDefinitionById(PrismDisplayPropertyDefinition id) {
        return entityService.getById(DisplayPropertyDefinition.class, id);
    }

    public HashMap<PrismDisplayPropertyDefinition, String> getDisplayProperties(Resource resource, PrismScope scope, PrismDisplayPropertyCategory category,
                                                                                PrismLocale locale, PrismProgramType programType) {
        List<DisplayPropertyConfiguration> displayValues = customizationService.getDisplayPropertyConfiguration(resource, scope, category, locale, programType);
        HashMap<PrismDisplayPropertyDefinition, String> displayProperties = Maps.newHashMap();
        for (DisplayPropertyConfiguration displayValue : displayValues) {
            PrismDisplayPropertyDefinition displayPropertyId = displayValue.getDisplayPropertyDefinition().getId();
            if (!displayProperties.containsKey(displayPropertyId)) {
                displayProperties.put(displayPropertyId, displayValue.getValue());
            }
        }
        return displayProperties;
    }

    public void googleTranslateDisplayProperties(PrismLocale locale) throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        Translate t = new Translate.Builder(httpTransport, jsonFactory, null)
                .setGoogleClientRequestInitializer(new CommonGoogleClientRequestInitializer("AIzaSyBs0D7C2wzrK3vL1tl5nZ8Vm3PvnvdrkYM")).build();


        com.zuehlke.pgadmissions.domain.system.System system = systemService.getSystem();
        PrismLocale systemLocale = PrismLocale.getSystemLocale();
        for (PrismDisplayPropertyCategory category : PrismDisplayPropertyCategory.values()) {
            List<DisplayPropertyConfiguration> referenceDisplayValues = customizationService.getDisplayPropertyConfiguration(system, category.getScope(), category, systemLocale, null);
            List<List<DisplayPropertyConfiguration>> partitions = Lists.partition(referenceDisplayValues, 30);
            for (List<DisplayPropertyConfiguration> partition : partitions) {
                translateProperties(locale, t, system, systemLocale, partition);
            }
        }


    }

    private void translateProperties(PrismLocale locale, Translate translationEndpoint, com.zuehlke.pgadmissions.domain.system.System system, PrismLocale systemLocale, List<DisplayPropertyConfiguration> referenceDisplayValues) throws IOException {
        List<String> phrasesToTranslate = Lists.newArrayListWithCapacity(referenceDisplayValues.size());
        for (DisplayPropertyConfiguration referenceDisplayValue : referenceDisplayValues) {
            phrasesToTranslate.add(referenceDisplayValue.getValue());
        }
        if (phrasesToTranslate.isEmpty()) { // nothing to translate
            return;
        }

        Translate.Translations.List list = translationEndpoint.translations()
                .list(phrasesToTranslate, locale.getLanguagePart())
                .setSource(systemLocale.getLanguagePart());
        TranslationsListResponse response = list.execute();

        if (response.getTranslations().size() != phrasesToTranslate.size()) {
            System.out.println(response.toString());
        }

        for (int i = 0; i < phrasesToTranslate.size(); i++) {
            String translation = response.getTranslations().get(i).getTranslatedText();
            DisplayPropertyDefinition displayPropertyDefinition = referenceDisplayValues.get(i).getDisplayPropertyDefinition();
            PrismScope translationScope = displayPropertyDefinition.getScope().getId();
            DisplayPropertyConfiguration displayPropertyConfiguration = new DisplayPropertyConfiguration()
                    .withDisplayPropertyDefinition(displayPropertyDefinition)
                    .withResource(system).withLocale(locale).withSystemDefault(false).withValue(translation);
            if (translationScope.ordinal() >= PrismScope.PROGRAM.ordinal()) {
                displayPropertyConfiguration.setProgramType(PrismProgramType.getSystemProgramType());
            }
            entityService.createOrUpdate(displayPropertyConfiguration);
        }

    }
}
