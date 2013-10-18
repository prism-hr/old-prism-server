<#if applicationForm.referees?has_content>
  <#assign hasReferees = true>
<#else>
  <#assign hasReferees = false>
</#if> 

<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<h2 id="referee-H2" class="no-arrow empty">References</h2>

<div class="open">
  <form>        
    <#if hasReferees>
    <#list applicationForm.referees as referee>
    
    <!-- All hidden input - Start -->
    <#assign encRefereeId = encrypter.encrypt(referee.id) />
    
    <input type="hidden" id="${encRefereeId}_refereeId" value="${encRefereeId}"/>
    <input type="hidden" id="${encRefereeId}_firstname" value="${(referee.firstname?html)!}"/>
    <input type="hidden" id="${encRefereeId}_phone" value="${(referee.phoneNumber?html)!}"/>
    <#if referee.messenger??>
    <input type="hidden" id="${encRefereeId}_messenger" value="${(referee.messenger?html)!}"/>
    <#else>
    <input type="hidden" id="${encRefereeId}_messenger" value=" "/>
    </#if>
    <input type="hidden" id="${encRefereeId}_lastname" value="${(referee.lastname?html)!}"/>                                    
    <input type="hidden" id="${encRefereeId}_jobEmployer" value="${(referee.jobEmployer?html)!}"/>
    <input type="hidden" id="${encRefereeId}_jobTitle" value="${(referee.jobTitle?html)!}"/>
    <input type="hidden" id="${encRefereeId}_addressLocation" value="${(referee.addressLocation.locationString?html)!}"/>
    
    <input type="hidden" id="${encRefereeId}_addressCountry" <#if referee.addressLocation.domicile??> value="${(referee.addressLocation.domicile.name?html)!}" </#if>/>
    <input type="hidden" id="${encRefereeId}_lastUpdated" value="<#if referee.hasProvidedReference() > 
    Provided ${(referee.reference.lastUpdated?string('dd MMM yyyy'))!}
    <#else>
    Not provided
    </#if>"/>
    
    <input type="hidden" id="${encRefereeId}_reference_document_url" value="<#if referee.hasProvidedReference() && referee.reference.document?? >
    <@spring.url '/download/reference?referenceId=${encrypter.encrypt(referee.reference.id)}'/></#if>"
    />
    <input type="hidden" id="${encRefereeId}_reference_document_name" value="<#if referee.hasProvidedReference()><#if referee.reference.document??>${referee.reference.document.fileName?html}</#if><#else>No document uploaded</#if>" />
    <input type="hidden" id="${encRefereeId}_email" value="${(referee.email?html)!}"/>
    
    <!-- All hidden input - End --> 
    
    <!-- Rendering part - Start -->
    
    <div class="row-group">
    
      <!-- Header -->
      <div class="admin_row">
        <label class="admin_header">Reference (${referee_index + 1})<#if referee.declined> - Declined</#if></label>
        <div class="field">&nbsp</div>
      </div>
    
      <!-- First name -->
      <div class="admin_row">
        <span class="admin_row_label">First Name</span>
        <div class="field" id="ref_firstname">${(referee.firstname?html)!"Not Provided"}</div>
      </div>
    
      <!-- Last name -->
      <div class="admin_row">
        <span class="admin_row_label">Last Name</span>
        <div class="field" id="ref_lastname">${(referee.lastname?html)!"Not Provided"}</div>
      </div>
    
      <!-- Employer / company name -->
      <div class="admin_row">
        <span class="admin_row_label">Employer</span>
        <div class="field" id="ref_employer">${(referee.jobEmployer?html)!"Not Provided"}</div>
      </div>
    
      <!-- Position title -->
      <div class="admin_row">
        <span class="admin_row_label">Position</span>
        <div class="field" id="ref_position">${(referee.jobTitle?html)!"Not Provided"}</div>
      </div>
    
      <!-- Address body -->
      <div class="admin_row">
        <span class="admin_row_label">Address</span>
        <div class="field" id="ref_address_location">${(referee.addressLocation.locationString?html)!"Not Provided"}</div>
      </div>
    
      <!-- Country -->
      <div class="admin_row">
        <span class="admin_row_label">Country</span>
        <div class="field" id="ref_address_country">${(referee.addressLocation.domicile.name?html)!"Not Provided"}</div>
      </div>
    
      <!-- Email address -->
      <div class="admin_row">
        <span class="admin_row_label">Email</span>
        <div class="field" id="ref_email">${(referee.email?html)!"Not Provided"}</div>
      </div>
    
      <!-- Telephone -->
      <div class="admin_row">
        <span class="admin_row_label">Telephone</span>
        <div class="field"  id="ref_phone">${(referee.phoneNumber?html)!"Not Provided"}</div>
      </div>
    
      <!-- Skype address -->
      <div class="admin_row">
        <span class="admin_row_label">Skype</span>
        <div class="field" id="ref_messenger">${(referee.messenger?html)!"Not Provided"}</div>
      </div>

    </div>
    </#list>
    </#if>
  </form>           
</div>