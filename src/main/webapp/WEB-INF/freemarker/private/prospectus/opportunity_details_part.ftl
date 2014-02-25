
<div class="row">
  <label class="plain-label" for="institutionCountry">Institution Country<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.institutionCountry'/>"></span>
  <div class="field">
    <select class="full selectpicker" data-live-search="true" data-size="6" id="institutionCountry" name="institutionCountry">
      <option value="">Select...</option>
      <#list countries as country>
        <option value="${encrypter.encrypt(country.id)}"
          <#if opportunityRequest.institutionCountry?? && opportunityRequest.institutionCountry.id == country.id> selected="selected"</#if>
          >${country.name?html}
        </option>
      </#list>
    </select>
    <@spring.bind "opportunityRequest.institutionCountry" />
    <#list spring.status.errorMessages as error></strong>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
    </#list>
  </div>
</div>

<div class="row">
  <label id="lbl-providerName" class="plain-label" for="institution">Institution Name<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.institutionName'/>"></span>
  <div class="field">
    <select class="full selectpicker" data-live-search="true" data-size="6"  id="institution" name="institutionCode">
      <option value="">Select...</option>
      <#if opportunityRequest.institutionCountry??>
        <#list institutions as inst>
          <option value="${inst.code}" <#if opportunityRequest.institutionCode?? && opportunityRequest.institutionCode == inst.code> selected="selected"</#if>>${inst.name?html}</option>
        </#list>
        <option value="OTHER" <#if opportunityRequest.institutionCode?? && opportunityRequest.institutionCode == "OTHER">selected="selected"</#if>>Other
        </option>
      </#if>
    </select>
    <@spring.bind "opportunityRequest.institutionCode" />
    <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
    </#list>
  </div>
</div>

<input type="hidden" id="forceCreatingNewInstitution" name="forceCreatingNewInstitution" value="${opportunityRequest.forceCreatingNewInstitution?c}" />

<div class="row">
  <label id="lbl-otherInstitutionProviderName" class="plain-label" for="otherInstitutionProviderName">Please Specify</label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.otherInstitution'/>"></span>
  <div class="field">
    <input readonly disabled="disabled" id="otherInstitution" name="otherInstitution" class="full" type="text" autocomplete="off" value="${(opportunityRequest.otherInstitution?html)!}" />
    <@spring.bind "opportunityRequest.otherInstitution" />
    <#list spring.status.errorCodes as error>
      <#if error == "institution.did.you.mean">
        <div id="didYouMeanInstitutionDiv" class="alert alert-error"> <i class="icon-warning-sign"></i>
            Did you mean:
            <#list spring.status.errorMessages[error_index]?split("::") as suggestion>
                <a name="didYouMeanInstitutionButtonYes">${suggestion?html}</a><#if suggestion_has_next>,<#else>.</#if>
            </#list>
            <a name="didYouMeanInstitutionButtonNo">Use original</a>.
        </div>
      <#else>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i>
          ${spring.status.errorMessages[error_index]}
        </div>
      </#if>
    </#list>
  </div>
</div>

<div class="row">
  <label id="programTitleLabel" class="plain-label" for="programTitle">Program Title<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.programTitle'/>"></span>
  <div class="field">
    <input id="programTitle" name="programTitle" class="full" type="text" value="${(opportunityRequest.programTitle?html)!}" />
    <@spring.bind "opportunityRequest.programTitle" />
    <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
    </#list>
  </div>
</div>

<div class="row">
  <label id="programDescriptionLabel" class="plain-label" for="programDescription">Program Description<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.programDescription'/>"></span>
  <div class="field">
    <textarea id="programDescription" name="programDescription" class="max" cols="70" rows="6">${(opportunityRequest.programDescription?html)!}</textarea>
    <@spring.bind "opportunityRequest.programDescription" />
    <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
    </#list>
  </div>
</div>

<div class="row">
  <label class="plain-label" for="studyOption">Study Options<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.studyOptions'/>"></span>
  <div class="field">
    <select multiple size="3" class="full" id="studyOptions" name="studyOptions">
      <#assign selectedOptionsString = opportunityRequest.studyOptions!"">    
      <#assign selectedOptions = selectedOptionsString?split(",")>
      <#list studyOptions as studyOption>
        <option value="${studyOption.id}"
          <#if selectedOptions?seq_contains(studyOption.id)> selected="selected"</#if>
          >${studyOption.name?html}
        </option>
      </#list>
    </select>
    <@spring.bind "opportunityRequest.studyOptions" />
    <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
    </#list>
  </div>
</div>

<div class="row">
  <label for="studyDurationNumber" class="plain-label">Duration of Study<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'prospectus.durationOfStudy'/>"></span>
  <div class="field">
      <input class="numeric input-small" type="text" size="4" id="studyDurationNumber" name="studyDurationNumber" value="${(opportunityRequest.studyDurationNumber?string)!}" />
      <select id="studyDurationUnit" name="studyDurationUnit" class="input-small">
          <#assign unit = opportunityRequest.studyDurationUnit!>
          <option value="">Select...</option>
          <option value="MONTHS" <#if unit?? && unit == "MONTHS">selected="selected"</#if>>Months</option>
          <option value="YEARS" <#if unit?? && unit == "YEARS">selected="selected"</#if>>Years</option>
      </select>
      <@spring.bind "opportunityRequest.studyDurationNumber" />
      <#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i>
          ${error}
        </div>
      </#list>
      <@spring.bind "opportunityRequest.studyDurationUnit" />
      <#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i>
          ${error}
        </div>
      </#list>
  </div>
</div>

<div class="row">
  <label class="plain-label" for="atasRequired">Does the opportunity require ATAS?<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.atasRequired'/>"></span>
  <div class="field">
    <label><input type="radio" name="atasRequired" value="true" id="atasRequired_true"
      <#if opportunityRequest.atasRequired?? && opportunityRequest.atasRequired> checked="checked" </#if>
      /> Yes</label>
    <label><input type="radio" name="atasRequired" value="false" id="atasRequired_false"
      <#if  opportunityRequest.atasRequired?? && !opportunityRequest.atasRequired> checked="checked" </#if>
      /> No</label>
    <@spring.bind "opportunityRequest.atasRequired" />
    <#list spring.status.errorMessages as error>
      <div class="alert alert-error">
       <i class="icon-warning-sign"></i>${error}
      </div>
    </#list>
  </div>
</div>

<div class="row">
  <label class="plain-label" for="advertisingDeadlineYear">Advertise deadline<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'opportunityRequest.advertisingDeadlineYear'/>"></span>
  <div class="field">
    <select class="full" id="advertisingDeadlineYear" name="advertisingDeadlineYear">
      <option value="">Select...</option>
      <#list advertisingDeadlines as advertisingDeadline>
        <option value="${advertisingDeadline?c}"
          <#if opportunityRequest.advertisingDeadlineYear?? && opportunityRequest.advertisingDeadlineYear == advertisingDeadline> selected="selected"</#if>
          >30 September ${advertisingDeadline?c}
        </option>
      </#list>
    </select>
 
    <@spring.bind "opportunityRequest.advertisingDeadlineYear" />
    <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
    </#list>
  </div>
</div>