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
        <option value="${actionName?html}">${actions[actionName]}</option>
    </#list>
    <option value="emailApplicant" data-email="${application.applicant.email?html}" data-applicationnumber="${application.applicationNumber?html}">Email Applicant</option>
    
</select>
