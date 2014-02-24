<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="qualifications-H2" class="no-arrow empty">Qualifications</h2>

<div id="qualificationsTable" class="open">
	
	<@spring.bind "sendToPorticoData.qualificationsSendToPortico"/>    
	
	<form>
	
		<#if applicationForm.hasQualificationsWithTranscripts()>
	    	<#include "/private/staff/supervisors/components/qualifications_table.ftl"/>
	    </#if>
	    
	    <#if spring.status.errorCodes?seq_contains("portico.submit.no.qualification.or.explanation") || 
	    	spring.status.errorCodes?seq_contains("portico.submit.qualifications.exceed")>
	    	 <div class="alert alert-error">
	            <i class="icon-warning-sign" data-desc="Please complete all of the mandatory fields in this section."></i>
	   	<#else>
	   		 <div class="alert alert-info">
	         	<i class="icon-info-sign"></i>  
	    </#if>
	    
			    <#if applicationForm.hasQualificationsWithTranscripts()>
			    	You must select a minimum of one and a maximum of two qualification transcripts to submit for offer processing. 
			    <#else>
			    	The application has no qualification transcripts to submit for offer processing. 
			    </#if>
			    
			    	Should you wish an offer to be considered without any supporting transcripts, please enter your case for 
			    		dispensation in the comment field below. 		
	    </div>
				
	    <div class="row-group" id="explanationArea">
	        <div class="row">
	            <label for="explanationText" id='explanationTextLabel' class="plain-label">Case for Dispensation
	            
	            <#if !applicationForm.hasQualificationsWithTranscripts()>
	            	<em>*</em>
	            </#if>
	            
	            </label>
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
	
	    <#if applicationForm.hasQualificationsWithTranscripts()>
	    	<#include "/private/staff/supervisors/components/qualifications_edit_view_stack.ftl"/>     
	    </#if>
		
		<div class="buttons">
	    	<button name="qualificationClearButton" type="button" id="qualificationClearButton" class="btn">Clear</button>
	    
	    	<#if applicationForm.hasQualificationsWithTranscripts()>
	    		<button type="button" id="qualificationCloseButton" class="btn">Close</button>
	    	</#if>
	    
	    	<button type="button" id="qualificationSaveButton" class="btn btn-primary">Save</button>
		</div>
	</form>	
</div>