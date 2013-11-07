<#import "/spring.ftl" as spring />
<#if applicationForm??>
    <#assign application = applicationForm>
</#if>

<#assign applicationDescriptor = applicationDescriptors[application.applicationNumber]>
 
<#assign actions = applicationDescriptor.actions>
<select class="actionType" name="app_[${application.applicationNumber?html}]">
    <option>Actions</option>
    <#list actions as action>
      <#if action.id == "emailApplicant">
        <option value="emailApplicant" data-email="${application.applicant.email?html}" data-applicationnumber="${application.applicationNumber?html}">${action.displayName}</option>
      <#else>
        <option value="${action.id}">${action.displayName}</option>
      </#if>
    </#list>
            
</select>
