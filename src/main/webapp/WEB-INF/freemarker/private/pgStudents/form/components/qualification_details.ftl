<#assign errorCode = RequestParameters.errorCode! /> <#if applicationForm.qualifications?has_content> <#assign hasQualifications = true> <#else> <#assign hasQualifications = false> </#if> <#import "/spring.ftl" as spring /> <#setting locale = "en_US">
<a name="qualification-details"></a>
<h2 id="qualifications-H2" class="empty open">
    <span class="left"></span><span class="right"></span><span class="status"></span> Qualifications
</h2>
<div>

    <#if hasQualifications>

    <table class="existing">
        <colgroup>
            <col style="width: 30px" />
            <col />
            <col style="width: 90px" />
            <col style="width: 30px" />
            <col style="width: 30px" />
        </colgroup>
        <thead>
            <tr>
                <th id="primary-header" colspan="2">Qualification</th>
                <th>Date</th>
                <th>&nbsp;</th>
                <th id="last-col">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
            <#list applicationForm.qualifications as existingQualification>
            <tr>
                <td>
                    <a class="row-arrow">-</a>
                </td>
                
                <td>
                    <#if existingQualification.proofOfAward?? && existingQualification.proofOfAward.id?? > 
                        <#assign encProofOfAwardId = encrypter.encrypt(existingQualification.proofOfAward.id) /> 
                        <a href="<@spring.url '/download?documentId=${encProofOfAwardId}'/>" data-desc="Proof Of Award" class="button-hint" target="_blank">
                        
                            <#if existingQualification.otherQualificationInstitution?has_content>
                                ${(existingQualification.otherQualificationInstitution?html)!} 
                            <#else>
                                ${(existingQualification.qualificationInstitution?html)!} 
                            </#if>
                            ${(existingQualification.qualificationSubject?html)!} (${(existingQualification.qualificationGrade?html)!}) 
                        </a> 
                    <#else>
                        <#if existingQualification.otherQualificationInstitution?has_content>
                            ${(existingQualification.otherQualificationInstitution?html)!} 
                        <#else>
                            ${(existingQualification.qualificationInstitution?html)!} 
                        </#if> 
                        ${(existingQualification.qualificationSubject?html)!} (${(existingQualification.qualificationGrade?html)!}) 
                    </#if>
                </td>
                
                <td>
                    <#if existingQualification.isQualificationCompleted()> 
                        ${(existingQualification.qualificationAwardDate?string('dd MMM yyyy'))!} 
                    <#else> 
                        Expected 
                    </#if>
                </td> 
                
                <#assign encQualificationId = encrypter.encrypt(existingQualification.id) />
                <td>
                    <a name="editQualificationLink" id="qualification_${encQualificationId}" class="button-edit button-hint" data-desc="<#if (!applicationForm.isDecided() && !applicationForm.isWithdrawn())>Edit<#else>Show</#if>">edit</a>
                </td>
                
                <td>
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> 
                        <a name="deleteQualificationButton" data-desc="Delete" id="qualification_${encQualificationId}" class="button-delete button-hint">delete</a> 
                    <#else> 
                        &nbsp; 
                    </#if>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>

    </#if> <input type="hidden" id="qualificationId" name="qualificationId" value="<#if qualification?? && qualification.id??>${encrypter.encrypt(qualification.id)}</#if>" />

    <form>

        <#if errorCode?? && errorCode=="true">
        <div class="section-error-bar">
            <span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span> <@spring.message 'education.qualifications.sectionInfo'/> <b><@spring.message 'education.qualifications.sectionInfoBold'/></b>
        </div>
        <#else>
        <div id="qual-info-bar-div" class="section-info-bar">
            <@spring.message 'education.qualifications.sectionInfo'/> <b><@spring.message 'education.qualifications.sectionInfoBold'/></b>
        </div>
        </#if>

        <div class="row-group">
            <div class="row">
                <span class="plain-label">Institution Country<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.institutionCountry'/>"></span>
                <div class="field">
                    <select class="full" id="institutionCountry" name="institutionCountry"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
                        <option value="">Select...</option> 
                        <#list countries as country>
                            <option value="${encrypter.encrypt(country.id)}"<#if qualification.institutionCountry?? && qualification.institutionCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option> 
                        </#list>
                    </select>
                </div>
            </div>
            <@spring.bind "qualification.institutionCountry" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>

            <!-- Provider -->
            <div class="row">
                <span id="lbl-providerName" class="plain-label grey-label">Institution / Provider Name<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.institutionName'/>"></span>
                <div class="field">
                    <select class="full" id="qualificationInstitution" name="qualificationInstitution"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
                        <option value="">Select...</option>
                        <#list institutions as inst>
                            <option value="${inst.code}" <#if qualification.qualificationInstitutionCode?? && qualification.qualificationInstitutionCode == inst.code> selected="selected"</#if>>${inst.name?html}</option> 
                        </#list>
                        <option value="OTHER" <#if qualification.qualificationInstitutionCode?? && qualification.qualificationInstitutionCode == "OTHER">selected="selected"</#if>>Other</option>
                    </select>
                </div>
            </div>
            <@spring.bind "qualification.qualificationInstitution" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>

            <!-- Other name-->
            <div class="row">
                <span id="lbl-otherInstitutionProviderName" class="plain-label grey-label">Please Specify<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.subject'/>"></span>
                <div class="field">
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> 
                        <input readonly="readonly" disabled="disabled" id="otherInstitutionProviderName" class="full" type="text" value="${(qualification.otherQualificationInstitution?html)!}" /> 
                    <#else> 
                        <input readonly="readonly" id="otherInstitutionProviderName" class="full" type="text" value="${(qualification.otherQualificationInstitution?html)!}" /> 
                    </#if>
                </div>
            </div>
            <@spring.bind "qualification.otherQualificationInstitution" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>
            
            <!-- Qualification type -->
            <div class="row">
                <span class="plain-label">Qualification Type<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.qualificationType'/>"></span>
                <div class="field">
                    <select class="full" id="qqualificationType" name="qqualificationType"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
                        <option value="">Select...</option> 
                        <#list types as type>
                            <option value="${encrypter.encrypt(type.id)}"<#if qualification.qualificationType?? && qualification.qualificationType.id == type.id> selected="selected"</#if>>${type.name?html}</option> 
                        </#list>
                    </select>
                </div>
            </div>
            <@spring.bind "qualification.qualificationType" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>
            
            <!-- Title -->
            <div class="row">
                <span class="plain-label">Qualification Title<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.title'/>"></span>
                <div class="field">
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> <input id="qualificationTitle" class="full" type="text" placeholder="e.g. MSc" value="${(qualification.qualificationTitle?html)!}" /> <#else> <input readonly="readonly" id="qualificationTitle" class="full" type="text" placeholder="e.g. Civil Engineering" value="${(qualification.qualificationTitle?html)!}" /> </#if>
                </div>
            </div>
            <@spring.bind "qualification.qualificationTitle" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>
            
            <!-- Subject -->
            <div class="row">
                <span class="plain-label">Qualification Subject<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.subject'/>"></span>
                <div class="field">
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> <input id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering" value="${(qualification.qualificationSubject?html)!}" /> <#else> <input readonly="readonly" id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering" value="${(qualification.qualificationSubject?html)!}" /> </#if>
                </div>
            </div>
            <@spring.bind "qualification.qualificationSubject" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>

            <!-- Language (in which programme was undertaken) -->
            <div class="row">
                <span class="plain-label">Language of Study<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.language'/>"></span>
                <div class="field">
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> <input id="qualificationLanguage" class="full" type="text" placeholder="e.g. English" value="${(qualification.qualificationLanguage?html)!}" /> <#else> <input readonly="readonly" id="qualificationLanguage" class="full" type="text" placeholder="e.g. English" value="${(qualification.qualificationLanguage?html)!}" /> </#if>
                </div>
            </div>
            <@spring.bind "qualification.qualificationLanguage" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>

            <!-- Start date -->
            <div class="row">
                <span class="plain-label">Start Date<em>*</em></span> <span class="hint" data-desc="<@spring.message 'education.qualifications.startDate'/>"></span>
                <div class="field">
                    <input id="qualificationStartDate" class="half date" type="text" value="${(qualification.qualificationStartDate?string('dd MMM yyyy'))!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if> />
                </div>
            </div>
            <@spring.bind "qualification.qualificationStartDate" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>

        </div>


        <div class="row-group">

            <!-- Has this been awarded? -->
            <div class="row">
                <span class="plain-label">Has this qualification been awarded?</span> <span class="hint" data-desc="<@spring.message 'education.qualifications.hasBeenAwarded'/>"></span>
                <div class="field">
                    <input type="checkbox" name="currentQualificationCB" id="currentQualificationCB"<#if qualification.isQualificationCompleted()> checked="checked"</#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> /> <input type="hidden" name="currentQualification" id="currentQualification" value="<#if qualification.isQualificationCompleted()>YES<#else>NO</#if>" />
                </div>
            </div>

            <!-- Qualification grade -->
            <div class="row">
                <span id="quali-grad-id" class="plain-label"> <#if qualification.isQualificationCompleted()> Grade / Result / GPA<em>*</em> <#else> Expected Grade / Result / GPA<em>*</em> </#if>
                </span> <span class="hint" data-desc="<@spring.message 'education.qualifications.grade'/>"></span>
                <div class="field">
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> <input id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction" value="${(qualification.qualificationGrade?html)!}" /> <#else> <input readonly="readonly" id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction" value="${(qualification.qualificationGrade?html)!}" /> </#if>
                </div>
            </div>
            <@spring.bind "qualification.qualificationGrade" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
            </div>
            </#list>

            <!-- Award date -->
            <div class="row">
                <span id="quali-award-date-lb" class="plain-label<#if !qualification.isQualificationCompleted()> grey-label</#if>">Award Date<#if qualification.isQualificationCompleted()><em>*</em></#if>
                </span> <span class="hint" data-desc="<@spring.message 'education.qualifications.awardDate'/>"></span>

                <div class="field" id="awardDateField">
                    <input type="text" class="half date" id="qualificationAwardDate" name="qualificationAwardDate" value="<#if qualification.isQualificationCompleted()>${(qualification.qualificationAwardDate?string('dd MMM yyyy'))!}</#if>"<#if !qualification.isQualificationCompleted() || applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> />
                </div>
            </div>

            <@spring.bind "qualification.qualificationAwardDate" /> <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span id="qualification-awarddate-error" class="invalid">${error}</span>
                </div>
            </div>
            </#list>

            <!-- Attachment / supporting document -->
            <div class="row">
                <span id="quali-proof-of-award-lb" class="plain-label<#if !qualification.isQualificationCompleted()> grey-label</#if>">Transcript (PDF)</span> <span class="hint" data-desc="<@spring.message 'education.qualifications.proofOfAward'/>"></span>
                <div class="field <#if qualification.proofOfAward??>uploaded</#if>" id="uploadFields">
                    <input id="proofOfAward" data-type="PROOF_OF_AWARD" data-reference="Proof Of Award" class="full" type="file" name="file" value=""<#if !qualification.isQualificationCompleted() || applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/> <span id="qualUploadedDocument"> <input type="hidden" class="file" id="document_PROOF_OF_AWARD" value="${(encrypter.encrypt(qualification.proofOfAward.id))!}" /> <#if qualification.proofOfAward??> <a class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(qualification.proofOfAward.id))!}'/>" target="_blank"> ${(qualification.proofOfAward.fileName?html)!}</a> <a class="button-delete button-hint" data-desc="Change Proof Of Award">delete</a> </#if>
                    </span> <span class="progress" style="display: none;"></span>
                </div>
            </div>

            <!-- Add another button -->
            <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
            <div class="row">
                <div class="field">
                    <button id="addQualificationButton" type="button" class="blue"><#if qualification?? && qualification.id??>Update<#else>Add</#if></button>
                </div>
            </div>
            </#if>

        </div>

        <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')> <@spring.bind "qualification.acceptedTerms" /> <#if spring.status.errorMessages?size &gt; 0>
        <div class="row-group terms-box invalid">

            <#else>
            <div class="row-group terms-box">
                </#if>
                <div class="row">
                    <span class="terms-label"> Confirm that the information that you have provided in this section is true and correct. Failure to provide true and correct information may result in a subsequent offer of study being withdrawn. </span>

                    <div class="terms-field">
                        <input type="checkbox" name="acceptTermsQDCB" id="acceptTermsQDCB" />
                    </div>
                    <input type="hidden" name="acceptTermsQDValue" id="acceptTermsQDValue" />
                </div>
            </div>
            </#if>

            <div class="buttons">
                <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                <button class="clear" type="button" id="qualificationClearButton" name="qualificationClearButton">Clear</button>
                <button class="blue" type="button" id="qualificationsCloseButton" name="qualificationsCloseButton">Close</button>
                <button class="blue" type="button" id="qualificationsSaveButton" value="add">Save</button>
                <#else>
                <button class="blue" type="button" id="qualificationsCloseButton">Close</button>
                </#if>
            </div>
    </form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>
