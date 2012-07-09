<div id="programme-details">
  <div class="row">
    <label>Programme</label>
    <#if applicationForm.researchHomePage??>
    	<a href="${applicationForm.researchHomePage}">${applicationForm.program.code} - ${applicationForm.program.title}</a>
    <#else>
    	${applicationForm.program.code} - ${applicationForm.program.title}
    </#if>
  </div>
    
  <div class="row">
    <label>Application Number</label>
    ${applicationForm.applicationNumber}
  </div>
    
  <div class="row half">
    <label>Submitted</label>
    ${(applicationForm.submittedDate?string("dd MMM yyyy"))!"In Progress"}    
  </div>
  <span style="float:right">
   <label>Actions:</label>
	<#include "/private/common/actions.ftl"/>   
   </span>

</div>