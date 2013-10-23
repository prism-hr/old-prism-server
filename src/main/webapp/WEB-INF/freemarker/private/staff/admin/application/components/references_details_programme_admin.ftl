<#if applicationForm.referees?has_content> 
    <#assign hasReferees = true> 
<#else> 
    <#assign hasReferees = false> 
</#if> 
<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="referee-H2" class="no-arrow empty">References</h2>

<div id="refereesTable" class="open">
<#if hasReferees>

	<form>
	
		<#include "/private/staff/admin/application/components/referees_table.ftl"/>

		<div class="alert alert-info">
	    	<i class="icon-info-sign"></i>
	    	&nbsp;Launch the form below to provide and edit references on behalf of referees. If you wish to at this stage, 
	    		you may also select the references that you wish to submit with your offer recommendation.
	    		You will be required to do this when the application reaches the Approval stage.
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
        <button name="refereeClearButton" type="button" class="btn" id="refereeClearButton">Clear</button>
        <button type="button" id="refereeCloseButton" class="btn">Close</button>
        <button type="button" id="refereeSaveButton" class="btn btn-primary">Save selection</button>
    </div>
</#if>
</div>

<script type="text/javascript">
	var $editRefereesDataUrl = "/pgadmissions/editApplicationFormAsProgrammeAdmin/editReferenceData";
    var $postRefereesDataUrl = "/pgadmissions/editApplicationFormAsProgrammeAdmin/postRefereesData";
    var $postReferenceUrl = "/pgadmissions/editApplicationFormAsProgrammeAdmin/postReference";
    var $postRefereeAndReferenceUrl = "/pgadmissions/editApplicationFormAsProgrammeAdmin/postRefereeAndReference";
</script>

