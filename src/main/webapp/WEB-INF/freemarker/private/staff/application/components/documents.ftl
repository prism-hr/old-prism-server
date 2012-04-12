	<h2 id="documents-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Documents
	</h2>
    
    <div>
    	

              
		<form>
            <div>
				<div class="row">
	        		<span class="plain-label">Personal Statement (PDF)<em>*</em></span>
	        		<span class="hint" data-desc="<@spring.message 'supportingDocuments.personalStatement'/>"></span>	 
	        		<a href="<@spring.url '/download?documentId=${(model.applicationForm.personalStatement.id?string("#######"))!}'/>">${(model.applicationForm.personalStatement.fileName)!}</a></span>						
	        	</div>  
        		
      		
      		
	      		 <div class="row">
	        		<span class="plain-label">CV / resume (PDF)<em>*</em></span>
	        		<span class="hint" data-desc="<@spring.message 'supportingDocuments.cv'/>"></span>   	       	
	          		
					<a href="<@spring.url '/download?documentId=${(model.applicationForm.cv.id?string("#######"))!}'/>">${(model.applicationForm.cv.fileName)!}</a></span>		
	        		
	      		</div>
      		</div>
			<div class="buttons">
                <button class="blue"  id="documentsCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>