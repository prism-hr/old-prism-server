<#import "/spring.ftl" as spring />
<h2 id="additional-H2" class="no-arrow empty">Additional Information</h2>

<div>
  <form>
  
    <div class="row-group">
    
      <!-- Free text field for info. -->
      <div class="admin_row">
        <span class="admin_row_label">Additional Information</span>
				<div class="field"><#if applicationForm.additionalInformation?? && (applicationForm.additionalInformation.informationText)?has_content>${(applicationForm.additionalInformation.informationText?html)}<#else>Not Provided</#if></div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Prior convictions</label>
        <div class="field"><#if applicationForm.additionalInformation.convictions>Yes<#else>No</#if></div>
      </div>
      
      <#if applicationForm.additionalInformation.convictions?? && applicationForm.additionalInformation.convictions >
      <!-- Free text field for convictions. -->
      <div class="admin_row">
        <span class="admin_row_label">Details of the convictions</span>
				<div class="field"><#if applicationForm.additionalInformation?? && (applicationForm.additionalInformation.convictionsText)?has_content>${(applicationForm.additionalInformation.convictionsText?html)}<#else>Not Provided</#if></div>
      </div>
      </#if>
      
    </div>    
  </form>
</div>
