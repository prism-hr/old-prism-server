<#import "/spring.ftl" as spring />
	<h2 id="documents-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Documents
	</h2>
    
    <div>
    	
    	<table class="existing">
        	<colgroup>
            	<col style="width: 20px" />
                <col style="width: 300px" />
                <col />
                <col style="width: 120px" />
                <col style="width: 30px" />
			</colgroup>
            
            <thead>
            	<tr>
	                <th colspan="2">Document Type</th>
	                <th>Document Name</th>
	                <th>Date Uploaded</th>
	                <th>&nbsp;</th>
                </tr>
                
                </thead>
		</table>
        ${model.message!}
		<form id="documentUploadForm" method="POST" action="<@spring.url '/addReferences/submit'/>" enctype="multipart/form-data">
             <input type="hidden" name="id" value="${model.referee.id}"/>
             <div>
              <textarea id="comment" name="comment" class="max" rows="5" cols="90" ></textarea>
            
                <!-- Document upload -->
                		<input class="full" type="file" name="file" value="" />                      	
				<#if model.uploadErrorCode?? >
					   <span class="invalid"><@spring.message  model.uploadErrorCode /></span>
				</#if>				

			<div class="buttons">
				<button type="reset" value="cancel">Cancel</button>
                <button class="blue" type="submit" id="referenceSaveButton" value="close">Submit</button>              
			</div>

		</form>
	</div>
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>
	
<#if model.result?? && model.result.hasErrors()  >

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#documents-H2').trigger('click');
	});
</script>
</#if>
</html>