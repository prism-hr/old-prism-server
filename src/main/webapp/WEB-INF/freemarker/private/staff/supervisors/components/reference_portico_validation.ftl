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

    <#include "/private/staff/admin/application/components/referees_table.ftl"/>

    <@spring.bind "sendToPorticoData.refereesSendToPortico" />
    <#if spring.status.errorCodes?seq_contains("portico.submit.referees.invalid")>
        <div class="section-error-bar">
            <span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span> <span class="invalid-info-text">
                Select the references that you wish to send to UCL Admissions. <b>You must select 2.</b>
            </span>
        </div>
    <#else>
        <div class="alert alert-info">
          			<i class="icon-info-sign"></i>
            Select two completed references to submit for offer processing. You may also enter a reference on behalf of a referee by clicking the provide reference icon.
        </div>
    </#if>
    
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
    
    <input type="hidden" name="editedRefereeId" id="editedRefereeId" value="${(editedRefereeId)!}" />
    <div class="buttons">
        <button name="refereeClearButton" type="button" id="refereeClearButton" class="btn">Clear</button>
        <button type="button" id="refereeCloseButton" class="btn">Close</button>
        <button type="button" id="refereeSaveButton" class="btn btn-primary">Save</button>
    </div>
</#if>
</div>

<script type="text/javascript">
    var $closeReferenceSectionAfterSaving = false;
    var $postRefereesDataUrl = "/pgadmissions/approval/postRefereesData";
    var $postReferenceUrl = "/pgadmissions/approval/postReference";
</script>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/admin/references.js' />"></script>
