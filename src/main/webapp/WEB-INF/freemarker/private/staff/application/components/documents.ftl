	<h2 id="documents-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Documents
	</h2>
    
    <div>
              
		<form>
            <div class="sub_section_amdin">
				<div class="admin_row">
	        		<span class="admin_row_label">Personal Statement</span>	        	
	        		<a href="<@spring.url '/download?documentId=${(applicationForm.personalStatement.id?string("#######"))!}'/>">
	        						${(applicationForm.personalStatement.fileName)!}</a></span>						
	        	</div>  
      		
	      		 <div class="admin_row">
	        		<span class="admin_row_label">CV / resume</span>       	       	
	          		
					<a href="<@spring.url '/download?documentId=${(applicationForm.cv.id?string("#######"))!}'/>">${(applicationForm.cv.fileName)!}</a></span>		
	        		
	      		</div>
      		</div>
      		
			<div class="buttons">
                <button class="blue"  id="documentsCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>