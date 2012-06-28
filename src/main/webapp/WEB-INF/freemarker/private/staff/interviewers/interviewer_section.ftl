<div id="section_">
<div class="row" >
	<span class="plain-label">Assign Interviewers<em>*</em></span>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.assign'/>"></span>
	<div class="field">
		<#-- <select id="programInterviewers" multiple="multiple" size="${avaliableOptionsSize}"> -->
		<select id="programInterviewers" class="list-select-from" multiple="multiple" size="8">
			<optgroup id="default" label="Default interviewers">
				<#list programmeInterviewers as interviewer>
				<option value="${encrypter.encrypt(interviewer.id)}" category="default">${interviewer.firstName?html} ${interviewer.lastName?html} </option>
				</#list>
			</optgroup>
			<optgroup id="previous" label="Previous interviewers">
				<#list previousInterviewers as interviewer>
				<option value="${encrypter.encrypt(interviewer.id)}" category="previous">${interviewer.firstName?html} ${interviewer.lastName?html}</option>
				</#list>
				<#list applicationInterviewers as interviewer>
				<option value="${encrypter.encrypt(interviewer.id)}" class="selected" disabled="disabled">
					${interviewer.firstName?html} ${interviewer.lastName?html}
				</option>
				</#list>
				<#list pendingInterviewers as unsaved>									
				<option value="${encrypter.encrypt(unsaved.id)}" class="selected" disabled="disabled">
					${unsaved.firstName?html} ${unsaved.lastName?html}
				</option>
				</#list>
				<#list willingToInterviewReviewers as willingReviewer>									
				<option value="${encrypter.encrypt(willingReviewer.id)}" class="selected" disabled="disabled">
					${willingReviewer.firstName?html} ${willingReviewer.lastName?html}
				</option>
				</#list>
			</optgroup>
		</select>
	</div>
</div>

<!-- Available Reviewer Buttons -->
<div class="row interviewer-buttons list-select-buttons">
	<div class="field">
		<span>
			<button class="blue" type="button" id="addInterviewerBtn"><span class="icon-down"></span> Add</button>
			<button type="button" id="removeInterviewerBtn"><span class="icon-up"></span> Remove</button>
		</span>
	</div>
</div>

<!-- Already interviewers of this application -->
<div class="row">
	<div class="field">
		<select id="applicationInterviewers" class="list-select-to" multiple="multiple" <#if assignOnly?? && assignOnly> disabled="disabled"</#if> size="${selectedOptionsSize}">
			<#list applicationInterviewers as interviewer>
			<option value="${encrypter.encrypt(interviewer.id)}">
				${interviewer.firstName?html} ${interviewer.lastName?html}
			</option>
			</#list>
			<#list pendingInterviewers as unsaved>									
			<option value="${encrypter.encrypt(unsaved.id)}">
				${unsaved.firstName?html} ${unsaved.lastName?html}
			</option>
			</#list>
			<#list willingToInterviewReviewers as willingReviewer>									
			<option value="${encrypter.encrypt(willingReviewer.id)}">
				${willingReviewer.firstName?html} ${willingReviewer.lastName?html}
			</option>
			</#list>
		</select>
		<@spring.bind "interview.interviewers" /> 
		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
	</div>
</div>
</div>
<div id="section_2">
<p><strong>Interview Arrangements</strong></p>
<div class="row">
	<label class="plain-label normal">Interview Date<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewDate'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<input class="half date" disabled="disabled" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
		<#else>
		<input class="half date" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
		</#if>
		<@spring.bind "interview.interviewDueDate" /> 
		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
	</div>
</div>

<div class="row">
	<label class="plain-label normal">Interview Time (GMT/BST)<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewTime'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<input disabled="disabled" type="text" value="${(interview.interviewTime)!}" />
		<#else>
		<#include "/private/staff/interviewers/time_dropdown.ftl"/>
		<span class="invalid" name="timeInvalid" style="display:none;"></span>
		<@spring.bind "interview.interviewTime" /> 
		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
		</#if>
	</div>
</div>

<div class="row">
	<label class="plain-label normal">Interview Instructions<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.instructions'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<textarea id="furtherDetails" readonly="readonly" disabled="disabled" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
		<#else>
		<textarea id="furtherDetails" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
		</#if>
		<@spring.bind "interview.furtherDetails" /> 
		<#list spring.status.errorMessages as error><br /><span class="invalid">${error}</span></#list>
	</div>
</div>

<div class="row">
	<label class="plain-label normal">Interviewer Location<em>*</em></label>
	<span class="hint" data-desc="<@spring.message 'assignInterviewer.location'/>"></span>
	<div class="field">
		<#if assignOnly?? && assignOnly>
		<textarea id="interviewLocation" readonly="readonly" disabled="disabled" name="interviewLocation" class="max" rows="1" cols="80" maxlength='5000'>${(interview.locationURL?html)!}</textarea>
		<#else>
		<textarea id="interviewLocation" name="interviewLocation" class="max" rows="1" cols="80" maxlength="5000" placeholder="e.g. http://www.ucl.ac.uk/locations/ucl-maps/">${(interview.locationURL?html)!}</textarea>
		</#if>				                                            
		<@spring.bind "interview.locationURL" /> 
		<#list spring.status.errorMessages as error><br /><span class="invalid">${error}</span></#list>
	</div>
</div>
</div>