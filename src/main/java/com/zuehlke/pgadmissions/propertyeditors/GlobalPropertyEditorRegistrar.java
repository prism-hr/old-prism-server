package com.zuehlke.pgadmissions.propertyeditors;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;

public class GlobalPropertyEditorRegistrar implements PropertyEditorRegistrar {

    private EntityPropertyEditor<Language> languagePropertyEditor;

    private EntityPropertyEditor<Country> countryPropertyEditor;

    private EntityPropertyEditor<Disability> disabilityPropertyEditor;

    private EntityPropertyEditor<Ethnicity> ethnicityPropertyEditor;

    private EntityPropertyEditor<Domicile> domicilePropertyEditor;

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(Language.class, languagePropertyEditor);
        registry.registerCustomEditor(Country.class, countryPropertyEditor);
        registry.registerCustomEditor(Disability.class, disabilityPropertyEditor);
        registry.registerCustomEditor(Ethnicity.class, ethnicityPropertyEditor);
        registry.registerCustomEditor(Domicile.class, domicilePropertyEditor);
    }

    public void setCountryPropertyEditor(EntityPropertyEditor<Country> countryPropertyEditor) {
        this.countryPropertyEditor = countryPropertyEditor;
    }

    public void setLanguagePropertyEditor(EntityPropertyEditor<Language> languagePropertyEditor) {
        this.languagePropertyEditor = languagePropertyEditor;
    }

    public void setDisabilityPropertyEditor(EntityPropertyEditor<Disability> disabilityPropertyEditor) {
        this.disabilityPropertyEditor = disabilityPropertyEditor;
    }

    public void setEthnicityPropertyEditor(EntityPropertyEditor<Ethnicity> ethnicityPropertyEditor) {
        this.ethnicityPropertyEditor = ethnicityPropertyEditor;
    }

    public void setDomicilePropertyEditor(EntityPropertyEditor<Domicile> domicilePropertyEditor) {
        this.domicilePropertyEditor = domicilePropertyEditor;
    }

    
    
}
