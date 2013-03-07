<div class="row-group">
    <div class="admin_row">
        <label class="admin_header">Reference Comment</label>
        <div class="field">&nbsp</div>
    </div>
    
    <div class="admin_row">
        <span class="admin_row_label">Comment</span>
        <div class="field">${(referee.reference.comment?html)!"Not Provided"}</div>
    </div>
    
    <div class="admin_row">
        <span class="admin_row_label">Is the applicant suitable for postgraduate study at UCL?</span>
        <div class="field">
            <#if referee.reference.suitableForUCL>Yes<#else>No</#if>
        </div>
    </div>
    
    <div class="admin_row">
        <span class="admin_row_label">Is the applicant suitable for their chosen postgraduate study programme?</span>
        <div class="field">
            <#if referee.reference.suitableForProgramme>Yes<#else>No</#if>
        </div>
    </div>
    
    <div class="admin_row">
        <span class="admin_row_label">Attached Document</span>
        <div class="field">
            <#if referee.reference.documents?has_content>
                <a href="<@spring.url '/download?documentId=${encrypter.encrypt(referee.reference.documents[0].id)}'/>" class="button-hint" target="_blank">${referee.reference.documents[0].fileName?html}</a>
            <#else>
                Not Provided
            </#if>
        </div>
    </div>
</div>
