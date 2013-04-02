<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#assign avaliableOptionsSize = (programmeInterviewers?size + previousInterviewers?size + 4)/>
<#if (avaliableOptionsSize > 25)>
<#assign avaliableOptionsSize = 25 />
</#if> 
<#assign selectedOptionsSize = (interview.interviewers?size) + 1/>
<#if (selectedOptionsSize > 25)>
<#assign selectedOptionsSize = 25 />
</#if>
<div id="section_1">
  <div class="row" >
    <label class="plain-label" for="programInterviewers">Assign Interviewers<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.assign'/>"></span>
    <div class="field"> <#--
      <select id="programInterviewers" multiple="multiple" size="${avaliableOptionsSize}">
         -->
        <select id="programInterviewers" class="list-select-from" multiple="multiple" size="8">
        <optgroup id="default" label="Default interviewers"> <#list programmeInterviewers as interviewer> <option value="${encrypter.encrypt(interviewer.id)}" category="default" <#if interviewer.isInterviewerInInterview(interview)> disabled="disabled" </#if>>
        ${interviewer.firstName?html}
        ${interviewer.lastName?html}
        </option>
        </#list> </optgroup>
        <optgroup id="previous" label="Previous interviewers"> <#list previousInterviewers as interviewer> <option value="${encrypter.encrypt(interviewer.id)}" category="previous" <#if interviewer.isInterviewerInInterview(interview)> disabled="disabled" </#if>>
        ${interviewer.firstName?html}
        ${interviewer.lastName?html}
        </option>
        </#list> </optgroup>
      </select>
    </div>
  </div>
  
  <!-- Available Reviewer Buttons -->
  <div class="row interviewer-buttons list-select-buttons">
    <div class="field"> <span>
      <button class="btn" type="button" id="addInterviewerBtn"><span class="icon-down"></span> Add</button>
      <button class="btn btn-danger" type="button" id="removeInterviewerBtn"><span class="icon-up"></span> Remove</button>
      </span> </div>
  </div>
  
  <!-- Already interviewers of this application -->
  <div class="row">
    <div class="field">
      <select id="applicationInterviewers" class="list-select-to" multiple="multiple" size="${selectedOptionsSize}">
        <#list interview.interviewers as interviewer>
        <option value="${encrypter.encrypt(interviewer.user.id)}">
        ${interviewer.user.firstName?html}
        ${interviewer.user.lastName?html}
        </option>
        </#list>
      </select>
      <@spring.bind "interview.interviewers" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error" id="interviewersErrorSpan"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
</div>
<div id="section_2">
  <p><strong>Interview Arrangements</strong></p>
  <div class="row">
    <label class="plain-label normal" for="interviewDate">Interview Date<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewDate'/>"></span>
    <div class="field"> <#if assignOnly?? && assignOnly>
      <input class="half date" disabled="disabled" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
      <#else>
      <input class="half date" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
      </#if>
      <@spring.bind "interview.interviewDueDate" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  <div class="row">
    <label class="plain-label normal">Interview Time (GMT/BST)<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewTime'/>"></span>
    <div class="field"> <#include "/private/staff/interviewers/time_dropdown.ftl"/> </div>
  </div>
  <div class="row">
    <label class="plain-label normal" for="furtherDetails">Interview Instructions for Candidate<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.instructions'/>"></span>
    <div class="field"> <#if assignOnly?? && assignOnly>
      <textarea id="furtherDetails" readonly disabled="disabled" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}
</textarea>
      <#else>
      <textarea id="furtherDetails" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}
</textarea>
      </#if>
      <@spring.bind "interview.furtherDetails" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  <div class="row">
    <label class="plain-label normal" for="interviewLocation">Interview Location</label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.location'/>"></span>
    <div class="field"> <#if assignOnly?? && assignOnly>
      <textarea id="interviewLocation" readonly disabled="disabled" name="interviewLocation" class="max" rows="1" cols="80" maxlength='5000'>${(interview.locationURL?html)!}
</textarea>
      <#else>
      <textarea id="interviewLocation" name="interviewLocation" class="max" rows="1" cols="80" maxlength="5000" placeholder="e.g. http://www.ucl.ac.uk/locations/ucl-maps/">${(interview.locationURL?html)!}
</textarea>
      </#if>
      <@spring.bind "interview.locationURL" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
</div>
