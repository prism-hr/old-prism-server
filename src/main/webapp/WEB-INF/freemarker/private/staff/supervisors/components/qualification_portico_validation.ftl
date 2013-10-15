<#if applicationForm.qualifications?has_content> 
    <#assign hasQualifications = true> 
<#else> 
    <#assign hasQualifications = false> 
</#if> 
<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="qualifications-H2" class="no-arrow empty">Qualifications</h2>

<div class="open">
	<form>
	
	    <@spring.bind "sendToPorticoData.qualificationsSendToPortico" />
	
		<#if hasQualifications>
        	<#include "/private/staff/supervisors/components/qualifications_table.ftl"/>
        </#if>
        
        <#if spring.status.error??>
	        <div class="alert alert-info">
	         	<i class="icon-info-sign"></i>
	   	<#else>
	   	    <div class="alert alert-error">
                <i class="icon-warning-sign" data-desc="Please complete all of the mandatory fields in this section."></i>
        </#if>
         	You must select at least one and a maximum of 2 transcripts to submit for offer processing. Should you
         		wish an offer to be considered without any supporting transcripts, please enter your case for dispensation
         		in the comment field below.
        </div>
   				
        <div class="row-group" id="explanationArea">
            <div class="row">
                <label for="explanationText" id='explanationTextLabel' class="plain-label">Case for Dispensation<em>*</em></label> 
                <span class="hint" data-desc="Explain why you wish to submit this application for offer processing without any accompanying transcript."></span>
                <div class="field">
                    <textarea cols="80" rows="5" class="max" id="explanationText" name="explanationText">${(sendToPorticoData.emptyQualificationsExplanation?html)!""}</textarea>
                    <@spring.bind "sendToPorticoData.emptyQualificationsExplanation"/>
                    <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> 
                        <i class="icon-warning-sign"></i>
                        ${error}
                      </div>
                    </#list>
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
            
            <#if hasQualifications>
            	<button type="button" id="qualificationCloseButton" class="btn">Close</button>
            </#if>
            
            <button type="button" id="qualificationSaveButton" class="btn btn-primary">Save</button>
        </div>
       	<input type="hidden" name="showExplanationText" id="showExplanationText" value="${spring.status.errorCodes?seq_contains("portico.submit.explanation.required")?string("yes", "no")}" />
  </form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/qualifications_portico_validation.js' />"></script>