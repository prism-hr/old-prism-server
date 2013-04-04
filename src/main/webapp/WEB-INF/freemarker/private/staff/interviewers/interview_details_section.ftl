<h3>Interview Arrangements</h3>
<div class="row">
	<label class="plain-label normal" for="interviewDate">Interview Date<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewDate'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<input class="half date" disabled="disabled" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
		<#else>
		<input class="half date" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
		</#if>
		<@spring.bind "interview.interviewDueDate" /> 
		<#list spring.status.errorMessages as error> 
        <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div></#list>
	</div>
</div>

<div class="row">
	<label class="plain-label normal" for="interviewTime">Interview Time (GMT/BST)<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewTime'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<input id="interviewTime" disabled="disabled" type="text" value="${(interview.interviewTime)!}" />
		<#else>
		<#include "/private/staff/interviewers/time_dropdown.ftl"/>
		<span class="invalid" name="timeInvalid" style="display:none;"></span>
		<@spring.bind "interview.interviewTime" /> 
		<#list spring.status.errorMessages as error> 
        <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
        </#list>
		</#if>
	</div>
</div>

<div class="row">
	<label class="plain-label normal" for="furtherDetails">Interview Instructions<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.instructions'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<textarea id="furtherDetails" readonly disabled="disabled" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
		<#else>
		<textarea id="furtherDetails" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
		</#if>
		<@spring.bind "interview.furtherDetails" /> 
		<#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
        </#list>
	</div>
</div>

<div class="row">
	<label class="plain-label normal" for="interviewLocation">Interviewer Location</label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.location'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<textarea id="interviewLocation" readonly disabled="disabled" name="interviewLocation" class="max" rows="1" cols="80" maxlength='5000'>${(interview.locationURL?html)!}</textarea>
		<#else>
		<textarea id="interviewLocation" name="interviewLocation" class="max" rows="1" cols="80" maxlength="5000" placeholder="e.g. http://www.ucl.ac.uk/locations/ucl-maps/">${(interview.locationURL?html)!}</textarea>
		</#if>				                                            
		<@spring.bind "interview.locationURL" /> 
		<#list spring.status.errorMessages as error><div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div></#list>
	</div>
</div>