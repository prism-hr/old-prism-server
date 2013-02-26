<#if applicationForm.fundings?has_content>
  <#assign hasFundings = true>
<#else>
  <#assign hasFundings = false>
</#if>

<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<h2 id="funding-H2" class="no-arrow empty">Funding</h2>

<div>
  <form>
  
    <#if hasFundings>
    <#list applicationForm.fundings as funding>    
    
    <!-- Rendering part - Start -->
    <div class="row-group">
    
      <!-- Header -->
      <div class="admin_row">
        <label class="admin_header">Funding (${funding_index + 1})</label>
        <div class="field">&nbsp</div>
      </div>
      
      <!-- Award type -->
      <div class="admin_row">
        <span class="admin_row_label">Funding Type</span>             
        <div class="field" id="fundingType">${(funding.type?capitalize)!"Not Provided"}</div>
      </div>
      
      <!-- Award description -->
      <div class="admin_row">
        <span class="admin_row_label" >Description</span>
        <div class="field" id="fundingDescription">${(funding.description?html)!"Not Provided"}</div>
      </div>
      
      <!-- Value of award -->
      <div class="admin_row">
        <span class="admin_row_label">Value of Award</span>
        <div class="field"  id="fundingValue">${(funding.value?html)!"Not Provided"}</div>
      </div>
      
      <!-- Award date -->
      <div class="admin_row">
        <span class="admin_row_label">Award Date</span>
        <div class="field" id="fundingAwardDate">${funding.awardDate?string('dd MMM yyyy')!"Not Provided"}</div>
      </div>
      
      <!-- Award date -->
      <div class="admin_row">
        <span class="admin_row_label">Proof of award</span>
        <#if funding.document?has_content>
        <div class="field" id="proofOfAward">
          <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(funding.document.id))!}'/>" target="_blank">
            ${(funding.document.fileName?html)!}
          </a>
        </div>
        <#else> 
        <div class="field" id="referenceDocument">Not Provided</div> 
        </#if>
      </div>
      
    </div>
    </#list>
      
    <#else>
      
    <!-- Rendering part - Start -->
    <div class="row-group">
    
      <!-- Award type -->
      <div class="row">
        <span class="admin_header">Funding</span>             
        <div class="field">Not Provided</div>
      </div>
      
    </div>
    
    </#if>
    
  </form>
</div>
