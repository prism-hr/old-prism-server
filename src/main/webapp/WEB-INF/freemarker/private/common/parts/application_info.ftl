<div id="programme-details">
  <div class="rsbox">
    <div class="bs-status">
     <span class="label label-info">Status</span>
     
     <div class="icon">
      ${applicationForm.status.displayValue()}
        <span class="icon-status ${applicationForm.status.displayValue()?lower_case?replace(' ','-')}"></span>
      </div>

    
    <#include "/private/common/actions.ftl"/> 
    
   </div>
  <div id="tools">
      <a class="btn btn-small" target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>"><i class="icon-download-alt"></i> Download as PDF</a>
  </div>
</div>
  <div class="row">
    <div class="applicantinfo"> 
      ${applicationForm.applicationNumber} 

      ${(applicationForm.personalDetails.title?capitalize)!} ${(applicationForm.applicant.firstName?html)!} ${(applicationForm.applicant.firstName2?html)!} ${(applicationForm.applicant.firstName3?html)!} ${(applicationForm.applicant.lastName?html)!}
    </div>
  </div>
  <div class="row">
    <label>Submitted</label> 
    ${(applicationForm.submittedDate?string("dd MMM yyyy"))!"In Progress"}
  </div>
  <div class="row">
    <label>Programme</label>
    <#if applicationForm.researchHomePage??>
      <a href="${applicationForm.researchHomePage}">${applicationForm.program.code} - ${applicationForm.program.title}</a>
    <#else>
      ${applicationForm.program.code} - ${applicationForm.program.title}
    </#if>
    
  </div>    

</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/actions.js'/>"></script>