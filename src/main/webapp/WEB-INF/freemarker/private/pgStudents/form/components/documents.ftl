<#import "/spring.ftl" as spring />
	<h2 class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Documents
	</h2>
    
    <div>
    	
    	<table class="existing">
        	<colgroup>
            	<col style="width: 30px" />
                <col style="width: 150px" />
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
                <tbody>
					<tr/>
                </tbody>
		</table>
              
		<form id="documentUploadForm" method="POST" action="<@spring.url '/documents'/>" enctype="multipart/form-data">
             <input type="hidden" name="id" value="${model.applicationForm.id}"/>
             <div>
                
             	<!-- Document type -->
                <div class="row">
                    <span class="label">Type</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<select class="full" name="documentType">
                    		<#list model.documentTypes as documentType>
                    			<option value="${documentType}">${documentType.displayValue}</option>
                    		</#list>	              
                      	</select>
                    </div>  
				</div>

                <!-- Document upload -->
                <div class="row">
                    <span class="label">Document</span>
                    <span class="hint"></span>
                    <div class="field">
                		<input class="full" type="file" name="file" value="" />                      	
                        <button class="blue" type="submit" value="close">Upload</button>          
                    </div>  
				</div>
				
			</div>

			<div class="buttons">
            	<a class="button" href="#">Cancel</a>
                <button class="blue" value="close">Close</button>              
			</div>

		</form>
	</div>
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>