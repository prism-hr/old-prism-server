<div id="programme-details">
  <div class="row">
    <label>Programme</label>
    ${applicationForm.program.code} - ${applicationForm.program.title}
  </div>
    
  <div class="row">
    <label>Application Number</label>
    ${applicationForm.applicationNumber}
  </div>
    
  <div class="row">
    <label>Submitted</label>
    ${(applicationForm.submittedDate?string("dd MMM yyyy"))!}
  </div>
</div>
<hr/>