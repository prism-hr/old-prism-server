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
  <form>
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
        <div class="field">${(applicationForm.applicant.firstName?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">First Name 2</label>
        <div class="field">${(applicationForm.applicant.firstName2?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">First Name 3</label>
        <div class="field">${(applicationForm.applicant.firstName3?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <label class="admin_row_label">Last Name</label>
        <div class="field">${(applicationForm.applicant.lastName?html)!"Not Provided"}</div>
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
            <#if applicationForm.personalDetails?? && applicationForm.personalDetails.firstNationality??>${applicationForm.personalDetails.firstNationality.name}</#if>
            <#if applicationForm.personalDetails?? && applicationForm.personalDetails.secondNationality??>, ${applicationForm.personalDetails.secondNationality.name}</#if>          
        </div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Is English your first language?</span>
        <div class="field">${(applicationForm.personalDetails.englishFirstLanguage?string("Yes", "No"))!"Not Provided"}</div>
      </div>                              
      
      <div class="admin_row">
        <span class="admin_row_label">Do you have an English language qualification?</span>
        <div class="field">
          ${(applicationForm.personalDetails.languageQualificationAvailable?string("Yes", "No"))!"Not Provided"}
        </div>
      </div>                              
    </div>
      
      <#if applicationForm.personalDetails?? && applicationForm.personalDetails.languageQualificationAvailable?? && applicationForm.personalDetails.languageQualificationAvailable>
      <#assign languageQualification = applicationForm.personalDetails.languageQualification>
      <div class="row-group">
          <div class="admin_row">
            <label class="admin_header">Language Qualifications</label>
            <div class="field">&nbsp</div>
          </div>

          <div class="admin_row">
            <span class="admin_row_label">Qualification Type</span>
            <div class="field">${(languageQualification.qualificationType.displayValue?html)!"Not Provided"}</div>
          </div>
          
          <div class="admin_row">
            <span class="admin_row_label">Other Qualification Type Name</span>
            <div class="field">${(languageQualification.otherQualificationTypeName?html)!"Not Provided"}</div>
          </div>
          
          <div class="admin_row">
            <span class="admin_row_label">Date of Examination</span>
            <div class="field">${(languageQualification.dateOfExamination?string('dd MMM yyyy'))!"Not Provided"}</div>
          </div>

          <div class="admin_row">
            <span class="admin_row_label">Overall Score</span>
            <div class="field">${(languageQualification.overallScore?html)!"Not Provided"}</div>
          </div>
          
          <div class="admin_row">
            <span class="admin_row_label">Reading Score</span>
            <div class="field">${(languageQualification.readingScore?html)!"Not Provided"}</div>
          </div>
          
          <div class="admin_row">
            <span class="admin_row_label">Essay / Writing Score</span>
            <div class="field">${(languageQualification.writingScore?html)!"Not Provided"}</div>
          </div>
          
          <div class="admin_row">
            <span class="admin_row_label">Speaking Score</span>
            <div class="field">${(languageQualification.speakingScore?html)!"Not Provided"}</div>
          </div>
          
          <div class="admin_row">
            <span class="admin_row_label">Listening Score</span>
            <div class="field">${(languageQualification.listeningScore?html)!"Not Provided"}</div>
          </div>    
          
          <div class="admin_row">
            <span class="admin_row_label">Did you sit the exam online?</span>
            <div class="field"><#if languageQualification.examTakenOnline>Yes<#else>No</#if></div>
         </div>
         
         <div class="admin_row">
            <span class="admin_row_label">Certificate (PDF)</span>
            <div class="field" id="referenceDocument">
                <#if languageQualification.languageQualificationDocument??>
                    <a href="<@spring.url '/download?documentId=${(encrypter.encrypt(languageQualification.languageQualificationDocument.id))!}'/>" target="_blank">${(languageQualification.languageQualificationDocument.fileName)!}</a>
                <#else>
                    Not Provided
                </#if>
            </div>         
        </div>
      </div>  
      </#if>
    
    <div class="row-group"> 
      <!-- Nationality -->
      <div class="admin_row">
        <span class="admin_row_label">Country of Residence</span>
        <div class="field">${(applicationForm.personalDetails.residenceCountry.name?html)!"Not Provided"}</div>
      </div>
      
      <!-- VISA - Passport -->
      <div class="admin_row">
        <span class="admin_row_label">Do you require a visa to study in the UK?</span>
        <div class="field">
          ${(applicationForm.personalDetails.requiresVisa?string("Yes", "No"))!"Not Provided"}
        </div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Do you have a passport?</span>
        <div class="field">
          ${(applicationForm.personalDetails.passportAvailable?string("Yes", "No"))!"Not Provided"}
        </div>
      </div> 
      
      <div class="admin_row">
        <span class="admin_row_label">Passport Number</span>
        <div class="field">${(applicationForm.personalDetails.passportInformation.passportNumber?html)!"Not Provided"}</div>                     
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Name on Passport</span>
        <div class="field">${(applicationForm.personalDetails.passportInformation.nameOnPassport?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Passport Issue Date</span>
        <div class="field">${(applicationForm.personalDetails.passportInformation.passportIssueDate?string('dd MMM yyyy'))!"Not Provided"}</div>                     
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Passport Expiry Date</span>
        <div class="field">${(applicationForm.personalDetails.passportInformation.passportExpiryDate?string('dd MMM yyyy'))!"Not Provided"}</div>                     
      </div>
      
      <!-- Contact Details -->
      <div class="admin_row">
        <span class="admin_row_label">Email</span>
        <div class="field"><a href="mailto:${applicationForm.applicant.email?html}?subject=Question Regarding UCL Prism Application ${applicationForm.applicationNumber?html}">${(applicationForm.applicant.email?html)!"Not Provided"}</a></div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Telephone</span>
        <div class="field">${(applicationForm.personalDetails.phoneNumber?html)!"Not Provided"}</div>
      </div>
      
      <div class="admin_row">
        <span class="admin_row_label">Skype Name</span>
		    <div class="field"><#if (applicationForm.personalDetails.messenger)?has_content>${(applicationForm.personalDetails.messenger?html)}<#else>Not Provided</#if></div>
      </div>
      
      <#if user == applicationForm.applicant >
      
        <div class="admin_row">
          <span class="admin_row_label">Ethnicity</span>
          <div class="field">${(applicationForm.personalDetails.ethnicity.name?html)!"Not Provided"}</div>
        </div>
        
        <div class="admin_row">
          <span class="admin_row_label">Disability</span>
          <div class="field">${(applicationForm.personalDetails.disability.name?html)!"Not Provided"}</div>
        </div>
      
      </#if>
      
    </div>            
  </form>
  
</div>
