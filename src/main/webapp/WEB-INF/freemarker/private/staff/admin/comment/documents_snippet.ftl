<div class="row">
	<label class="plain-label" for="file">Attach Document</label>
	<span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
	<div class="field uploaded" id="uploadFields">
		<span id="commentUploadedDocument">
			<#if comment??>
				<#list comment.documents as document>
				<span class="uploaded-file" name="supportingDocumentSpan">
					<input type="text" value = "${encrypter.encrypt(document.id)}" style="display:none" name="documents"/>	
					<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
					<a name="delete" data-desc="Delete" class="button-delete button-hint" id="${encrypter.encrypt(document.id)}">delete</a>
				</span>
				</#list>
			</#if>
		</span>
		<input id="commentDocument" class="full" type="file" name="file" value="" style="display:block" />
		<span id="commentDocumentProgress" style="display: none;" ></span>
	</div>
</div>