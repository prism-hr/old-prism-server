package com.zuehlke.pgadmissions.workflow;

import org.joda.time.LocalDate;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.propertyeditors.GlobalPropertyEditorRegistrar;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;

@Service
@Transactional
public class ApplicationTestDataProvider {

    @Autowired
    private PersonalDetailsService personalDetailsService;

    @Autowired
    private GlobalPropertyEditorRegistrar globalPropertyEditorRegistrar;

    public void fillWithData(Application application) throws BindException {
        PersonalDetails personalDetails = new PersonalDetails();
        DataBinder dataBinder = new DataBinder(personalDetails);
        globalPropertyEditorRegistrar.registerCustomEditors(dataBinder);
        dataBinder.bind(new MutablePropertyValues().add("title", "MR").add("gender", "INDETERMINATE_GENDER").add("dateOfBirth", new LocalDate().minusYears(28))
                .add("countryOfbirth", "1").add("firstNationality", "1").add("secondNationality", "1").add("englishFirstLanguage", "false")
                .add("messenger", "dupa").add("country", "1"));
        dataBinder.close();

        personalDetailsService.saveOrUpdate(application, personalDetails, application.getUser());
    }

}
