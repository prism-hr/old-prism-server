<#if applicationForm.referees?has_content> 
    <#assign hasReferees = true> 
<#else> 
    <#assign hasReferees = false> 
</#if> 
<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="referee-H2" class="no-arrow empty">References</h2>

<div class="open">
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
        <div class="section-info-bar">
            Select two completed references to submit for offer processing. You may also enter a reference on behalf of a referee by clicking the provide reference icon.
        </div>
    </#if>
    
    <#include "/private/staff/admin/application/components/referees_edit_view_stack.ftl"/>
    
    <input type="hidden" name="editedRefereeId" id="editedRefereeId" value="${(editedRefereeId)!}" />
    <div class="buttons">
        <button name="refereeClearButton" type="button" id="refereeClearButton" class="clear">Clear</button>
        <button type="button" id="refereeCloseButton" class="blue">Close</button>
        <button type="button" id="refereeSaveButton" class="blue">Save</button>
    </div>
</#if>
</div>

<script type="text/javascript">
    var $closeReferenceSectionAfterSaving = false;
    var $postRefereesDataUrl = "/pgadmissions/approval/postRefereesData";
    var $postReferenceUrl = "/pgadmissions/approval/postReference";
</script>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/admin/references.js' />"></script>
