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

<#-- Personal Details Rendering -->

<!-- Personal details -->
<h2 id="personalDetails-H2" class="tick">
  <span class="left"></span><span class="right"></span><span class="status"></span>
  Personal Details
</h2>

<div id="personal-details-section" class="open">
  <form method="post" method = "GET">
    <input type ="hidden" id="view-type-personal-form" value="${viewType}"/>
    <input type="hidden" name="id" value="${applicationForm.applicationNumber}"/>
    <input type="hidden" id="form-display-state" value="${formDisplayState!}"/>
    
    <!-- Basic Details -->
    <div class="row-group">
    
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
        <div class="field">${(applicationForm.personalDetails.gender?html)!"Not Provided"}</div>
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
      
      <!-- My Nationality -->
      <div class="admin_row">
        <label class="admin_row_label">My Nationality</label>
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
      
      <!-- Maternal guardian nationality -->
      <div class="admin_row">
        <label class="admin_row_label">Maternal Guardian Nationality</label>
        <div class="field">
          <#assign size_mn = applicationForm.personalDetails.maternalGuardianNationalities?size>
          <#if ( size_mn > 0)>
          <#list applicationForm.personalDetails.maternalGuardianNationalities as nationality >
          <#assign index_i = nationality_index>
          ${nationality.name!"Not Provided"}<#if (index_i < (size_mn - 1))>,</#if> 
          </#list>
          <#else>
          Not Provided
          </#if>
        </div>
      </div>
      
      <!-- Paternal guardian nationality -->
      <div class="admin_row">
        <label class="admin_row_label">Paternal Guardian Nationality</label>
        <div class="field">
          <#assign size_cp = applicationForm.personalDetails.paternalGuardianNationalities?size>
          <#if ( size_cp > 0)>
          <#list applicationForm.personalDetails.paternalGuardianNationalities as nationality >
          <#assign index_i = nationality_index>
          ${nationality.name!"Not Provided"}<#if (index_i < (size_cp - 1))>,</#if>
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
      
      <div class="admin_row">
        <span class="admin_row_label">Do you require a visa to study in the UK?</span>
        <div class="field"><#if applicationForm.personalDetails.requiresVisa>Yes<#else>No</#if></div>
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
        <div class="field">${(applicationForm.personalDetails.messenger?html)!"Not Provided"}</div>
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

<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>