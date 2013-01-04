<#import "/spring.ftl" as spring />
<#setting locale = "en_US">

        <div class="row">
                <label id="lbl-englishLanguageQualifications" class="group-heading-label grey-label">English Language Qualification</label>
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
            
                <label id="lbl-qualificationType" class="plain-label grey-label">Qualification Type<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.type'/>"></span>
                <div class="field">
                    <select class="full" name="qualificationType" id="qualificationType" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> readonly="readonly" disabled="disabled">
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
                    <input class="full" readonly="readonly" disabled="disabled" type="text" name="otherQualificationTypeName" id="otherQualificationTypeName" value="${(languageQualification.otherQualificationTypeName?html)!}" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled" />
                </div>
            </div>
            <@spring.bind "languageQualification.otherQualificationTypeName" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <label id="lbl-dateOfExamination" class="plain-label grey-label">Date of Examination<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.date'/>"></span>
                <div class="field">
                    <#if (!applicationForm.isDecided() && !applicationForm.isWithdrawn())>
                        <input class="half date" value="${(languageQualification.dateOfExamination?string('dd MMM yyyy'))!}" name="dateOfExamination" id="dateOfExamination" readonly="readonly" disabled="disabled"/>                      
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
                <label id="lbl-overallScore" class="plain-label grey-label">Overall Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.overall'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.overallScore?html)!}" name="overallScore" id="overallScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                <label id="lbl-readingScore" class="plain-label grey-label">Reading Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.reading'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.readingScore?html)!}" name="readingScore" id="readingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                <label id="lbl-writingScore" class="plain-label grey-label">Essay / Writing Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.writing'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.writingScore?html)!}" name="writingScore" id="writingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                <label id="lbl-speakingScore" class="plain-label grey-label">Speaking Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.speaking'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.speakingScore?html)!}" name="speakingScore" id="speakingScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                <label id="lbl-listeningScore" class="plain-label grey-label">Listening Score<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.score.listening'/>"></span>
                <div class="field">
                    <input class="full" readonly="readonly" type="text" value="${(languageQualification.listeningScore?html)!}" name="listeningScore" id="listeningScoreFree" <#if applicationForm.isDecided() || applicationForm.isWithdrawn() || (personalDetails.isLanguageQualificationAvailableSet() && !personalDetails.getLanguageQualificationAvailable()) >disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>
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
                <label id="lbl-examTakenOnline" class="plain-label grey-label">Did you sit the exam online?<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.exam.online'/>"></span>
                <div class="field">
                    <label>
                        <input type="radio" name="examTakenOnline" id="examTakenOnlineYes" value="true" readonly="readonly" disabled="disabled"
                        <#if languageQualification.isExamTakenOnlineSet() && languageQualification.getExamTakenOnline()>
                            checked="checked"
                        </#if>
                        <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                            disabled="disabled"
                        </#if>/> Yes
                    </label>                            
                    <label>
                        <input type="radio" name="examTakenOnline" id="examTakenOnlineNo" value="false" readonly="readonly" disabled="disabled"
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
                <span id="lbl-languageQualificationDocument" class="plain-label grey-label">Certificate (PDF)</span>
                <span class="hint" data-desc="<@spring.message 'personalDetails.languageQualification.pdf'/>"></span>
                <div class="field <#if languageQualification.languageQualificationDocument??>uploaded</#if>" id="uploadFields">                      
                    <input id="languageQualificationDocument" data-type="LANGUAGE_QUALIFICATION" data-reference="Language Qualification" class="full" type="file" name="file" value="" disabled="disabled" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> readonly="readonly" disabled="disabled"/>                    
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
                    <button class="blue" id="addLanguageQualificationButton" type="button" style="display:none;">Add</button>
                </div>
            </div>
