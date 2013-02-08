<#if applicationForm.qualifications?has_content> 
    <#assign hasQualifications = true> 
<#else> 
    <#assign hasQualifications = false> 
</#if> 
<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="qualifications-H2" class="no-arrow empty">Qualifications</h2>
<div class="open">

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
                <#assign anyQualificationEnabled = false>
                <#list applicationForm.qualifications as existingQualification>
                <#assign encQualificationId = encrypter.encrypt(existingQualification.id) />
                <tr>
                    <td>
                        <input type="checkbox" name="qualificationSendToUcl" value="${encQualificationId}"  
                        <#if existingQualification.sendToUCL?? && existingQualification.sendToUCL>checked="checked"</#if> 
                        <#if !(existingQualification.proofOfAward?? && existingQualification.proofOfAward.id??)>
                            disabled="disabled"
                        <#else>
                            <#assign anyQualificationEnabled = true>
                        </#if>
                        data-desc="<#if existingQualification.proofOfAward?? && existingQualification.proofOfAward.id??>Send transcript for offer processing<#else>Transcript not provided</#if>" 
                        />
                    </td>
        
                    <td>
                        <#if existingQualification.proofOfAward?? && existingQualification.proofOfAward.id??>
                            <#assign encProofOfAwardId = encrypter.encrypt(existingQualification.proofOfAward.id) /> 
                            <a href="<@spring.url '/download?documentId=${encProofOfAwardId}'/>" data-desc="Proof Of Award" class="button-hint" target="_blank"> 
                                <#if existingQualification.otherQualificationInstitution?has_content> 
                                    ${(existingQualification.otherQualificationInstitution?html)!}
                                <#else>
                                    ${(existingQualification.qualificationInstitution?html)!} 
                                </#if> 
                            ${(existingQualification.qualificationSubject?html)!} (${(existingQualification.qualificationGrade?html)!}) </a> 
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
                    <td><a name="showQualificationLink" id="showQualificationLink_${encQualificationId}" toggles="qualification_${encQualificationId}" class="button-show button-hint" data-desc="Show">edit</a></td>
                    <td></td>
                </tr>
                </#list>
            </tbody>
        </table>
        
        <@spring.bind "sendToPorticoData.emptyQualificationsExplanation" />

        <input type="hidden" name="showExplanationText" id="showExplanationText" value="${spring.status.errorCodes?seq_contains("portico.submit.explanation.empty")?string("yes", "no")}" />
        
        <#if spring.status.errorCodes?seq_contains("portico.submit.explanation.empty")>
            <div class="section-error-bar">
                <span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span> <span class="invalid-info-text">
                    <#if anyQualificationEnabled>
                        You have not selected any transcripts to submit for offer processing. <b>You must explain why.</b>
                    <#else>
                        You must explain why no transcripts have been not selected to submit for offer processing.
                    </#if>
                </span>
            </div>
        <#elseif spring.status.errorCodes?seq_contains("portico.submit.qualifications.exceed")>
            <div class="section-error-bar">
                <span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span> <span class="invalid-info-text">
                    Select the proof award documents that you wish to send to UCL Admissions. <b>You may select a maximum of 2.</b>
                </span>
            </div>
        <#else>
            <div class="section-info-bar">
                <#if applicationForm.qualifications?size == 0>
                    It looks like you wish to approve an applicant that has no known qualifications. Please explain why you wish to do this.
                <#else>
                    Select a maximum of two qualification transcripts to submit for offer processing.
                </#if>
            </div>
        </#if>
        
        <div class="row-group" id="explanationArea" style="display:none">
            <div class="row">
                <span class="plain-label">Explanation<em>*</em></span> <span class="hint" data-desc="Explain why you wish to submit this application for offer processing without any accompanying transcript."></span>
                <div class="field">
                    <textarea cols="80" rows="5" class="max" id="explanationText" name="explanationText">${(sendToPorticoData.emptyQualificationsExplanation?html)!""}</textarea>
                </div>
            </div>
        </div>

        <#if hasQualifications> 
        <#list applicationForm.qualifications as qualification>
            <#assign encQualificationId = encrypter.encrypt(qualification.id) />

        <div class="row-group" id="qualification_${encQualificationId}" style="display:none">

            <!-- Header -->
            <div class="admin_row">
                <label class="admin_header">Qualification</label>
                <div class="field">&nbsp</div>
            </div>

            <!-- Provider -->
            <div class="admin_row">
                <span class="admin_row_label">Institution Country</span>
                <div class="field" id="qualificationInstitutionCountry">${(qualification.institutionCountry.name?html)!"Not Provided"}</div>
            </div>

            <div class="admin_row">
                <span class="admin_row_label">Institution / Provider Name</span>
                <div class="field" id="qualificationInstitution">${(qualification.qualificationInstitution?html)!"Not Provided"}</div>
            </div>

            <div class="admin_row">
                <span class="admin_row_label">Other Institution / Provider Name</span>
                <div class="field" id="qualificationInstitution">${(qualification.otherQualificationInstitution?html)!"Not Provided"}</div>
            </div>

            <!-- Type -->
            <div class="admin_row">
                <span class="admin_row_label">Qualification Type</span>
                <div class="field" id="qualificationType">${(qualification.qualificationType.name?html)!"Not Provided"}</div>
            </div>

            <!-- Title / Subject -->
            <div class="admin_row">
                <span class="admin_row_label">Title / Subject</span>
                <div class="field" id="qualificationSubject">${(qualification.qualificationSubject?html)!"Not Provided"}</div>
            </div>

            <!-- Language (in which programme was undertaken) -->
            <div class="admin_row">
                <span class="admin_row_label">Language of Study</span>
                <div class="field" id="qualificationLanguage">${(qualification.qualificationLanguage?html)!"Not Provided"}</div>
            </div>

            <!-- Start date -->
            <div class="admin_row">
                <span class="admin_row_label">Start Date</span>
                <div class="field" id="qualificationStartDate">${(qualification.qualificationStartDate?string('dd MMM yyyy'))!"Not Provided"}</div>
            </div>

            <!-- Has qualification been awarded? -->
            <div class="admin_row">
                <span class="admin_row_label">Has this qualification been awarded?</span>
                <div class="field" id="qualificationCompleted">${(qualification.completed?capitalize)!"Not Provided"}</div>
            </div>

            <!-- Qualification grade -->
            <div class="admin_row">
                <span class="admin_row_label">Grade / Result /GPA</span>
                <div class="field" id="qualificationGrade">${(qualification.qualificationGrade?html)!"Not Provided"}</div>
            </div>

            <!-- Award date -->
            <div class="admin_row">
                <span class="admin_row_label">Award Date</span>
                <div class="field" id="qualificationAwardDate">${(qualification.qualificationAwardDate?string('dd MMM yyyy'))!"Not Provided"}</div>
            </div>

            <!-- Attachment / supporting document  -->
            <div class="admin_row">
                <span class="admin_row_label">Transcript (PDF)</span>
                <div class="field" id="referenceDocument">
                    <#if qualification.proofOfAward??> <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(qualification.proofOfAward.id))!}'/>" target="_blank">${(qualification.proofOfAward.fileName)!}</a> <#else> Not Provided </#if>
                </div>

            </div>
        </div>
        </#list>
        <#else>
            <div class="row-group">
            <div class="row">
                <span class="admin_header">Qualification</span>
                <div class="field">Not Provided</div>
            </div>
        </div>
        </#if>
        <div class="buttons">
            <button name="qualificationClearButton" type="button" id="qualificationClearButton" class="clear">Clear</button>
            <button type="button" id="qualificationCloseButton" class="blue">Close</button>
            <button type="button" id="qualificationSaveButton" class="blue">Save</button>
        </div> 
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/qualifications_portico_validation.js' />"></script>
