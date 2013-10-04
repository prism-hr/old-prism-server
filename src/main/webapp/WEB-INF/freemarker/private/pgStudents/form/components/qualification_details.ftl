<#assign errorCode = RequestParameters.errorCode! /> <#if applicationForm.qualifications?has_content> <#assign hasQualifications = true> <#else> <#assign hasQualifications = false> </#if> <#import "/spring.ftl" as spring /> <#setting locale = "en_US"> <a name="qualification-details"></a>
<h2 id="qualifications-H2" class="empty"> <span class="left"></span><span class="right"></span><span class="status"></span> Qualifications </h2>
<div style="display:none;"> <#if hasQualifications>
  <table class="existing table table-condensed table-bordered ">
    <colgroup>
    <col />
    <col style="width: 150px" />
    <col style="width: 36px" />
    <col style="width: 36px" />
    </colgroup>
    <thead>
      <tr>
        <th>Qualification</th>
        <th>Date</th>
        <th>&nbsp;</th>
        <th>&nbsp;</th>
      </tr>
    </thead>
    <tbody>
        <tr>
            <td colspan="4" class="scrollparent">
    		<div class="scroll">
            <table class="table-hover table-striped">
                <colgroup>
                <col />
                <col style="width: 150px" />
                <col style="width: 30px" />
                <col style="width: 30px" />
                </colgroup>
            	<tbody>
            <#list applicationForm.qualifications as existingQualification>
            <tr>
              <td><#if existingQualification.proofOfAward?? && existingQualification.proofOfAward.id?? > 
                <#assign encProofOfAwardId = encrypter.encrypt(existingQualification.proofOfAward.id) /> 
                <a href="<@spring.url '/download?documentId=${encProofOfAwardId}'/>" data-desc="Proof Of Award" class="button-hint" target="_blank"> <#if existingQualification.otherQualificationInstitution?has_content>
                ${(existingQualification.otherQualificationInstitution?html)!}
                <#else>
                ${(existingQualification.qualificationInstitution?html)!}
                </#if>
                ${(existingQualification.qualificationTitle?html)!}
                ${(existingQualification.qualificationSubject?html)!}
                (${(existingQualification.qualificationGrade?html)!})</a> <#else>
                <#if existingQualification.otherQualificationInstitution?has_content>
                ${(existingQualification.otherQualificationInstitution?html)!}
                <#else>
                ${(existingQualification.qualificationInstitution?html)!}
                </#if>
                ${(existingQualification.qualificationTitle?html)!}
                ${(existingQualification.qualificationSubject?html)!}
                (${(existingQualification.qualificationGrade?html)!}) 
                </#if> </td>
              <td><#if existingQualification.isQualificationCompleted()>
                ${(existingQualification.qualificationAwardDate?string('dd MMM yyyy'))!}
                <#else>
                ${(existingQualification.qualificationAwardDate?string('dd MMM yyyy'))!}
                (Expected) 
                </#if> </td>
              <#assign encQualificationId = encrypter.encrypt(existingQualification.id) />
              <td><a name="editQualificationLink" id="qualification_${encQualificationId}" class="button-edit button-hint" data-desc="<#if (!applicationForm.isDecided() && !applicationForm.isWithdrawn())>Edit<#else>Show</#if>">edit</a></td>
              <td><#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> <a name="deleteQualificationButton" data-desc="Delete" id="qualification_${encQualificationId}" class="button-delete button-hint">delete</a> <#else> 
                &nbsp; 
                </#if> </td>
            </tr>
            </#list>
          </tbody>
       </table>
    </div>
      </tbody>
    
  </table>
  </#if>
  <input type="hidden" id="qualificationId" name="qualificationId" value="<#if qualification?? && qualification.id??>${encrypter.encrypt(qualification.id)}</#if>" />
  <form>
    <#if errorCode?? && errorCode=="true">
    <div class="alert alert-error">
        <i class="icon-warning-sign" data-desc="Please complete all of the mandatory fields in this section."></i>
        <@spring.message 'education.qualifications.sectionInfo'/>
	</div>
    <#else>
    <div class="alert alert-info"> <i class="icon-info-sign"></i>
      <@spring.message 'education.qualifications.sectionInfo'/>
    </div>
    </#if>
    <div class="row-group">
      <div class="row">
        <label class="plain-label" for="institutionCountry">Institution Country<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'education.qualifications.institutionCountry'/>"></span>
        <div class="field"> <select class="full" id="institutionCountry" name="institutionCountry"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
          <option value="">Select...</option>
          <#list countries as country> <option value="${encrypter.encrypt(country.id)}"<#if qualification.institutionCountry?? && qualification.institutionCountry.id == country.id> selected="selected"</#if>>
          ${country.name?html}
          </option>
          </#list>
          </select>
          <@spring.bind "qualification.institutionCountry" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Provider -->
      <div class="row">
        <label id="lbl-providerName" class="plain-label grey-label" for="qualificationInstitution">Institution / Provider Name<em>*</em></label>
        <span class="hint grey" data-desc="<@spring.message 'education.qualifications.institutionName'/>"></span>
        <div class="field"> <select class="full" id="qualificationInstitution" name="qualificationInstitution"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
          <option value="">Select...</option>
          <#list institutions as inst> <option value="${inst.code}" <#if qualification.qualificationInstitutionCode?? && qualification.qualificationInstitutionCode == inst.code> selected="selected"</#if>>
          ${inst.name?html}
          </option>
          </#list> <option value="OTHER" <#if qualification.qualificationInstitutionCode?? && qualification.qualificationInstitutionCode == "OTHER">selected="selected"</#if>>Other
          </option>
          </select>
          <@spring.bind "qualification.qualificationInstitutionCode" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Other name-->
      <div class="row">
        <label id="lbl-otherInstitutionProviderName" class="plain-label grey-label" for="otherInstitutionProviderName">Please Specify<em>*</em></label>
        <span class="hint grey" data-desc="<@spring.message 'education.qualifications.subject'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input readonly disabled="disabled" id="otherInstitutionProviderName" class="full" type="text" value="${(qualification.otherQualificationInstitution?html)!}" />
          <#else>
          <input readonly id="otherInstitutionProviderName" class="full" type="text" value="${(qualification.otherQualificationInstitution?html)!}" />
          </#if>
          <@spring.bind "qualification.otherQualificationInstitution" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Qualification type -->
      <div class="row">
        <label class="plain-label" for="qqualificationType">Qualification Type<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'education.qualifications.qualificationType'/>"></span>
        <div class="field"> <select class="full" id="qqualificationType" name="qqualificationType"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
          <option value="">Select...</option>
          <#list types as type> <option value="${encrypter.encrypt(type.id)}"<#if qualification.qualificationType?? && qualification.qualificationType.id == type.id> selected="selected"</#if>>
          ${type.name?html}
          </option>
          </#list>
          </select>
          <@spring.bind "qualification.qualificationType" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Title -->
      <div class="row">
        <label class="plain-label" for="qualificationTitle">Qualification Title</label>
        <span class="hint" data-desc="<@spring.message 'education.qualifications.title'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input id="qualificationTitle" class="full" type="text" placeholder="e.g. MSc" value="${(qualification.qualificationTitle?html)!}" />
          <#else>
          <input readonly id="qualificationTitle" class="full" type="text" placeholder="e.g. Civil Engineering" value="${(qualification.qualificationTitle?html)!}" />
          </#if>
          <@spring.bind "qualification.qualificationTitle" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Subject -->
      <div class="row">
        <label class="plain-label" for="qualificationSubject">Qualification Subject<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'education.qualifications.subject'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering" value="${(qualification.qualificationSubject?html)!}" />
          <#else>
          <input readonly id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering" value="${(qualification.qualificationSubject?html)!}" />
          </#if>
          <@spring.bind "qualification.qualificationSubject" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Language (in which programme was undertaken) -->
      <div class="row">
        <label class="plain-label" for="qualificationLanguage">Language of Study<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'education.qualifications.language'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input id="qualificationLanguage" class="full" type="text" placeholder="e.g. English" value="${(qualification.qualificationLanguage?html)!}" />
          <#else>
          <input readonly id="qualificationLanguage" class="full" type="text" placeholder="e.g. English" value="${(qualification.qualificationLanguage?html)!}" />
          </#if>
          <@spring.bind "qualification.qualificationLanguage" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Start date -->
      <div class="row">
        <label class="plain-label" for="qualificationStartDate">Start Date<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'education.qualifications.startDate'/>"></span>
        <div class="field"> <input id="qualificationStartDate" class="half date" type="text" value="${(qualification.qualificationStartDate?string('dd MMM yyyy'))!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if> />
          <@spring.bind "qualification.qualificationStartDate" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    
    <div class="row-group"> 
      <!-- Has this been awarded? -->
      <div class="row"> 
      <label class="plain-label" for="currentQualificationCB">Has this qualification been awarded?</label> 
      <span class="hint" data-desc="<@spring.message 'education.qualifications.hasBeenAwarded'/>"></span>
        <div class="field"> <input type="checkbox" name="currentQualificationCB" id="currentQualificationCB"<#if qualification.isQualificationCompleted()> checked="checked"</#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> />
          <input type="hidden" name="currentQualification" id="currentQualification" value="<#if qualification.isQualificationCompleted()>YES<#else>NO</#if>" />
        </div>
      </div>
      
      <!-- Qualification grade -->
      <div class="row"> 
      <label id="quali-grad-id" class="plain-label" for="qualificationGrade"> <#if qualification.isQualificationCompleted()> Grade / Result / GPA<em>*</em> <#else> Expected Grade / Result / GPA<em>*</em> </#if> </label> <span class="hint" data-desc="<@spring.message 'education.qualifications.grade'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction" value="${(qualification.qualificationGrade?html)!}" />
          <#else>
          <input readonly id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction" value="${(qualification.qualificationGrade?html)!}" />
          </#if> 
                <@spring.bind "qualification.qualificationGrade" />
      <#list spring.status.errorMessages as error>
       <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
      </#list> 
          </div>
      </div>

      
      <!-- Award date -->
      <div class="row"> 
      <label id="quali-award-date-lb" class="plain-label" for="qualificationAwardDate"><#if qualification.isQualificationCompleted()>Award Date<#else>Expected Award Date</#if><em>*</em> </label> <span class="hint" data-desc="<@spring.message 'education.qualifications.awardDate'/>"></span>
        <div class="field" id="awardDateField"> <input type="text" class="half date" id="qualificationAwardDate" name="qualificationAwardDate" value="${(qualification.qualificationAwardDate?string('dd MMM yyyy'))!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if> /> 
        <@spring.bind "qualification.qualificationAwardDate" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
      </#list> 
        </div>
      </div>
      
      
      <!-- Attachment / supporting document -->
      <div class="row"> 
      <label id="quali-proof-of-award-lb" class="plain-label" for="proofOfAward"><#if !qualification.isQualificationCompleted()>Interim Transcript (PDF)<#else>Proof of award (PDF)</#if></label> 
      <span class="hint" data-desc="<@spring.message 'education.qualifications.proofOfAward'/>"></span>
        <div class="field <#if qualification.proofOfAward??>uploaded</#if>" id="uploadFields"> 
        
        <div class="fileupload fileupload-new" data-provides="fileupload">
            <div class="input-append">
              <div class="uneditable-input span4" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
              <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
              <input id="proofOfAward" data-type="PROOF_OF_AWARD" data-reference="Proof Of Award" class="full" type="file" name="file" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>
    
              </span> 
            </div>
        </div>
        
        <ul id="qualUploadedDocument" class="uploaded-files">
          <#if qualification.proofOfAward??> 
           <li class="done">
            <span class="uploaded-file" name="supportingDocumentSpan">
             <input type="text" class="file" id="document_PROOF_OF_AWARD" value="${(encrypter.encrypt(qualification.proofOfAward.id))!}" style="display:none;" />
           	<a class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(qualification.proofOfAward.id))!}'/>" target="_blank">${(qualification.proofOfAward.fileName?html)!}</a> 
          	<a class="btn btn-danger delete" data-desc="Delete Proof Of Award"><i class="icon-trash icon-large"></i> Delete</a> 
           </span>
          </#if>
         </ul> 
         
         
        </div>
      </div>
      
      <!-- Add another button --> 
      <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <div class="row">
        <div class="field">
          <button id="addQualificationButton" type="button" class="btn"><#if qualification?? && qualification.id??>Update<#else>Add</#if></button>
        </div>
      </div>
      </#if> </div>
    <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
    <@spring.bind "qualification.acceptedTerms" />
    <#if spring.status.errorMessages?size &gt; 0>
    <div class="alert alert-error tac" >
      <#else>
        <div class="alert tac" >
      </#if> 
      <div class="row">
       <label for="acceptTermsQDCB" class="terms-label"> Confirm that the information that you have provided in this section is true and correct. Failure to provide true and correct information may result in a subsequent offer of study being withdrawn. </label>
       <div class="terms-field">
        <input type="checkbox" name="acceptTermsQDCB" id="acceptTermsQDCB" />
        </div>
        <input type="hidden" name="acceptTermsQDValue" id="acceptTermsQDValue" />
      </div>
    </div>
    </#if>
    <div class="buttons"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <button class="btn" type="button" id="qualificationClearButton" name="qualificationClearButton">Clear</button>
      <button class="btn" type="button" id="qualificationsCloseButton" name="qualificationsCloseButton">Close</button>
      <button class="btn btn-primary" type="button" id="qualificationsSaveButton" value="add">Save</button>
      <#else>
      <button class="btn" type="button" id="qualificationsCloseButton">Close</button>
      </#if> </div>
  </form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script> 
