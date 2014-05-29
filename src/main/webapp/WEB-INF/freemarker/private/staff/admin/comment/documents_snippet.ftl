<div class="row">
<label class="plain-label" for="commentDocument">Attach Document (PDF)</label>
<span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
<div class="field" id="uploadFields">
  <div class="fileupload fileupload-new" data-provides="fileupload">
    <div class="input-append">
      <div class="uneditable-input span4"> <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
      <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
      <input id="commentDocument" class="full" data-type="COMMENT" data-reference="Comment" type="file" name="file" value="" />
      <input type="hidden" class="file" id="document_COMMENT" value=""/>
      </span> </div>
  </div>
  <input type="hidden" name="MAX_FILE_SIZE" value="2097152" />
<div id="commentDocumentProgress" class="progress" style="display: none;"></span> </div>
        <ul id="commentUploadedDocument"  class="uploaded-files" style="display:none;">
        <#if comment?has_content &&
        	comment.documents?has_content>
          <#list comment.documents as document>
            <li class="done"> 
            <span class="uploaded-file" name="supportingDocumentSpan">
            <input class="file" style="display:none" type="text" value="${encrypter.encrypt(document.id)}" name="documents"/><a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">
                ${document.fileName?html}
                </a> <a class="btn btn-danger delete" href="#" id="${encrypter.encrypt(document.id)}" data-desc="Delete" name="delete"> <i class="icon-trash icon-large"></i> Delete</a> 
                </span></li>
            </#list>
            </#if>
    
        </ul>
  </div>
  
</div>

