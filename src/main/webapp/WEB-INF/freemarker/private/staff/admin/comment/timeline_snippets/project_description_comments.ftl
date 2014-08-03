<li>                          
  <div class="box">
    <div class="title">
      <span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
      <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span> <span class="commented">commented:</span>
      <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
    </div>
        
    <p class="added"><span/>
        <#assign primarySupervisor = comment.supervisor.user>
        <#assign secondarySupervisor = comment.secondarySupervisor.user>

        ${primarySupervisor.firstName?html} ${primarySupervisor.lastName?html} (Primary), ${secondarySupervisor.firstName?html} ${secondarySupervisor.lastName?html} added as supervisors.
    </p>
                            
  </div>
</li>

<#if comment.projectTitle?has_content>
    <li>                          
      <div class="box">
        <div class="title">
          <span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
          <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span> <span class="commented">commented:</span>
          <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
        </div>
            <p class="project_title"><span data-desc="Project title and description"/>
                <b>${(comment.projectTitle?html)!}</b><br/>
                <i>${(comment.projectAbstract?html)!}</i>
            </p>
      </div>
    </li>
</#if>

<#if comment.recommendedConditionsAvailable??>
    <li>                          
      <div class="box">
        <div class="title">
          <span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
          <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span> <span class="commented">commented:</span>
          <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
        </div>
            
            <p class="start_date"><span/>
           		<b>Provisional start date: </b>${comment.recommendedStartDate?string('dd MMM yy')}.
            </p>
            
            <#if comment.recommendedConditionsAvailable>
                <p class="conditional_offer"><span/>
                    <b>Conditional offer recommended: </b>${(comment.recommendedConditions?html)!}</p>
            <#else>
                <p class="unconditional_offer"><span/><b>Unconditional offer recommended.</b></p>
            </#if>
      </div>
    </li>
</#if>
