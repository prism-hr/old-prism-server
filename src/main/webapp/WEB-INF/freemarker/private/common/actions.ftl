<#import "/spring.ftl" as spring />
<#if application??>
    <#assign application = application>
<#elseif applicationForm??>
    <#assign application = applicationForm>
</#if>

<#assign actionsDefinition = actionDefinitions[application.applicationNumber]>
 
<select id="actionTypeSelect" class="actionType" name="app_[${application.applicationNumber?html}]">
    <option>Actions</option>
    
    <#assign actions = actionsDefinition.actions>
    <#list actions?keys as actionName>
        <#if !user.isInRole('APPLICANT') && actionName == "emailApplicant">
            <option value="emailApplicant" data-email="${application.applicant.email?html}" data-applicationnumber="${application.applicationNumber?html}">${actions[actionName]}</option>
        <#else>
            <option value="${actionName?html}">${actions[actionName]}</option>
        </#if>
    </#list>
    
</select>
