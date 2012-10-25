<#-- Assignments -->

<#if view??>
  <#assign viewType = view>
<#else>
  <#assign viewType = 'open'>
</#if>
<#assign prevComments = "false">

<#if applicationForm.hasComments()>
  <#assign comCount = applicationForm.applicationComments?size>
<#else> 
  <#assign comCount = 0>
</#if>
<#setting locale = "en_US">
<#-- Personal Details Rendering -->

<!-- Personal details -->
<h2 id="personalDetails-H2" class="no-arrow tick">Personal Details</h2>

<div id="personal-details-section" class="open">
  <form method="post" method = "GET">
    <input type ="hidden" id="view-type-personal-form" value="${viewType}"/>
    <input type="hidden" name="id" value="${applicationForm.applicationNumber}"/>
    <input type="hidden" id="form-display-state" value="${formDisplayState!}"/>
    
    <!-- Basic Details -->
    <div class="row-group">
    
      <div class="admin_row">
        <label class="admin_row_label">Title</label>
        <div class="field">${(applicationForm.personalDetails.title?capitalize)!"Not Provided"}</div>
      </div>
    
      <div class="admin_row">
        <label class="admin_row_label">First Name</label>
        <div class="field">${(applicationForm.personalDetails.firstName?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Last Name</label>
        <div class="field">${(applicationForm.personalDetails.lastName?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Gender</label>
        <div class="field">${(applicationForm.personalDetails.gender?capitalize)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Date of Birth</label>
        <div class="field">${(applicationForm.personalDetails.dateOfBirth?string('dd MMM yyyy'))!"Not Provided"}</div>
      </div>
      
      <!-- Country -->
      <div class="admin_row">
        <span class="admin_row_label">Country of Birth</span>
        <div class="field">${(applicationForm.personalDetails.country.name?html)!"Not Provided"}</div>
      </div>
      
      <!-- Nationality -->
      <div class="admin_row">
        <label class="admin_row_label">Nationality</label>
        <div class="field">
          <#assign size_cn = applicationForm.personalDetails.candidateNationalities?size>
          <#if ( size_cn > 0)>
          <#list applicationForm.personalDetails.candidateNationalities as nationality>
          <#assign index_i = nationality_index>
          ${nationality.name!"Not Provided"}<#if (index_i < (size_cn - 1))>,</#if>
          </#list>
          <#else>
          Not Provided
          </#if>
        </div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Is English your first language?</span>
        <div class="field"><#if applicationForm.personalDetails.englishFirstLanguage>Yes<#else>No</#if></div>
      </div>                              
      
      <!-- Nationality -->
      <div class="admin_row">
        <span class="admin_row_label">Country of Residence</span>
        <div class="field">${(applicationForm.personalDetails.residenceCountry.name?html)!"Not Provided"}</div>
      </div>
      
      <!-- VISA - Passport -->
      <div class="admin_row">
        <span class="admin_row_label">Do you require a visa to study in the UK?</span>
        <div class="field"><#if applicationForm.personalDetails.requiresVisa>Yes<#else>No</#if></div>
      </div> 
      
      <div class="admin_row">
        <span class="admin_row_label">Passport Number</span>
        <div class="field">${(applicationForm.personalDetails.passportNumber?html)!"Not Provided"}</div>                     
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Name on Passport</span>
        <div class="field">${(applicationForm.personalDetails.nameOnPassport?html)!"Not Provided"}</div>                     
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Passport Issue Date</span>
        <div class="field">${(applicationForm.personalDetails.passportIssueDate?string('dd MMM yyyy'))!"Not Provided"}</div>                     
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Passport Expiry Date</span>
        <div class="field">${(applicationForm.personalDetails.passportExpiryDate?string('dd MMM yyyy'))!"Not Provided"}</div>                     
      </div>
      
      <!-- Contact Details -->
      <div class="admin_row">
        <span class="admin_row_label">Email</span>
        <div class="field">${(applicationForm.personalDetails.email?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Telephone</span>
        <div class="field">${(applicationForm.personalDetails.phoneNumber?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Skype Name</span>
				<div class="field"><#if (applicationForm.personalDetails.messenger)?has_content>${(applicationForm.personalDetails.messenger?html)}<#else>Not Provided</#if></div>
      </div>
      
<#--
      <div class="admin_row">
        <label class="admin_row_label">Ethnicity</label>
        <div class="field">${(applicationForm.personalDetails.ethnicity.name?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Disability</label>
        <div class="field">${(applicationForm.personalDetails.disability.name?html)!"Not Provided"}</div>
      </div>
-->      
    </div>            
  </form>
  
</div>
