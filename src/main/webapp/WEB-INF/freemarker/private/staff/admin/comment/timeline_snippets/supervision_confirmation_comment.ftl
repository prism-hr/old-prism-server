<#assign supervisor = comment.supervisor>

<li>                          
  <div class="box">
    <div class="title">
      <span class="icon-role ${role}" data-desc="${role?cap_first}"></span>
      <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!} <#if supervisor.isPrimary> (Primary)</#if></span>
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

<#if comment.projectTitle??>
    <li>                          
      <div class="box">
        <div class="title">
          <span class="icon-role ${role}" data-desc="${role?cap_first}"></span>
          <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!} <#if supervisor.isPrimary> (Primary)</#if></span>
          <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
        </div>
            
            <p class="project_title"><span/>
                <b>Project Title:</b> ${(comment.projectTitle?html)!}
            </p>
            
        
      </div>
    </li>
</#if>

<#if comment.recommendedConditionsAvailable??>
    <li>                          
      <div class="box">
        <div class="title">
          <span class="icon-role ${role}" data-desc="${role?cap_first}"></span>
          <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!} <#if supervisor.isPrimary> (Primary)</#if></span>
          <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
        </div>
            
            <p class="start_date"><span/>
                <b>${comment.recommendedStartDate?string('dd MMM yy')}</b> 
            </p>
            
            <#if comment.recommendedConditionsAvailable>
                <p class="conditional_offer"><span/>
                    <b>Recommended conditions: </b> ${(comment.recommendedConditions?html)!}
                </p>
            <#else>
                <p class="unconditional_offer"><span/>
                    <b>Unconditional offer recommended.</b>
                </p>
            </#if>
            
        
      </div>
    </li>
</#if>
