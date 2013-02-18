<#if applicationForm.qualifications?has_content> 
    <#assign hasQualifications = true> 
<#else> 
    <#assign hasQualifications = false> 
</#if> 
<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="qualifications-H2" class="no-arrow empty">Qualifications</h2>
<div class="open">

        <#include "/private/staff/admin/application/components/qualifications_table.ftl"/>

        <div class="section-info-bar">
            You may select a maximum of two qualification transcripts to submit for offer processing.
        </div>

        <#if hasQualifications> 

        <#include "/private/staff/admin/application/components/qualifications_edit_view_stack.ftl"/>
        
        <div class="buttons">
            <button name="qualificationClearButton" type="button" id="qualificationClearButton" class="clear">Clear</button>
            <button type="button" id="qualificationCloseButton" class="blue">Close</button>
            <button type="button" id="qualificationSaveButton" class="blue">Save</button>
        </div> 
        <#else>
            <div class="row-group">
            <div class="row">
                <span class="admin_header">Qualification</span>
                <div class="field">Not Provided</div>
            </div>
        </div>
        </#if>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/admin/qualifications.js' />"></script>