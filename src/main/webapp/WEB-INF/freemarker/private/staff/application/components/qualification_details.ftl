<#if applicationForm.qualifications?has_content>
<#assign hasQualifications = true>
<#else>
<#assign hasQualifications = false>
</#if> 

<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<h2 id="qualifications-H2" class="no-arrow empty">Qualifications</h2>

<div>
  <form>
    <#if hasQualifications>
    
    <#list applicationForm.qualifications as qualification>
    
    <!-- Rendering part - Start -->
    
    <div class="row-group">
    
      <!-- Header -->
      <div class="admin_row">
        <label class="admin_header">Qualification (${qualification_index + 1})</label>
        <div class="field">&nbsp</div>
      </div>
    
      <!-- Provider -->
      <div class="admin_row">
        <span class="admin_row_label">Institution Country</span>
        <div class="field" id="qualificationInstitutionCountry">${(qualification.institutionCountry.name?html)!"Not Provided"}</div>
      </div>
    
      <div class="admin_row">
        <span class="admin_row_label">Institution / Provider Name</span>
        <div class="field" id="qualificationInstitution">${(qualification.qualificationInstitution?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Other Institution / Provider Name</span>
        <div class="field" id="qualificationInstitution">${(qualification.otherQualificationInstitution?html)!"Not Provided"}</div>
      </div>
      
      <!-- Type -->
      <div class="admin_row">
        <span class="admin_row_label">QualificationType</span>
        <div class="field" id="qualificationType">${(qualification.qualificationType.name?html)!"Not Provided"}</div>
      </div>
      
      <!-- Title -->
      <div class="admin_row">
        <span class="admin_row_label">Qualification Title</span>
        <div class="field" id="qualificationSubject">${(qualification.qualificationTitle?html)!"Not Provided"}</div>
      </div>
      
      <!-- Subject -->
      <div class="admin_row">
        <span class="admin_row_label">Qualification Subject</span>
        <div class="field" id="qualificationSubject">${(qualification.qualificationSubject?html)!"Not Provided"}</div>
      </div>
      
      <!-- Language (in which programme was undertaken) -->
      <div class="admin_row">
        <span class="admin_row_label">Language of Study</span>
        <div class="field" id="qualificationLanguage">${(qualification.qualificationLanguage?html)!"Not Provided"}</div>
      </div>
      
      <!-- Start date -->
      <div class="admin_row">
        <span class="admin_row_label">Start Date</span>
        <div class="field" id="qualificationStartDate">${(qualification.qualificationStartDate?string('dd MMM yyyy'))!"Not Provided"}</div>
      </div>
      
      <!-- Has qualification been awarded? -->
      <div class="admin_row">
        <span class="admin_row_label">Has this qualification been awarded?</span>                    
        <div class="field" id="qualificationCompleted">${(qualification.completed?capitalize)!"Not Provided"}</div>
      </div>
      
      <!-- Qualification grade -->
      <div class="admin_row">
        <span class="admin_row_label"><#if qualification.isQualificationCompleted()>Grade / Result /GPA<#else>Expected Grade / Result / GPA</#if></span>
        <div class="field" id="qualificationGrade">${(qualification.qualificationGrade?html)!"Not Provided"}</div>
      </div>
      
      <!-- Award date -->
      <div class="admin_row">
        <span class="admin_row_label"><#if qualification.isQualificationCompleted()>Award Date<#else>Expected Award Date</#if></span>
        <div class="field" id="qualificationAwardDate">${(qualification.qualificationAwardDate?string('dd MMM yyyy'))!"Not Provided"}</div>
      </div>
      
      <!-- Attachment / supporting document  -->
      <div class="admin_row">
      	<span class="admin_row_label"><#if qualification.isQualificationCompleted()>Proof of award (PDF)<#else>Interim Transcript (PDF)</#if></span>
        <div class="field" id="referenceDocument">
            <#if qualification.proofOfAward??>
                <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(qualification.proofOfAward.id))!}'/>" target="_blank">${(qualification.proofOfAward.fileName)!}</a>
            <#else>
                Not Provided
            </#if>
        </div>
         
      </div>
      
    </div>                             
    </#list>
    <#else>
      
    <div class="row-group">
    
      <!-- Provider -->
      <div class="row">
        <span class="admin_header">Qualification</span>
        <div class="field">Not Provided</div>
      </div>
    
    </div>                             
    </#if>
  </form>
</div>
