 <div id="programme-details">
  	<div class="row">
    	<label>Programme</label>
        ${applicationForm.program.code} - ${applicationForm.program.title}
    </div>
    
  	<div class="row">
    	<label>Application Number</label>
        ${applicationForm.id?string("######")}
    </div>
    
     
  	<div class="row">
    	<label>Date Submitted</label>
        ${(applicationForm.submittedDate?string("dd-MMM-yyyy hh:mm a"))!}
    </div>
     

</div>
<hr/>