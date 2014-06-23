<#if applicationForm.program?has_content>
  <#assign hasProgram = true>
<#else>
  <#assign hasProgram = false>
</#if>

<#if programme?has_content>
  <#assign hasProgramme = true>
<#else>
  <#assign hasProgramme = false>
</#if>

<#-- Assignments -->
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#-- Programme Details Rendering -->
<h2 id="programme-H2" class="no-arrow tick">Programme</h2>

<div>
  <form>
  
    <div class="row-group">
    
      <!-- Programme name (disabled) -->
      <div class="admin_row">
        <label class="admin_row_label">Programme</label>
        <div class="field">${(applicationForm.program.title?html)!}</div>
      </div>
      
      <!-- Study option -->
      <div class="admin_row">
        <label class="admin_row_label">Study Option</label>
        <div class="field">
          <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.studyOption??>
          ${applicationForm.programmeDetails.studyOption}
          </#if>
        </div>
      </div>
      
      <!-- Start date -->
      <div class="admin_row">
        <label class="admin_row_label">Start Date</label>
        <div class="field">${(applicationForm.programmeDetails.startDate?string('dd MMM yyyy'))!}</div>
      </div>
      
      <!-- Project -->
      <div class="admin_row">
        <label class="admin_row_label">Project</label>
        <div class="field"><#if (applicationForm.projectTitle)?has_content>${(applicationForm.projectTitle?html)}<#else>Not Required</#if></div>
      </div>
      
      <!-- Referrer -->
      <div class="admin_row">
        <label class="admin_row_label">How did you find us?</label>
        <div class="field">
          <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.sourcesOfInterest??>                    
          ${applicationForm.programmeDetails.sourcesOfInterest.name?html}
          </#if>                    
        </div>
      </div>
      
      <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.sourcesOfInterestText?has_content>
      <!-- Referrer Free Text-->
      <div class="admin_row">
        <label class="admin_row_label">Please explain</label>
        <div class="field">
          ${applicationForm.programmeDetails.sourcesOfInterestText?html}
        </div>
      </div>
      </#if>
      
    </div>

    <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.suggestedSupervisors?? && (applicationForm.programmeDetails.suggestedSupervisors?size > 0) >
    <#list applicationForm.programmeDetails.suggestedSupervisors! as supervisor>
      
    <div class="row-group">
      
      <div class="admin_row">
        <label class="admin_header">Supervision (${supervisor_index + 1})</label>
        <div class="field">&nbsp</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Supervisor First Name:</label>
        <div class="field">${(supervisor.firstname?html)!}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Supervisor Last Name:</label>
        <div class="field">${(supervisor.lastname?html)!}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Supervisor Email:</label>
        <div class="field">${supervisor.email?html}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Is this supervisor aware of the application? </label>
        <div class="field"><#if supervisor.aware??>Yes<#else>No</#if></div>
      </div>
      
    </div>
    </#list>
      
    <#else>
    <div class="row-group">
      <div class="row">
        <label class="admin_header">Supervision</label>
        <div class="field">Not Provided</div>
      </div>
    </div>
    </#if>
  
  </form>
</div>
