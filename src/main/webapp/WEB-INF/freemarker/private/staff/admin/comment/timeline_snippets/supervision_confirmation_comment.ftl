<#assign supervisor = comment.supervisor>

<li>                          
  <div class="box">
    <div class="title">
      <span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
      <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!} <#if supervisor.isPrimary> (Primary)</#if></span> <span class="commented">commented:</span>
      <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
    </div>
    
    <#if supervisor.confirmedSupervision??>
        <#if !supervisor.confirmedSupervision>
            <p><em>${(supervisor.declinedSupervisionReason?html)!}</em></p>
        </#if>
        
        <h3 class="answer <#if supervisor.confirmedSupervision>yes<#else>no</#if>">
            <span data-desc="<#if supervisor.confirmedSupervision>Yes<#else>No</#if>"></span>Confirm that you are willing to provide primary supervision?
        </h3>
    </#if>
    
  </div>
</li>

<#include "project_description_comments.ftl"/>
