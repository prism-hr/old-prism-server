<#assign supervisor = comment.supervisor>

<#if supervisor.confirmedSupervision??>
    <#if !supervisor.confirmedSupervision>
        <p><em>${(supervisor.declinedSupervisionReason?html)!}</em></p>
    </#if>
    
    <h3 class="answer <#if supervisor.confirmedSupervision>yes<#else>no</#if>">
        <span data-desc="<#if supervisor.confirmedSupervision>Yes<#else>No</#if>"></span>Confirm that you are willing to provide primary supervision?
    </h3>
</#if>
