	<h2 id="documents-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Documents
	</h2>
    
    <div>
              
		<form>
            <div class="row-group">
				<div class="admin_row">
	        		<span class="admin_row_label">Personal Statement</span>
	        		<div class="field">	        	
	        			<a href="<@spring.url '/download?documentId=${(applicationForm.personalStatement.id?string("#######"))!}'/>">
	        						${(applicationForm.personalStatement.fileName)!}</a></span>
	        		</div>						
	        	</div>  
      		
	      		 <div class="admin_row">
	        		<span class="admin_row_label">CV / resume</span>       	       	
	          		<div class="field">
						<a href="<@spring.url '/download?documentId=${(applicationForm.cv.id?string("#######"))!}'/>">
									${(applicationForm.cv.fileName)!}</a></span>
					</div>
	      		</div>
      		</div>
      		
			<div class="buttons">
                <button class="blue"  id="documentsCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>