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

    <div class="section-info-bar">
        You may select two completed references to submit for offer processing. You may also enter a reference on behalf of a referee by clicking the provide reference icon.
    </div>
    
    <#include "/private/staff/admin/application/components/referees_edit_view_stack.ftl"/>

    <input type="hidden" name="editedRefereeId" id="editedRefereeId" value="${(editedRefereeId)!}" />
    <div class="buttons">
        <button name="refereeClearButton" type="button" id="refereeClearButton" class="clear">Clear</button>
        <button type="button" id="refereeCloseButton" class="blue">Close</button>
        <button type="button" id="refereeSaveButton" class="blue">Save</button>
    </div>
</div>
</#if>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/admin/references.js' />"></script>