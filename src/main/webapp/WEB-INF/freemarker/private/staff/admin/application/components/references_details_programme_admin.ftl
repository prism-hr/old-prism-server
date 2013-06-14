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

	<div class="alert alert-info">
    <i class="icon-info-sign"></i> Select two completed references to submit for offer processing. You can also create and update references and provide them on behalf of the Applicant's referees.
    </div>

    <#include "/private/staff/admin/application/components/referees_table.ftl"/>
    
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
        <button name="refereeClearButton" type="button" class="btn" id="refereeClearButton">Clear</button>
        <button type="button" id="refereeCloseButton" class="btn">Close</button>
        <button type="button" id="refereeSaveButton" class="btn btn-primary">Save selection</button>
    </div>
</#if>
</div>

<script type="text/javascript">
    var $closeReferenceSectionAfterSaving = true;
    var $postRefereesDataUrl = "/pgadmissions/editApplicationFormAsProgrammeAdmin/postRefereesData";
    var $postReferenceUrl = "/pgadmissions/editApplicationFormAsProgrammeAdmin/postReference";
    var $postRefereeAndReferenceUrl = "/pgadmissions/editApplicationFormAsProgrammeAdmin/postRefereeAndReference";
</script>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/admin/references.js' />"></script>
