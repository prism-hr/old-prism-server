<#import "/spring.ftl" as spring />
<span name="supportingDocumentSpan">
	<#if document?? && document.id??>
		<input type="text" id="document_${document.type}" value = "${encrypter.encrypt(document.id)}" style="display:none" name="document"/>
		<#if document.type != 'REFERENCE'>
			<a class="docName" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>"
				data-oldlink="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>">${document.fileName?html}</a>
			<a data-desc="" id="" class="button-edit button-hint">edit</a>
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