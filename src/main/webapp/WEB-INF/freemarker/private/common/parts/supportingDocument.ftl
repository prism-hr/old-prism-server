<#import "/spring.ftl" as spring />
<span name="supportingDocumentSpan">
	<#if document?? && document.id??>
		<input type="text" id="document_${document.type}" value = "${document.id?string("#######")}" style="display:none" name="document"/>
		<#if document.type != 'REFERENCE'>
			<a style="display:none;" href="<@spring.url '/download?documentId=${document.id?string("#######")}'/>">${document.fileName?html}</a>
		<#else>
			${document.fileName?html}
		</#if>		
	</#if>
	<#if message??>
		${message?html}
	</#if>
	 <@spring.bind "document.*" /> 
     <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list> 	 
</span>