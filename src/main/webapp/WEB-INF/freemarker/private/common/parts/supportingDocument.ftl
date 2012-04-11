<#import "/spring.ftl" as spring />
<span name="supportingDocumentSpan">
	<#if document?? && document.id??>
		<input type="hidden" id="document_${document.type}" value = "${document.id?string("#######")}"/>
		<a href="<@spring.url '/download?documentId=${document.id?string("#######")}'/>">${document.fileName}</a>		
	</#if>
	<#if message??>
		${message?html}
	</#if>
	 <@spring.bind "document.*" /> 
     <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list> 	 
</span>