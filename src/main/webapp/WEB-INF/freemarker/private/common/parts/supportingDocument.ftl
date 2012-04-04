<#import "/spring.ftl" as spring />
<span name="supportingDocumentSpan">
	<#if document??>
		<input type="hidden" id="profOfAwardId" value = "${document.id?string("#######")}"/>
		<a href="<@spring.url '/filemanagement/view/${document.id?string("#######")}'/>">${document.fileName}</a>
		<a id="deleteProofOfAwardButton" class="button-delete">delete</a>
	</#if>
	<#if message??>
		${message?html}
	</#if> 	 
</span>