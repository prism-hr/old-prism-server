<#if applicationForm.qualifications?has_content> 
    <#assign hasQualifications = true> 
<#else> 
    <#assign hasQualifications = false> 
</#if> 
<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="qualifications-H2" class="no-arrow empty">Qualifications</h2>
<div class="open">

        <#include "/private/staff/supervisors/components/qualifications_table.ftl"/>
        
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
                <#if sendToPorticoData.emptyQualificationsExplanation??>
                    <div class="alert alert-info">
          			<i class="icon-info-sign"></i>
                        Explain why no transcripts have been not selected to submit for offer processing.
                    </div>
                <#else>
                    <div class="alert alert-info">
          				<i class="icon-info-sign"></i>           
                        <#if anyQualificationEnabled>
                            Select a maximum of two qualification transcripts to submit for offer processing.
                        <#else>
                            It looks like you wish to approve an applicant that has no known qualifications containing transcripts. Please explain why you wish to do this.
                        </#if>
                    </div>
                </#if>
            
        </#if>
        
        <div class="row-group" id="explanationArea" style="display:none">
            <div class="row">
                <label for="explanationText" class="plain-label">Explanation<em>*</em></label> 
                <span class="hint" data-desc="Explain why you wish to submit this application for offer processing without any accompanying transcript."></span>
                <div class="field">
                    <textarea cols="80" rows="5" class="max" id="explanationText" name="explanationText">${(sendToPorticoData.emptyQualificationsExplanation?html)!""}</textarea>
                </div>
            </div>
        </div>

        <#if hasQualifications> 

        <#include "/private/staff/supervisors/components/qualifications_edit_view_stack.ftl"/>
        
        <#else>
            <div class="row-group">
            <div class="row">
                <span class="admin_header">Qualification</span>
                <div class="field">Not Provided</div>
            </div>
        </div>
        </#if>
        <div class="buttons">
            <button name="qualificationClearButton" type="button" id="qualificationClearButton" class="btn">Clear</button>
            <button type="button" id="qualificationCloseButton" class="btn">Close</button>
            <button type="button" id="qualificationSaveButton" class="btn btn-primary">Save</button>
        </div> 
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/qualifications_portico_validation.js' />"></script>
