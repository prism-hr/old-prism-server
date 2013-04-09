<#import "/spring.ftl" as spring />
<#if application??>
    <#assign application = application>
<#elseif applicationForm??>
    <#assign application = applicationForm>
</#if>

<#assign actionsDefinition = actionDefinitions[application.applicationNumber]>
 
<select id="actionTypeSelect" class="actionType" name="app_[${application.applicationNumber}]">
    <option>Actions</option>
    
    <#assign actions = actionsDefinition.actions>
    <#list actions?keys as actionName>
        <option value="${actionName}">${actions[actionName]}</option>
    </#list>
</select>
