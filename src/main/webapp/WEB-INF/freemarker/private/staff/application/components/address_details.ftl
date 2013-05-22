<#if applicationForm.addresses?has_content>
  <#assign hasAddresses = true>
<#else>
  <#assign hasAddresses = false>
</#if> 

<#import "/spring.ftl" as spring />
<#setting locale = "en_US">

<h2 id="address-H2" class="no-arrow empty">Address</h2>

<div>
  <form>
  
    <input type="hidden" id="addressId" name="addressId"/>
    
    <div class="row-group">
    
      <!-- Address body -->
      <div class="admin_row">
        <span class="admin_row_label">Current Address</span>
        <div class="field">${(applicationForm.currentAddress.locationString?html)!}</div>
      </div>
      
      <!-- Country -->
      <div class="admin_row">
        <span class="admin_row_label">Country</span>
        <div class="field">${(applicationForm.currentAddress.country.name?html)!}</div>
      </div>
      
      </div>
      
      <div class="row-group">
      
      <div class="admin_row">
        <span class="admin_row_label">Contact Address</span>    
        <div class="field">${(applicationForm.contactAddress.locationString?html)!}</div>
      </div>
      
      <!-- Country -->
      <div class="admin_row">
        <span class="admin_row_label">Country</span>
        <div class="field">${(applicationForm.contactAddress.country.name?html)!}</div>
      </div>
    
    </div>
    
  </form>
</div>
