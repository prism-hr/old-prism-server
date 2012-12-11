<#-- Assignments -->
<#assign errorCode = RequestParameters.errorCode! />
<#if personalDetails?has_content>
    <#assign hasPersonalDetails = true>
<#else>
    <#assign hasPersonalDetails = false>
</#if>

<#if personalDetails.candidateNationalities?has_content>
    <#assign nationalityExist = true/>
<#else>
    <#assign nationalityExist = false>
</#if>

<#if personalDetails.languageProficiencies?has_content>
    <#assign proficiencyExist = true/>
<#else>
    <#assign proficiencyExist = false>
</#if>

<#if personalDetails.candidateNationalities?has_content>
    <#assign candidateNationalitiesExist = true/>
<#else>
    <#assign candidateNationalitiesExist = false>
</#if>

<#if personalDetails.languageQualifications?has_content>
  <#assign hasLanguageQualifications = true>
<#else>
  <#assign hasLanguageQualifications = false>
</#if>

<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#-- Personal Details Rendering -->

<!-- Personal details -->
<a name="Personal-Details"></a>
<h2 id="personalDetails-H2" class="open">
    <span class="left"></span><span class="right"></span><span class="status"></span>
    Personal Details<em>*</em>
</h2>

<div>    
    <form>              
        <input type="hidden" id="form-display-state" value="${formDisplayState!}"/>
        <#if errorCode?? && errorCode=="true">
            <div class="section-error-bar">
                <span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>               
                <span class="invalid-info-text"><@spring.message 'personalDetails.sectionInfo'/></span>
            </div>
        <#else>
            <div id="pres-info-bar-div" class="section-info-bar"><@spring.message 'personalDetails.sectionInfo'/></div>  
        </#if>
        
        <div class="row-group">
            <div class="row">
                <label class="plain-label">Title<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.title'/>"></span>
                <div class="field">
                    <select class="full" name="title" id="title" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> >
                        <option value="">Select...</option>
                            <#list titles as title>
                                <option value="${title}" <#if personalDetails.title?? &&  personalDetails.title == title >selected="selected"</#if>>${title.displayValue?html}</option>               
                            </#list>
                    </select>                
                </div>
            </div>
            <@spring.bind "personalDetails.title" /> 
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label class="plain-label grey-label">First Name<em class="grey-label">*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.firstname'/>"></span>
                <div class="field">                     
                    <input class="full" readonly="readonly" type="text" value="${(user.firstName?html)!}" name="firstName" id="firstName" disabled="disabled"/>             
                </div>
            </div>
             
            <div class="row">
                <label class="plain-label grey-label">Last Name<em class="grey-label">*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.lastname'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(user.lastName?html)!}" name="lastName" id="lastName" disabled="disabled"/>           
                </div>
            </div>
             
            <div class="row">
                <label class="plain-label">Gender<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.gender'/>"></span>
                <div class="field">
                    <#list genders as gender>
                        <label><input type="radio" name="genderRadio" value="${gender}"
                            <#if personalDetails.gender?? &&  personalDetails.gender == gender >
                                checked="checked"
                            </#if> 
                            <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>                                       
                                /> ${gender.displayValue}</label>
                    </#list>                
                </div>
            </div>
            <@spring.bind "personalDetails.gender" /> 
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label class="plain-label">Date of Birth<em>*</em> </label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.dateOfBirth'/>"></span>
                <div class="field">
                    <#if (!applicationForm.isDecided() && !applicationForm.isWithdrawn())>
                        <input class="half date" value="${(personalDetails.dateOfBirth?string('dd MMM yyyy'))!}" name="dateOfBirth" id="dateOfBirth"/>                      
                    <#else>
                        <input class="full" readonly="readonly" type="text" disabled="disabled" value="${(personalDetails.dateOfBirth?string('dd MMM yyyy'))!}" name="dateOfBirth" id="dateOfBirth" />             
                    </#if>    
                </div>               
            </div>
            <@spring.bind "personalDetails.dateOfBirth" /> 
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>                
        </div>
        
        <div class="row-group">
            <div class="row"><label class="group-heading-label">Nationality</label></div>
            <div class="row">
                <label class="plain-label">Country of Birth<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.countryOfBirth'/>"></span>
                <div class="field">
                    <select class="full" name="country" id="country" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <#list countries as country>
                            <option value="${encrypter.encrypt(country.id)}"
                            <#if personalDetails.country?? &&  personalDetails.country.id == country.id >
                                selected="selected"
                            </#if>   
                            >${country.name?html}</option>               
                        </#list>
                    </select>                           
                </div>
            </div>
            <@spring.bind "personalDetails.country" /> 
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
                
            <div class="row">                       
                <label class="plain-label" id="my-nationality">Nationality<#if !nationalityExist><em id="nationality-em">*</em></#if></label>      
                <span id="my-nationality-hint" class="hint" data-desc="<@spring.message 'personalDetails.nationality'/>"></span>    
                <div class="field">
                    <div id="my-nationality-div">
                        <#list personalDetails.candidateNationalities as nationality >                              
                            <div class="nationality-item">
                                <label class="full">${nationality.name}</label>  
                                <input type="hidden" name="candidateNationalities" value='${encrypter.encrypt(nationality.id)}'/>
                                <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()><button type="button" class="button-delete" data-desc="Delete">Delete</button></#if>
                            </div>                          
                        </#list>
                    </div>
                        
                    <select class="full" name="candidateNationalityCountry" id="candidateNationalityCountry"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>>
                        <option value="">Select...</option>
                        <#list languages as country>
                            <option value="${encrypter.encrypt(country.id)}">${country.name?html}</option>               
                        </#list>
                    </select>                
                </div>
            </div>
            <@spring.bind "personalDetails.candidateNationalities" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
                 
            <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                <div class="row">
                    <div class="field"><button type="button" class="blue" id="addCandidateNationalityButton">Add</button></div>
                </div>
            </#if>
        </div>
        
        <div class="row-group">
            <div class="row"><label class="group-heading-label">Language</label></div>                     
            <div class="row">
                <label class="plain-label">Is English your first language?<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.language.section'/>"></span>
                <div class="field">
                    <label>
                        <input type="radio" name="englishFirstLanguage" id="englishFirstLanguageYes" value="true"
                        <#if personalDetails.isEnglishFirstLanguageSet() && personalDetails.getEnglishFirstLanguage()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> Yes
                    </label>                            
                    <label>
                        <input type="radio" name="englishFirstLanguage" id="englishFirstLanguageNo" value="false"
                        <#if personalDetails.isEnglishFirstLanguageSet() && !personalDetails.getEnglishFirstLanguage()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> No
                    </label>
                </div>
                <@spring.bind "personalDetails.englishFirstLanguage" />
                <#list spring.status.errorMessages as error>
                    <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
                </#list>
            </div>
            
            <div class="row">
                <label id="lbl-languageQualificationAvailable" class="plain-label grey-label">Do you have an English language qualification?<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.available'/>"></span>
                <div class="field">
                    <label>
                        <input type="radio" name="languageQualificationAvailable" id="languageQualificationAvailableYes" value="true"
                        <#if personalDetails.isLanguageQualificationAvailableSet() && personalDetails.getLanguageQualificationAvailable()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> Yes
                    </label>                            
                    <label>
                        <input type="radio" name="languageQualificationAvailable" id="languageQualificationAvailableNo" value="false"
                        <#if personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> No
                    </label>
                </div>
            </div>
            <@spring.bind "personalDetails.languageQualificationAvailable" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
        </div>
        
        <div id="languageQualification_div" class="row-group">
            
            <div class="row">
                <label id="lbl-englishLanguageQualifications" class="group-heading-label grey-label">English Language Qualifications</label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.title'/>"></span>
                
                <div class="field">
                <@spring.bind "personalDetails.languageQualifications" /> 
                <#list spring.status.errorMessages as error><span class="invalid">${error}</span></#list>
                    <table id="languageQualificationsTable" class="data-table">
                        <colgroup>
                            <col />
                            <col style="width: 60px;" />
                        </colgroup>
                        <tbody>
                        
                        <#assign x = -1>
                        <#list personalDetails.languageQualifications! as languageQualification>
                        <#assign x = x + 1>
                            <tr <#if languageQualification.id??> rel="${encrypter.encrypt(languageQualification.id)!}"</#if>>
                                <td>
                                    <#if languageQualification.languageQualificationDocument??> 
                                        <a class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(languageQualification.languageQualificationDocument.id))!}'/>" target="_blank">
                                        ${(languageQualification.qualificationType.displayValue?html)!} ${(languageQualification.otherQualificationTypeName?html)!}
                                        </a>
                                    <#else>
                                        ${(languageQualification.qualificationType.displayValue?html)!} ${(languageQualification.otherQualificationTypeName?html)!}
                                    </#if>
                                </td>
                                <td>
                                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>                    
                                        <a class="button-edit" data-desc="Edit" id="languageQualification_<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>" name="editLanguageQualificationLink">edit</a>
                                        <a class="button-delete" data-desc="Delete" id="languageQualificationDelete_<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>" name="deleteLanguageQualification">delete</a>
                                    </#if>
                                    <input type="hidden" name="lq_Id" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_languageQualificationId" value="<#if languageQualification.id?? >${encrypter.encrypt(languageQualification.id)!}</#if>" />
                                    <input type="hidden" name="lq_qualificationType" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_qualificationType" value="${(languageQualification.qualificationType?html)!}"/>
                                    <input type="hidden" name="lq_otherQualificationTypeName" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_otherQualificationTypeName" value="${(languageQualification.otherQualificationTypeName?html)!}"/>
                                    <input type="hidden" name="lq_dateOfExamination" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_dateOfExamination" value="${(languageQualification.dateOfExamination?string('dd MMM yyyy'))!}"/>
                                    <input type="hidden" name="lq_overallScore" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_overallScore" value="${(languageQualification.overallScore?html)!}"/>
                                    <input type="hidden" name="lq_readingScore" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_readingScore" value="${(languageQualification.readingScore?html)!}"/>
                                    <input type="hidden" name="lq_writingScore" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_writingScore" value="${(languageQualification.writingScore?html)!}"/>
                                    <input type="hidden" name="lq_speakingScore" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_speakingScore" value="${(languageQualification.speakingScore?html)!}"/>
                                    <input type="hidden" name="lq_listeningScore" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_listeningScore" value="${(languageQualification.listeningScore?html)!}"/>
                                    <input type="hidden" name="lq_examTakenOnline" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_examTakenOnline" value="<#if languageQualification.examTakenOnline??>true<#else>false</#if>"/>
                                    <input type="hidden" name="lq_languageQualificationDocument" id="<#if languageQualification.id??>${encrypter.encrypt(languageQualification.id)!}</#if>_languageQualificationDocument" value="<#if languageQualification.languageQualificationDocument?? && languageQualification.languageQualificationDocument.id??>${encrypter.encrypt(languageQualification.languageQualificationDocument.id)!}</#if>"/>
                                    <input type="hidden" name="lq_listId" id="lq_listId" value="${x}" />
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
            
            <div class="row">
            
                <input type="hidden" id="languageQualificationId" name="languageQualificationId" value="<#if languageQualificationId??>${languageQualificationId!}</#if>" />
            
                <label id="lbl-qualificationType" class="plain-label">Qualification Type<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.type'/>"></span>
                <div class="field">
                    <select class="full" name="qualificationType" id="qualificationType" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <#list languageQualificationTypes as qtype>
                            <option value="${qtype}"
                            <#if languageQualification.qualificationType?? && languageQualification.qualificationType.displayValue == qtype.displayValue>
                                selected="selected"
                            </#if>
                            >${qtype.displayValue?html}</option>
                        </#list>
                    </select>        
                </div>
            </div>
            <@spring.bind "languageQualification.qualificationType" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label id="lbl-otherQualificationTypeName" class="plain-label grey-label">Other Qualification Type Name<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.type.other'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" disabled="disabled" type="text" name="otherQualificationTypeName" id="otherQualificationTypeName" value="${(languageQualification.otherQualificationTypeName?html)!}" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> />
                </div>
            </div>
            <@spring.bind "languageQualification.otherQualificationTypeName" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label id="lbl-dateOfExamination" class="plain-label">Date of Examination<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.date'/>"></span>
                <div class="field">
                    <#if (!applicationForm.isDecided() && !applicationForm.isWithdrawn())>
                        <input class="half date" value="${(languageQualification.dateOfExamination?string('dd MMM yyyy'))!}" name="dateOfExamination" id="dateOfExamination"/>                      
                    <#else>
                        <input class="full" readonly="readonly" type="text" disabled="disabled" value="${(languageQualification.dateOfExamination?string('dd MMM yyyy'))!}" name="dateOfExamination" id="dateOfExamination" />
                    </#if>    
                </div>
            </div>
            <@spring.bind "languageQualification.dateOfExamination" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label id="lbl-overallScore" class="plain-label">Overall Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.overall'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.overallScore?html)!}" name="overallScore" id="overallScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> />
                    <select class="full" readonly="readonly" style="display:none" name="overallScore" id="overallScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <option value="4.0">4.0</option>
                        <option value="4.5">4.5</option>
                        <option value="5.0">5.0</option>
                        <option value="5.5">5.5</option>
                        <option value="6.0">6.0</option>
                        <option value="6.5">6.5</option>
                        <option value="7.0">7.0</option>
                        <option value="7.5">7.5</option>
                        <option value="8.0">8.0</option>
                        <option value="8.5">8.5</option>
                        <option value="9.0">9.0</option>
                    </select>
                </div>
            </div>
            <@spring.bind "languageQualification.overallScore" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label id="lbl-readingScore" class="plain-label">Reading Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.reading'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.readingScore?html)!}" name="readingScore" id="readingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> />
                    <select class="full" readonly="readonly" style="display:none" name="readingScore" id="readingScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <option value="4.0">4.0</option>
                        <option value="4.5">4.5</option>
                        <option value="5.0">5.0</option>
                        <option value="5.5">5.5</option>
                        <option value="6.0">6.0</option>
                        <option value="6.5">6.5</option>
                        <option value="7.0">7.0</option>
                        <option value="7.5">7.5</option>
                        <option value="8.0">8.0</option>
                        <option value="8.5">8.5</option>
                        <option value="9.0">9.0</option>
                    </select>
                </div>
            </div>
            <@spring.bind "languageQualification.readingScore" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label id="lbl-writingScore" class="plain-label">Essay / Writing Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.writing'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.writingScore?html)!}" name="writingScore" id="writingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> />
                    <select class="full" readonly="readonly" style="display:none" name="writingScore" id="writingScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <option value="4.0">4.0</option>
                        <option value="4.5">4.5</option>
                        <option value="5.0">5.0</option>
                        <option value="5.5">5.5</option>
                        <option value="6.0">6.0</option>
                        <option value="6.5">6.5</option>
                        <option value="7.0">7.0</option>
                        <option value="7.5">7.5</option>
                        <option value="8.0">8.0</option>
                        <option value="8.5">8.5</option>
                        <option value="9.0">9.0</option>
                    </select>
                </div>
            </div>                
            <@spring.bind "languageQualification.writingScore" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>            
            
            <div class="row">
                <label id="lbl-speakingScore" class="plain-label">Speaking Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.speaking'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.speakingScore?html)!}" name="speakingScore" id="speakingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> />
                    <select class="full" readonly="readonly" style="display:none" name="speakingScore" id="speakingScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <option value="4.0">4.0</option>
                        <option value="4.5">4.5</option>
                        <option value="5.0">5.0</option>
                        <option value="5.5">5.5</option>
                        <option value="6.0">6.0</option>
                        <option value="6.5">6.5</option>
                        <option value="7.0">7.0</option>
                        <option value="7.5">7.5</option>
                        <option value="8.0">8.0</option>
                        <option value="8.5">8.5</option>
                        <option value="9.0">9.0</option>
                    </select>                    
                </div>
            </div>
            <@spring.bind "languageQualification.speakingScore" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>            
            
            <div class="row">
                <label id="lbl-listeningScore" class="plain-label">Listening Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.listening'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.listeningScore?html)!}" name="listeningScore" id="listeningScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> />
                    <select class="full" readonly="readonly" style="display:none" name="listeningScore" id="listeningScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <option value="4.0">4.0</option>
                        <option value="4.5">4.5</option>
                        <option value="5.0">5.0</option>
                        <option value="5.5">5.5</option>
                        <option value="6.0">6.0</option>
                        <option value="6.5">6.5</option>
                        <option value="7.0">7.0</option>
                        <option value="7.5">7.5</option>
                        <option value="8.0">8.0</option>
                        <option value="8.5">8.5</option>
                        <option value="9.0">9.0</option>
                    </select>                    
                </div>
            </div>
            <@spring.bind "languageQualification.listeningScore" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>            
            
            <div class="row">
                <label id="lbl-examTakenOnline" class="plain-label">Did you sit the exam online?<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.exam.online'/>"></span>
                <div class="field">
                    <label>
                        <input type="radio" name="examTakenOnline" id="examTakenOnlineYes" value="true"
                        <#if languageQualification.isExamTakenOnlineSet() && languageQualification.getExamTakenOnline()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> Yes
                    </label>                            
                    <label>
                        <input type="radio" name="examTakenOnline" id="examTakenOnlineNo" value="false"
                        <#if languageQualification.isExamTakenOnlineSet() && !languageQualification.getExamTakenOnline()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> No
                    </label>
                </div>
            </div>    
            <@spring.bind "languageQualification.examTakenOnline" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>                          

            <!-- Attachment / supporting document -->
            <div class="row">
                <span id="lbl-languageQualificationDocument" class="plain-label">Certificate (PDF)</span>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.pdf'/>"></span>
                <div class="field <#if languageQualification.languageQualificationDocument??>uploaded</#if>" id="uploadFields">                      
                    <input id="languageQualificationDocument" data-type="LANGUAGE_QUALIFICATION" data-reference="Language Qualification" class="full" type="file" name="file" value="" disabled="disabled" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>                    
                    <span id="langqualUploadedDocument">
                        <input type="hidden" class="file" id="document_LANGUAGE_QUALIFICATION" value="${(encrypter.encrypt(languageQualification.languageQualificationDocument.id))!}"/>
                        <#if languageQualification.languageQualificationDocument??> 
                            <a class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(languageQualification.languageQualificationDocument.id))!}'/>" target="_blank">
                            ${(languageQualification.languageQualificationDocument.fileName?html)!}</a>
                            <a class="button-delete button-hint" data-desc="Change Language Qualification">delete</a> 
                        </#if>
                    </span>
                    <span class="progress" style="display: none;"></span>                   
                </div>                  
            </div>
            
            <div class="row">
                <div class="field">
                    <button id="updateLanguageQualificationButton" class="blue" type="button" style="display:none;">Update</button>
                    <button class="blue" id="addLanguageQualificationButton" type="button">Add</button>
                </div>
            </div>
            
        </div>

        <div class="row-group">
            <div class="row">
                <label class="group-heading-label">Residence</label>
            </div>
            <div class="row">
                <span class="plain-label">Country of Residence<em>*</em></span>
                    <span class="hint" data-desc="<@spring.message 'personalDetails.residence.country'/>"></span>        
                <div class="field">
                    <select class="full" name="residenceCountry" id="residenceCountry" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>>
                        <option value="">Select...</option>
                            <#list domiciles as domicile>
                              <option value="${encrypter.encrypt(domicile.id)}"
                                <#if personalDetails.residenceCountry?? &&  personalDetails.residenceCountry.id == domicile.id >
                                    selected="selected"
                                </#if>  
                              >${domicile.name?html}</option>               
                            </#list>
                     </select>
                    
                </div>
                <@spring.bind "personalDetails.residenceCountry" /> 
                <#list spring.status.errorMessages as error>
                    <div class="row">
                        <div class="field">
                            <span class="invalid">${error}</span>
                        </div>
                    </div>
                </#list>
                
                <div class="row">
                    <label class="plain-label">Do you require a visa to study in the UK?<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'personalDetails.residence.visa'/>"></span>
                    <div class="field">                             
                        <label>
                            <input type="radio" name="requiresVisa" id="requiresVisaYes" value="true"
                            <#if  personalDetails.isRequiresVisaSet() && personalDetails.getRequiresVisa() >
                                      checked="checked"
                            </#if>
                            <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                                      disabled="disabled"
                            </#if>/> Yes
                        </label>
                        <label>
                            <input type="radio" name="requiresVisa" id="requiresVisaNo" value="false"
                            <#if personalDetails.isRequiresVisaSet() && !personalDetails.getRequiresVisa()>
                                      checked="checked"
                            </#if>
                            <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                                      disabled="disabled"
                            </#if>/> No
                        </label>
                    </div>
                    <@spring.bind "personalDetails.requiresVisa" />
                    <#list spring.status.errorMessages as error >
                        <div class="row">
                            <div class="field">
                                <span class="invalid">${error}</span>
                            </div>
                        </div>
                    </#list>
                </div>

                <div class="row">
                    <label id="lbl-passportAvailable" class="plain-label grey-label">Do you have a passport?<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'personalDetails.passportAvailable'/>"></span>
                    <div class="field">                             
                        <label>
                            <input type="radio" name="passportAvailable" id="passportAvailableYes" value="true"
                            <#if  personalDetails.isPassportAvailableSet() && personalDetails.getPassportAvailable() >
                                      checked="checked"
                            </#if>
                            <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                                      disabled="disabled"
                            </#if>/> Yes
                        </label>
                        <label>
                            <input type="radio" name="passportAvailable" id="passportAvailableNo" value="false"
                            <#if personalDetails.isPassportAvailableSet() && !personalDetails.getPassportAvailable()>
                                      checked="checked"
                            </#if>
                            <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                                      disabled="disabled"
                            </#if>/> No
                        </label>
                    </div>
                    <@spring.bind "personalDetails.passportAvailable" />
                    <#list spring.status.errorMessages as error >
                        <div class="row">
                            <div class="field">
                                <span class="invalid">${error}</span>
                            </div>
                        </div>
                    </#list>
                </div>
                
                <div class="row">
                    <label id="lbl_passportNumber" class="plain-label">Passport Number</label>
                    <span class="hint" data-desc="<@spring.message 'personalDetails.passportNumber'/>"></span>
                    <div class="field">                     
                        <input class="full" readonly="readonly" type="text" value="${(personalDetails.passportInformation.passportNumber?html)!}" name="passportNumber" id="passportNumber" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isRequiresVisaSet() && !personalDetails.getRequiresVisa()) >disabled="disabled"</#if> />             
                    </div>
                </div>
                <@spring.bind "personalDetails.passportInformation.passportNumber" /> 
                <#list spring.status.errorMessages as error>
                    <div class="row">
                        <div class="field">
                            <span class="invalid">${error}</span>
                        </div>
                    </div>
                </#list>
                
                <div class="row">
                    <label id="lbl_nameOnPassport" class="plain-label">Name on Passport</label>
                    <span class="hint" data-desc="<@spring.message 'personalDetails.nameOnPassport'/>"></span>
                    <div class="field">
                        <input class="full" readonly="readonly" type="text" value="${(personalDetails.passportInformation.nameOnPassport?html)!}" name="nameOnPassport" id="nameOnPassport" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isRequiresVisaSet() && !personalDetails.getRequiresVisa())>disabled="disabled"</#if> />             
                    </div>
                </div>
                <@spring.bind "personalDetails.passportInformation.nameOnPassport" /> 
                <#list spring.status.errorMessages as error>
                    <div class="row">
                        <div class="field">
                            <span class="invalid">${error}</span>
                        </div>
                    </div>
                </#list>
                
                <div class="row">
                    <label id="lbl_passportIssueDate" class="plain-label">Passport Issue Date</label>
                    <span class="hint" data-desc="<@spring.message 'personalDetails.passportIssueDate'/>"></span>
                    <div class="field">
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isRequiresVisaSet() && !personalDetails.getRequiresVisa())>
                            <input class="full" readonly="readonly" type="text" disabled="disabled" value="${(personalDetails.passportInformation.passportIssueDate?string('dd MMM yyyy'))!}" name="passportIssueDate" id="passportIssueDate" />             
                        <#else>
                            <input class="half date" value="${(personalDetails.passportInformation.passportIssueDate?string('dd MMM yyyy'))!}" name="passportIssueDate" id="passportIssueDate"/>
                        </#if>    
                    </div>               
                </div>
                <@spring.bind "personalDetails.passportInformation.passportIssueDate" /> 
                <#list spring.status.errorMessages as error>
                    <div class="row">
                        <div class="field">
                            <span class="invalid">${error}</span>
                        </div>
                    </div>
                </#list>
            
                <div class="row">
                    <label id="lbl_passportExpiryDate" class="plain-label">Passport Expiry Date</label>
                    <span class="hint" data-desc="<@spring.message 'personalDetails.passportExpiryDate'/>"></span>
                    <div class="field">
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isRequiresVisaSet() && !personalDetails.getRequiresVisa())>
                            <input class="full" readonly="readonly" type="text" disabled="disabled" value="${(personalDetails.passportInformation.passportExpiryDate?string('dd MMM yyyy'))!}" name="passportExpiryDate" id="passportExpiryDate" />             
                        <#else>
                            <input class="half date" value="${(personalDetails.passportInformation.passportExpiryDate?string('dd MMM yyyy'))!}" name="passportExpiryDate" id="passportExpiryDate"/>
                        </#if>    
                    </div>               
                </div>
                <@spring.bind "personalDetails.passportInformation.passportExpiryDate" /> 
                <#list spring.status.errorMessages as error>
                    <div class="row">
                        <div class="field">
                            <span class="invalid">${error}</span>
                        </div>
                    </div>
                </#list>
                
            </div>
        </div>

        <div class="row-group">
            <div class="row">
                <label class="group-heading-label">Contact Details</label>
            </div>
            
            <div class="row">
                <span class="plain-label grey-label">Email<em class="grey-label">*</em></span>
                <span class="hint" data-desc="<@spring.message 'personalDetails.email'/>"></span> 
                <div class="field">
                        <input class="full" readonly="readonly" type="email" value="${(user.email?html)!}"  
                        name="email" id="email" disabled="disabled"/>             
                </div>
            </div>
   
                <div class="row">          
                <span class="plain-label">Telephone<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'personalDetails.telephone'/>"></span>
                <div class="field">                     
                    <#if !applicationForm.isDecided()  && !applicationForm.isWithdrawn()>
                        <input class="full" type="text" value="${(personalDetails.phoneNumber?html)!}" placeholder="e.g. +44 (0) 123 123 1234" name="pd_telephone" id="pd_telephone"/>
                    <#else>
                        <input class="full" readonly="readonly" disabled="disabled" type="text" value="${(personalDetails.phoneNumber?html)!}" name="pd_telephone" id="pd_telephone" />           
                    </#if>
                </div>
            </div>
            <@spring.bind "personalDetails.phoneNumber" />
            <#list spring.status.errorMessages as error>
                <div class="row">
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                </div>
            </#list>
            
          
            <div class="row">
                <label class="plain-label">Skype Name</label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.skype'/>"></span>
                <div class="field">                     
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                        <input class="full" type="text" value="${(personalDetails.messenger?html)!}" name="pd_messenger" id="pd_messenger"/>
                    <#else>
                        <input class="full" readonly="readonly" disabled="disabled" type="text" value="${(personalDetails.messenger?html)!}" name="pd_messenger" id="pd_messenger" />             
                    </#if>
                </div>
            </div>
            <@spring.bind "personalDetails.messenger" />         
            <#list spring.status.errorMessages as error>
                <div class="row">
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                </div>
            </#list>
        </div>
        
        <div class="row-group">
            <div class="row">
                <label class="group-heading-label">Equal Opportunities</label>
            </div>
            <div class="row">
                <label class="plain-label">Ethnicity<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.ethnicity'/>"></span>
                <div class="field">
                    <select class="full" name="ethnicity" id="ethnicity" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <#list ethnicities as ethnicity>
                        <option value="${encrypter.encrypt(ethnicity.id)}"
                        <#if personalDetails.ethnicity?? &&  personalDetails.ethnicity.id == ethnicity.id >
                        selected="selected"
                        </#if>   
                      >${ethnicity.name}</option>               
                    </#list>
                    </select>
                    
                </div>
            </div>
            <@spring.bind "personalDetails.ethnicity" /> 
            <#list spring.status.errorMessages as error>
                <div class="row">
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                </div>
            </#list>
            
            <div class="row">
                <label class="plain-label">Disability<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.disability'/>"></span>
                <div class="field">
                    <select class="full" name="disability" id="disability" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> >
                        <option value="">Select...</option>
                        <#list disabilities as disability>
                        <option value="${encrypter.encrypt(disability.id)}"
                        <#if personalDetails.disability?? &&  personalDetails.disability.id == disability.id >
                        selected="selected"
                        </#if>   
                      >${disability.name}</option>               
                    </#list>
                    </select>
                     
                </div>
            </div>
            <@spring.bind "personalDetails.disability" />
            <#list spring.status.errorMessages as error>
                <div class="row">
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                </div>
            </#list>
            
        </div>
  
            <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
            <@spring.bind "personalDetails.acceptedTerms" />
            <#if spring.status.errorMessages?size &gt; 0>        
                <div class="row-group terms-box invalid" >
    
            <#else>
                <div class="row-group terms-box" >
             </#if>
                <div class="row">
                    <span class="terms-label">
                        Confirm that the information that you have provided in this section is true 
                        and correct. Failure to provide true and correct information may result in a 
                        subsequent offer of study being withdrawn.              
                    </span>

                    <div class="terms-field">
                        <input type="checkbox" name="acceptTermsPEDCB" id="acceptTermsPEDCB"/>
                    </div>
                    <input type="hidden" name="acceptTermsPEDValue" id="acceptTermsPEDValue"/>
                </div>          
            </div>
        </#if>  
  
            <div class="buttons">
            <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
            <button type="button" class="" name="personalDetailsClearButton" id="personalDetailsClearButton" value="clear">Clear</button>
            <button type="button" class="blue" id="personalDetailsCloseButton">Close</button>
            <button type="button" class="blue" id="personalDetailsSaveButton" value="close">Save</button>
            <#else>
            <a id="personalDetailsCloseButton"class="button blue">Close</a>         
            </#if>
        </div>
     </form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>
