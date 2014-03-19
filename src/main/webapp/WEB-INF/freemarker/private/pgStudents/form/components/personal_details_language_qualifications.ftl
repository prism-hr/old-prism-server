<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
            <h3 id="lbl-englishLanguageQualifications" class="group-heading-label grey-label">English Language Qualification</h3>
            <div class="row">
            
                <input type="hidden" id="languageQualificationId" name="languageQualificationId" value="<#if languageQualificationId??>${languageQualificationId!}</#if>" />
            
                <label id="lbl-qualificationType" class="plain-label grey-label">Qualification Type<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.type'/>"></span>
                <div class="field">
                    <select class="full" name="qualificationType" id="qualificationType" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> readonly="readonly" disabled="disabled">
                        <option value="">Select...</option>
                        <#list languageQualificationTypes as qtype>
                            <option value="${qtype}"
                            <#if personalDetails.languageQualification.qualificationType?? && personalDetails.languageQualification.qualificationType.displayValue == qtype.displayValue>
                                selected="selected"
                            </#if>
                            >${qtype.displayValue?html}</option>
                        </#list>
                    </select>   
                    <@spring.bind "personalDetails.languageQualification.qualificationType" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>     
                </div>
            </div>
            
            
            <div class="row">
                <label for="qualificationTypeName" id="lbl-qualificationTypeName" class="plain-label grey-label">Other Qualification Type Name<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.type.other'/>"></span>
                <div class="field">
                    <input class="full" readonly disabled="disabled" type="text" name="qualificationTypeName" id="qualificationTypeName" value="${(personalDetails.languageQualification.qualificationTypeName?html)!}" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> readonly="readonly" disabled="disabled" />
                    <@spring.bind "personalDetails.languageQualification.qualificationTypeName" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>
                </div>
            </div>

            
            <div class="row">
                <label id="lbl-examDate" class="plain-label grey-label" for="examDate">Date of Examination<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.date'/>"></span>
                <div class="field">
                    <#if (!applicationForm.isDecided() && !applicationForm.isWithdrawn())>
                        <input class="half date" type="text" readonly value="${(personalDetails.languageQualification.examDate?string('dd MMM yyyy'))!}" name="examDate" id="examDate" readonly="readonly" disabled="disabled"/>                      
                    <#else>
                        <input class="full" readonly type="text" disabled="disabled" value="${(personalDetails.languageQualification.examDate?string('dd MMM yyyy'))!}" name="examDate" id="examDate" />
                    </#if>  
                     <@spring.bind "personalDetails.languageQualification.examDate" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>  


                </div>
            </div>

            
            <div class="row">
                <label id="lbl-overallScore" class="plain-label grey-label" for="overallScoreFree">Overall Score<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.score.overall'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualification.overallScore?html)!}" name="overallScore" id="overallScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
                    <select class="full" readonly="readonly" style="display:none" name="overallScore" id="overallScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> >
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
                    <@spring.bind "personalDetails.languageQualification.overallScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>
                </div>
            </div>
            
            
            <div class="row">
                <label id="lbl-readingScore" class="plain-label grey-label" for="readingScoreFree">Reading Score<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.score.reading'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualification.readingScore?html)!}" name="readingScore" id="readingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
                    <select class="full" readonly="readonly" style="display:none" name="readingScore" id="readingScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> >
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
                    <@spring.bind "personalDetails.languageQualification.readingScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>
                </div>
            </div>
            
            
            <div class="row">
                <label id="lbl-writingScore" class="plain-label grey-label" for="writingScoreFree">Essay / Writing Score<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.score.writing'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualification.writingScore?html)!}" name="writingScore" id="writingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
                    <select class="full" readonly="readonly" style="display:none" name="writingScore" id="writingScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> >
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
                    <@spring.bind "personalDetails.languageQualification.writingScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>  
                </div>
            </div>                
                      
            
            <div class="row">
                <label id="lbl-speakingScore" class="plain-label grey-label" for="speakingScoreFree">Speaking Score<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.score.speaking'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualification.speakingScore?html)!}" name="speakingScore" id="speakingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
                    <select class="full" readonly="readonly" style="display:none" name="speakingScore" id="speakingScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> >
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
                    <@spring.bind "personalDetails.languageQualification.speakingScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>                    
                </div>
            </div>
                     
            
            <div class="row">
                <label id="lbl-listeningScore" class="plain-label grey-label" for="listeningScoreFree">Listening Score<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.score.listening'/>"></span>
                <div class="field">
                    <input class="full" readonly type="text" value="${(personalDetails.languageQualification.listeningScore?html)!}" name="listeningScore" id="listeningScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
                    <select class="full" readonly="readonly" style="display:none" name="listeningScore" id="listeningScoreSelect" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.languageQualificationAvailable?? && !personalDetails.languageQualificationAvailable) >disabled="disabled"</#if> >
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
                    <@spring.bind "personalDetails.languageQualification.listeningScore" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                            <i class="icon-warning-sign"></i>${error} 
                        </div>
                    </#list>                            
                </div>
            </div>

            
            <div class="row">
                <label id="lbl-examOnline" class="plain-label grey-label">Did you sit the exam online?<em>*</em></label>
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.exam.online'/>"></span>
                <div class="field">
                    <#assign languageQualifications = personalDetails.languageQualification>
                    <label class="grey-label">
                        <input type="radio" name="examOnline" id="examOnlineYes" value="true" readonly disabled="disabled"
                        <#if languageQualifications.examOnline?? && languageQualifications.examOnline>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> Yes
                    </label>                            
                    <label class="grey-label">
                        <input type="radio" name="examOnline" id="examOnlineNo" value="false" readonly disabled="disabled"
                        <#if languageQualifications.examOnline?? && !languageQualifications.examOnline>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> No
                    </label>
                    <@spring.bind "personalDetails.languageQualification.examOnline" />
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
                <span class="hint grey" data-desc="<@spring.message 'personalDetails.languageQualification.pdf'/>"></span>
                <div class="field <#if personalDetails.languageQualification.languageQualificationDocument??>uploaded</#if>" id="uploadFields">
                      <div class="fileupload fileupload-new" data-provides="fileupload">
                        <div class="input-append">
                          <div class="uneditable-input span4 disabled" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
                          <span class="btn btn-file disabled"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
                         <input id="languageQualificationDocument"  data-type="LANGUAGE_QUALIFICATION" data-reference="Language Qualification" class="full" type="file" name="file" value="" disabled="disabled" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> readonly="readonly" disabled="disabled"/> 
                          </span> </div>
                      </div>

                    <ul id="langqualUploadedDocument"  class="uploaded-files">
                        <#if personalDetails.languageQualification.languageQualificationDocument??> 
                            
                             <li class="done">
                             <span class="uploaded-file" name="supportingDocumentSpan">
                             <input type="hidden" class="file" id="document_LANGUAGE_QUALIFICATION" value="${(encrypter.encrypt(personalDetails.languageQualification.languageQualificationDocument.id))!}"/> 
                             <a id="lqLink" class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(personalDetails.languageQualification.languageQualificationDocument.id))!}'/>" target="_blank">                  ${(personalDetails.languageQualification.languageQualificationDocument.fileName?html)!}</a>
                             
                            <a id="deleteLq" class="btn btn-danger delete" data-desc="delete Language Qualification"><i class="icon-trash icon-large"></i> Delete</a> 
                            </span>
                        </#if>
                    </ul>
                    
                    <span class="progress" style="display: none;"></span> 
                    <@spring.bind "personalDetails.languageQualification.languageQualificationDocument" />
                    <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                                    <i class="icon-warning-sign"></i>${error} 
                                </div>
                    </#list>                  
                </div>                  
            </div>
            
            
