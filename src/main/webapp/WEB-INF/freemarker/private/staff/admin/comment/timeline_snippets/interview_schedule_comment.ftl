<#if user.isApplicant(applicationForm) || user.isInterviewerOfApplicationForm(applicationForm) || user.isInRole('SUPERADMINISTRATOR')>

  <li>                          
    <div class="box">
    	
      <div class="title">
        <span class="icon-role ${role}" data-desc="${role?cap_first}"></span>
        <span class="name" data-desc="${(comment.user.email?html)!}">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span>
        <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
      </div>   
      
      <#if user.isInterviewerOfApplicationForm(applicationForm) || user.isInRole('SUPERADMINISTRATOR')>
        <p><strong>Instructions for interviewers:</strong> <em>${(comment.furtherInterviewerDetails?html)!"Not Provided"}</em></p>
      </#if>
      
      <#if user.isApplicant(applicationForm) || user.isInRole('SUPERADMINISTRATOR')>
        <p><strong>Instructions for applicant:</strong> <em> ${(comment.furtherDetails?html)!"Not Provided"}</em></p>
      </#if>
    
    </div>
  </li>             

</#if>   
