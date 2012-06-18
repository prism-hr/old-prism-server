<h2 id="documents-H2" class="empty">
  <span class="left"></span><span class="right"></span><span class="status"></span>
  Documents
</h2>

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
          <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(applicationForm.cv.id))!}'/>" target="_blank">
            ${(applicationForm.cv.fileName)!}
          </a>
        </div>
      </div>
      
    </div>
  
  </form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>