package com.zuehlke.pgadmissions.workflow;

import javax.annotation.Resource;

import org.joda.time.LocalDate;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.DataBinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.propertyeditors.GlobalPropertyEditorRegistrar;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

@Service
@Transactional
public class ApplicationTestDataProvider {

    @Autowired
    private PersonalDetailsService personalDetailsService;
    
    @Autowired
    private PersonalDetailsValidator personalDetailsValidator;

    @Autowired
    private GlobalPropertyEditorRegistrar globalPropertyEditorRegistrar;
    
    @Autowired
    private DocumentService documentService;
    
    @Resource(name="jacksonObjectMapper")
    private ObjectMapper objectMapper;

    public void fillWithData(Application application) throws Exception {

        Document document = new Document().withContent(new byte[0]);
        documentService.save(document);

        PersonalDetails personalDetails = objectMapper.readValue("{\"requiresVisa\" : \"true\", \"residenceCountry\" : 295}", PersonalDetails.class);

        DataBinder dataBinder = new DataBinder(personalDetails);
        globalPropertyEditorRegistrar.registerCustomEditors(dataBinder);
        dataBinder.setValidator(personalDetailsValidator);
        dataBinder.bind(new MutablePropertyValues().add("title", "MR")
                .add("gender", "INDETERMINATE_GENDER").add("dateOfBirth", new LocalDate().minusYears(28))
                .add("country", "14").add("firstNationality", "537")
                .add("secondNationality", "553").add("englishFirstLanguage", "yes")
                .add("languageQualificationAvailable", true)
                .add("languageQualification.qualificationType", "OTHER")
                .add("languageQualification.qualificationTypeOther", "I tak sie chuja nauczylem")
                .add("languageQualification.examDate", "14-SEP-1967")
                .add("languageQualification.overallScore", "6")
                .add("languageQualification.readingScore", "6")
                .add("languageQualification.writingScore", "6")
                .add("languageQualification.speakingScore", "6")
                .add("languageQualification.listeningScore", "6")
                .add("languageQualification.examOnline", "no")
                .add("languageQualification.proofOfAward", document)
                .add("residenceCountry", "295")
                .add("requiresVisa", "yes")
                .add("passportAvailable", "yes")
                .add("passport.number", "666")
                .add("passport.name", "Kubus Fibinger")
                .add("passport.issueDate", "14-SEP-2003")
                .add("passport.expiryDate", "14-AUG-2084")
                .add("phoneNumber", "+44(4)5435435") .add("messenger", "dupajasia")
                .add("ethnicity", "517").add("disability", "502"));
        dataBinder.validate();
        dataBinder.close();

        personalDetailsService.saveOrUpdate(application, personalDetails, application.getUser());
    }

}
