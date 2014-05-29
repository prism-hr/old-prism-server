<#import "/spring.ftl" as spring/> 
<#setting locale = "en_US">

<#if applicationForm.referees?has_content> 
    <#assign hasReferees = true> 
<#else> 
    <#assign hasReferees = false> 
</#if>

<h2 id="referee-H2" class="no-arrow empty">References</h2>

<div id="refereesTable" class="open">
<#if hasReferees>

    <#include "/private/staff/admin/application/components/referees_table.ftl"/>

    <@spring.bind "sendToPorticoData.refereesSendToPortico" />
    
    <form>
    
	    <#if spring.status.errorCodes?seq_contains("portico.submit.referees.invalid")>
	        <div class="alert alert-error">
	        	<i class="icon-warning-sign" data-desc="Please complete all of the mandatory fields in this section."></i>
	    <#else>
	        <div class="alert alert-info">
	        	<i class="icon-info-sign"></i>
	    </#if>    
	        
		        Select the references that you wish to submit with your offer recommendation. <b> You must select 2 completed references</b>.
		        	You can also edit existing references and/or create new references on behalf of referees if you need to.
	    </div>
	    
	    <#list applicationForm.referees as referee>
	        <#assign encRefereeId = encrypter.encrypt(referee.id) />
	        <div id="referee_${encRefereeId}" style="display:none">
	            
	            <#include "/private/staff/admin/application/components/referee_view.ftl"/>
	            
	            <#if referee.hasResponded() && !referee.isDeclined()>
	                <#include "/private/staff/admin/application/components/reference_edit.ftl"/>
	            </#if>
	        
	            <#if !referee.hasResponded() && !referee.isDeclined()>
	                <#include "/private/staff/admin/application/components/reference_add.ftl"/>
	            </#if>
	        </div>
	    </#list>
	    
	    <div id="referee_newReferee" style="display:none">
	        <#assign encRefereeId = "newReferee" />
	        <#include "/private/staff/admin/application/components/referee_add.ftl"/>
	        <#include "/private/staff/admin/application/components/reference_add.ftl"/>
	    </div>
	    
	</form>
	    
    <input type="hidden" name="editedRefereeId" id="editedRefereeId" value="${(editedRefereeId)!}" />
    <div class="buttons">
        <button id="newReferenceButton" class="btn btn-success right" type="button">New Reference</button>
        <button name="refereeClearButton" type="button" id="refereeClearButton" class="btn">Clear</button>
        <button type="button" id="refereeCloseButton" class="btn">Close</button>
        <button type="button" id="refereeSaveButton" class="btn btn-primary">Save</button>
    </div>
    
</#if>
</div>

<script type="text/javascript">
	var $editRefereesDataUrl = "/pgadmissions/approval/editReferenceData";
    var $postRefereesDataUrl = "/pgadmissions/approval/postRefereesDataAndValidateForApproval";
</script>