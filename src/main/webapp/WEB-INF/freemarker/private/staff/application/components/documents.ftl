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
					<#list model.applicationForm.supportingDocuments as document>
					<tr>
						<td>-</td>
		                <td  nowrap>${document.type.displayValue}</td>
		                <td ><a href="<@spring.url '/download'/>?documentId=${document.id}">
		                	<#if document.fileName?length <40 >${document.fileName}<#else>${document.fileName?substring(0,37)}...</#if></a></td>
		                <td>${(document.dateUploaded?string('dd-MMM-yyyy'))!}</td>
		                <td>
		                	<#if !model.applicationForm.submitted>
			                	<form method="Post" action="<@spring.url '/delete'/>" style="padding:0">
			                		<input type="hidden" name="documentId" value="${document.id}"/>		                		
			                		<a name="deleteButton" class="button-delete">delete</a>
			                	</form>
		                	</#if>
		               </td>
	                </tr>
					</#list>
                </tbody>
		</table>
              
		<form>
             

			<div class="buttons">
                <button class="blue" type="button">Close</button>
			</div>

		</form>
	</div>