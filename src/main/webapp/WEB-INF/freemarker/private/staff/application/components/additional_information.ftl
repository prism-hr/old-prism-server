<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<h2 id="additional-H2" class="no-arrow empty">Additional Information</h2>

<div>
  <form>
  
    <div class="row-group">
    
      <div class="admin_row">
        <label class="admin_row_label">Do you have any unspent Criminial Convictions?</label>
        <div class="field">${(applicationForm.additionalInformation.convictions?html)!"Not Provided"}</div>
      </div>
      
      <#if user.isInRole('SUPERADMINISTRATOR')>
          <#if applicationForm.additionalInformation.convictions?? && applicationForm.additionalInformation.convictions >
          <!-- Free text field for convictions. -->
          <div class="admin_row">
            <span class="admin_row_label">Description</span>
    				<div class="field"><#if applicationForm.additionalInformation?? && (applicationForm.additionalInformation.convictionsText)?has_content>${(applicationForm.additionalInformation.convictionsText?html)}<#else>Not Provided</#if></div>
          </div>
          </#if>
      </#if>
    </div>    
  </form>
</div>
