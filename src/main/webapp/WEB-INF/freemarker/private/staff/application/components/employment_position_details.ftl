<#if applicationForm.employmentPositions?has_content>
  <#assign hasEmploymentPositions = true>
<#else>
  <#assign hasEmploymentPositions = false>
</#if>

<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<h2 id="position-H2" class="no-arrow empty">Employment</h2>

<div>
  <form>
    <#if hasEmploymentPositions>
    <#list applicationForm.employmentPositions as position>

    <!-- Rendering part - Start -->
    <div class="row-group">
    
      <!-- Header -->
      <div class="admin_row">
        <label class="admin_header">Position (${position_index + 1})</label>
        <div class="field">&nbsp</div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Country</span>                 
        <div class="field" id="emp_country">${(position.employerAddress.country.name?html)!"Not Provided"}</div>
      </div>
      
      <!-- Employer (company name) -->
      <div class="admin_row">
        <span class="admin_row_label">Employer Name</span>                 
        <div class="field" id="emp_name">${(position.employerName?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Employer Address</span>                 
        <div class="field" id="emp_address">${(position.employerAddress.locationString?html)!"Not Provided"}</div>
      </div>
      
      <!-- Position -->
      <div class="admin_row">
        <span class="admin_row_label">Position</span>
        <div class="field" id="emp_position">${(position.position)!"Not Provided"}</div>
      </div>
      
      <!-- Position -->
      <div class="admin_row">
        <span class="admin_row_label">Roles and Responsibilities</span>
        <div class="field" id="emp_description">${(position.remit?html)!"Not Provided"}</div>
      </div>
           
      <!-- Start date -->
      <div class="admin_row">
        <span class="admin_row_label">Start Date</span>
        <div class="field" id="emp_startDate">${(position.startDate?string('dd MMM yyyy'))!"Not Provided"}</div>
      </div>

      <!-- Current position? -->
      <div class="admin_row">
        <span class="admin_row_label">Is this your current position?</span>
        <div class="field" id="emp_current"><#if position.current>Yes<#else>No</#if> </div>
      </div>
      
      <!-- End date -->
      <div class="admin_row">
        <span class="admin_row_label">End date</span>
        <div class="field" id="emp_endDate">${(position.endDate?string('dd MMM yyyy'))!"Not Provided"}</div>
      </div>
      
    </div>
    </#list>
      
    <#else>
      
    <!-- Rendering part - Start -->
    <div class="row-group">
      
      <div class="row">
        <span class="admin_header">Employment</span>                 
        <div class="field">Not Provided</div>
      </div>
      
    </div>
    </#if>
    
  </form>
</div>
