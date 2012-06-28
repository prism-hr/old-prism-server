<h2 id="documents-H2" class="no-arrow empty">Documents</h2>

<div>
  <form>
    <div class="row-group">
  
      <div class="admin_row">
        <span class="admin_row_label">Personal Statement</span>
        <div class="field">            
          <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(applicationForm.personalStatement.id))!}'/>" target="_blank">
            ${(applicationForm.personalStatement.fileName)!}
          </a>
        </div>            
      </div>  
        
      <div class="admin_row">
        <span class="admin_row_label">CV / resume</span>                  
        <div class="field">
				<#if applicationForm.cv??>
          <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(applicationForm.cv.id))!}'/>" target="_blank">
            ${(applicationForm.cv.fileName)!}
          </a>
				<#else>
					Not Provided
				</#if>
        </div>
      </div>
      
    </div>
  
  </form>
</div>
