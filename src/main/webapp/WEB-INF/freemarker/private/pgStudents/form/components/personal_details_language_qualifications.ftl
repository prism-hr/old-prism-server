<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
            <h3 id="lbl-englishLanguageQualifications" class="group-heading-label grey-label">English Language Qualification</h3>
            <div class="row">
            
                <input type="hidden" id="languageQualificationId" name="languageQualificationId" value="<#if languageQualificationId??>${languageQualificationId!}</#if>" />
            
                <label id="lbl-qualificationType" class="plain-label grey-label">Qualification Type<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.type'/>"></span>
                <div class="field">
                    <select class="full" name="qualificationType" id="qualificationType" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> readonly="readonly" disabled="disabled">
                        <option value="">Select...</option>
                        <#list languageQualificationTypes as qtype>
                            <option value="${qtype}"
                            <#if personalDetails.languageQualifications[0].qualificationType?? && personalDetails.languageQualifications[0].qualificationType.displayValue == qtype.displayValue>
                                selected="selected"
                            </#if>
                            >${qtype.displayValue?html}</option>
                        </#list>
                    </select>   
                    <@spring.bind "personalDetails.languageQualifications[0].qualificationType" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>     
                </div>
            </div>
            
            
            <div class="row">
                <label for="otherQualificationTypeName" id="lbl-otherQualificationTypeName" class="plain-label grey-label">Other Qualification Type Name<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.type.other'/>"></span>
                <div class="field">
                    <input class="full" readonly disabled="disabled" type="text" name="otherQualificationTypeName" id="otherQualificationTypeName" value="${(personalDetails.languageQualifications[0].otherQualificationTypeName?html)!}" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled" />
                    <@spring.bind "personalDetails.languageQualifications[0].otherQualificationTypeName" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>
                </div>
            </div>

            
            <div class="row">
                <label id="lbl-dateOfExamination" class="plain-label grey-label" for="dateOfExamination">Date of Examination<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.date'/>"></span>
                <div class="field">
                    <#if (!applicationForm.isDecided() && !applicationForm.isWithdrawn())>
                        <input class="half date" type="text" readonly value="${(personalDetails.languageQualifications[0].dateOfExamination?string('dd MMM yyyy'))!}" name="dateOfExamination" id="dateOfExamination" readonly="readonly" disabled="disabled"/>                      
                    <#else>
                        <input class="full" readonly type="text" disabled="disabled" value="${(personalDetails.languageQualifications[0].dateOfExamination?string('dd MMM yyyy'))!}" name="dateOfExamination" id="dateOfExamination" />
                    </#if>  
                     <@spring.bind "personalDetails.languageQualifications[0].dateOfExamination" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>  


                </div>
            </div>

            
            <div class="row">
                <label id="lbl-overallScore" class="plain-label grey-label" for="overallScoreFree">Overall Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.overall'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualifications[0].overallScore?html)!}" name="overallScore" id="overallScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                    <@spring.bind "personalDetails.languageQualifications[0].overallScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>
                </div>
            </div>
            
            
            <div class="row">
                <label id="lbl-readingScore" class="plain-label grey-label" for="readingScoreFree">Reading Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.reading'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualifications[0].readingScore?html)!}" name="readingScore" id="readingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                    <@spring.bind "personalDetails.languageQualifications[0].readingScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>
                </div>
            </div>
            
            
            <div class="row">
                <label id="lbl-writingScore" class="plain-label grey-label" for="writingScoreFree">Essay / Writing Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.writing'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualifications[0].writingScore?html)!}" name="writingScore" id="writingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                    <@spring.bind "personalDetails.languageQualifications[0].writingScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>  
                </div>
            </div>                
                      
            
            <div class="row">
                <label id="lbl-speakingScore" class="plain-label grey-label" for="speakingScoreFree">Speaking Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.speaking'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualifications[0].speakingScore?html)!}" name="speakingScore" id="speakingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                    <@spring.bind "personalDetails.languageQualifications[0].speakingScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>                    
                </div>
            </div>
                     
            
            <div class="row">
                <label id="lbl-listeningScore" class="plain-label grey-label" for="listeningScoreFree">Listening Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.listening'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualifications[0].listeningScore?html)!}" name="listeningScore" id="listeningScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                    <@spring.bind "personalDetails.languageQualifications[0].listeningScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>                            
                </div>
            </div>

            
            <div class="row">
                <label id="lbl-examTakenOnline" class="plain-label grey-label">Did you sit the exam online?<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.exam.online'/>"></span>
                <div class="field">
                    <label>
                        <input type="radio" name="examTakenOnline" id="examTakenOnlineYes" value="true" readonly disabled="disabled"
                        <#if personalDetails.languageQualifications[0].isExamTakenOnlineSet() && personalDetails.languageQualifications[0].getExamTakenOnline()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> Yes
                    </label>                            
                    <label>
                        <input type="radio" name="examTakenOnline" id="examTakenOnlineNo" value="false" readonly disabled="disabled"
                        <#if personalDetails.languageQualifications[0].isExamTakenOnlineSet() && !personalDetails.languageQualifications[0].getExamTakenOnline()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> No
                    </label>
                    <@spring.bind "personalDetails.languageQualifications[0].examTakenOnline" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                                    <i class="icon-warning-sign"></i>${error} 
                                </div>
                    </#list>   
                </div>
            </div>    
                                   

            <!-- Attachment / supporting document -->
            <div class="row">
                <label for="languageQualificationDocument" id="lbl-languageQualificationDocument" class="plain-label grey-label">Certificate (PDF)<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.pdf'/>"></span>
                <div class="field <#if personalDetails.languageQualifications[0].languageQualificationDocument??>uploaded</#if>" id="uploadFields">                      
                    <input id="languageQualificationDocument"  data-type="LANGUAGE_QUALIFICATION" data-reference="Language Qualification" class="full" type="file" name="file" value="" disabled="disabled" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> readonly="readonly" disabled="disabled"/> 

                    <span id="langqualUploadedDocument">
                        <input type="hidden" class="file" id="document_LANGUAGE_QUALIFICATION" value="${(encrypter.encrypt(personalDetails.languageQualifications[0].languageQualificationDocument.id))!}"/>
                        <#if personalDetails.languageQualifications[0].languageQualificationDocument??> 
                            <a id="lqLink" class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(personalDetails.languageQualifications[0].languageQualificationDocument.id))!}'/>" target="_blank">
                            ${(personalDetails.languageQualifications[0].languageQualificationDocument.fileName?html)!}</a>
                            <a id="deleteLq" class="button-delete button-hint" data-desc="Change Language Qualification">delete</a> 
                        </#if>
                    </span>
                    <span class="progress" style="display: none;"></span> 
                    <@spring.bind "personalDetails.languageQualifications[0].languageQualificationDocument" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                                    <i class="icon-warning-sign"></i>${error} 
                                </div>
                    </#list>                  
                </div>                  
            </div>
            
            
