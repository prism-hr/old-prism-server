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
        <optgroup id="nominated" label="Applicant nominated supervisors"> 
            <#list nominatedSupervisors as interviewer> 
                <option value="${encrypter.encrypt(interviewer.id)}" category="nominated" <#if interviewer.isInterviewerInInterview(interview)> disabled="disabled" </#if>>
                ${interviewer.firstName?html}
                ${interviewer.lastName?html}
                </option>
            </#list>
        </optgroup>
        <optgroup id="default" label="Default interviewers"> 
            <#list programmeInterviewers as interviewer> 
                <option value="${encrypter.encrypt(interviewer.id)}" category="default" <#if interviewer.isInterviewerInInterview(interview)> disabled="disabled" </#if>>
                ${interviewer.firstName?html}
                ${interviewer.lastName?html}
                </option>
            </#list> 
        </optgroup>
        <optgroup id="previous" label="Previous interviewers"> 
            <#list previousInterviewers as interviewer>
                <option value="${encrypter.encrypt(interviewer.id)}" category="previous" <#if interviewer.isInterviewerInInterview(interview)> disabled="disabled" </#if>>
                ${interviewer.firstName?html}
                ${interviewer.lastName?html}
                </option>
            </#list> 
        </optgroup>
      </select>
    </div>
  </div>
  
  <!-- Available Reviewer Buttons -->
  <div class="row interviewer-buttons list-select-buttons">
    <div class="field"> <span>
      <button class="btn btn-primary" type="button" id="addInterviewerBtn"><span class="icon-down"></span> Add</button>
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
  <h3>Interview Arrangements</h3>
  <div class="row">
  	<label class="plain-label normal">Interview Status<em>*</em></label>
  	<span class="hint" data-desc="<@spring.message 'interviewArrangements.status'/>"></span>
    <div class="field">
	    <label>
		  	<input id="interviewHappened" type="radio" class="interviewHappened no-margin" name="interviewStatus" value="TAKEN_PLACE" <#if interview.stage = 'SCHEDULED' && interview.takenPlace?? && interview.takenPlace>checked</#if> />
		  	Taken place
		</label>
	  	<label>
	  		<input id="interviewScheduled" type="radio" class="interviewScheduled no-margin" name="interviewStatus" value="SCHEDULED" <#if interview.stage = 'SCHEDULED' && interview.takenPlace?? && !interview.takenPlace>checked</#if>/>
	  		Scheduled
		</label>
		<label>
			<input id="interviewToBeScheduled" type="radio" class="interviewScheduled no-margin" name="interviewStatus" value="SCHEDULING" <#if interview.stage = 'SCHEDULING'>checked</#if>/>
			To be scheduled
		</label>
	  	<@spring.bind "interview.stage" />
	  	<#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
      </#list>  
  	</div>
  </div>
  <div class="row interview-happened interview-scheduled interview-to-schedule">
  	<label class="plain-label normal" for="timezone">Time Zone<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'interviewArrangements.timezone'/>"></span>
    <div class="field">
	  	<select id="timezone" name="timezone" class="very-large">
	  		<#list availableTimeZones.timeZones as tz>
	  			<option value="${tz.timeZone.ID}"<#if tz.timeZone.ID == interview.timeZone> selected</#if>>${tz.displayName}</option>
	  		</#list>
		</select>
		<div id="submittedTimezone" style="display:none;">${interview.timeZone!}</div>
		<@spring.bind "interview.timeZone" />
    	<#list spring.status.errorMessages as error>
    		<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
    	</#list>											
	  </div>	
	  	
  </div>
  <div id="interviewDuration" class="row interview-happened interview-to-schedule interview-scheduled">
	<label class="plain-label normal" for="interviewDate">Interview Duration<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'interviewArrangements.duration'/>"></span>
	<div class="field">
		<input type="text" name="interviewDurationValue" id="interviewDurationValue" value="" class="half" />
		<input type="hidden" id="submittedInterviewDuration" value="${interview.duration!}" />
		<select name="interviewDurationUnits" id="interviewDurationUnits" class="half">
			<option value="hours">Hours</option>
			<option value="minutes">Minutes</option>
		</select>
		<@spring.bind "interview.duration" />
    	<#list spring.status.errorMessages as error>
    		<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
    	</#list>
	</div>
  </div>
  <div class="row interview-to-schedule">
  	<label class="plain-label normal" for="availableDates">Prefered Dates<em>*</em></label>
  	<script>
  		var dates = [];
  		
  		function repositionAvailableDates() {
  			var calendar = $('#availableDatesPicker');
			<#list interview.timeslots as date>
				reposition(calendar, new Date('${date.dueDate?date}'), '${date.startTime}');	
		  	</#list>
		  	
		  	setPossibleStartTimesVisibility(calendar);
  		}
  		
  		function reposition (calendar, date, time) {
			var dateText = dateToDMY(date);
			if (dates.indexOf(dateText) == -1) {
				dates.push(dateText);
				calendar.multiDatesPicker('toggleDate', dateText);
				addAvailableDate(calendar, date);
			}
			
			addAvailableTime(calendar, date, time);
		}
  	</script>
    <span class="hint" data-desc="<@spring.message 'interviewArrangements.availableDates'/>"></span>
    <div class="field">
    	<div id="availableDatesPicker" class="datepicker-inline"></div>
  	</div>
  </div>
  <div id="interviewPossibleStartTimes" class="row interview-to-schedule">
  	<table>
  		<thead>
  			<tr>
  				<th class="suggested-date"></th>
  				<th>Time 1</th>
  				<th>Time 2</th>
  				<th>Time 3</th>
  				<th class="time-hidden">Time 4</th>
  				<th class="time-hidden">Time 5</th>
  				<th class="time-hidden">Time 6</th>
  				<th class="time-hidden">Time 7</th>
  				<th class="time-hidden">Time 8</th>
  				<th class="clone-column"></th>
  				<th class="remove-column"></th>
  			</tr>
  		</thead>
  		<tbody>
  		</tbody>
  		<tfoot>
  			<td class="suggested-date"></td>
  			<td colspan="2">
  				<a href="javascript:void(0);" class="add-column">Add column</a>
  			</td>
  			<td class="time-hidden"></td>
  			<td class="time-hidden"></td>
  			<td class="time-hidden"></td>
  			<td class="time-hidden"></td>
  			<td class="time-hidden"></td>
  			<td class="clone-column"></td>
  			<td class="remove-column"></td>
  		</tfoot>
  	</table>
  </div>
  
  	<@spring.bind "interview.timeslots" />
	      <#list spring.status.errorMessages as error>
          <div class="field mpt">
		      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
              </div>
	      </#list>	
  
  <div class="row interview-happened interview-scheduled">
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
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  
  <div class="row interview-happened interview-scheduled">
    <label class="plain-label normal">Interview Time<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewTime'/>"></span>
    <div class="field"> <#include "/private/staff/interviewers/time_dropdown.ftl"/> </div>
  </div>
  
  <div class="row interview-scheduled interview-to-schedule">
    <label class="plain-label normal" for="instructionsForInterviewer">Interview Instructions (Interviewer)</label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewerInstructions'/>"></span>
    <div class="field">
    	<#if assignOnly?? && assignOnly>
	      <textarea id="furtherInterviewerDetails" readonly disabled="disabled" name="furtherInterviewerDetails" class="max" rows="6" cols="80" maxlength='2000'>${interview.furtherInterviewerDetails!}</textarea>
	    <#else>
	      <textarea id="furtherInterviewerDetails" name="furtherInterviewerDetails" class="max" rows="6" cols="80" maxlength='2000'>${interview.furtherInterviewerDetails!}</textarea>
	    </#if>
      <@spring.bind "interview.furtherInterviewerDetails" />
      <#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i>
          ${error}
        </div>
      </#list>
    </div>
  </div>

  <div class="row interview-scheduled interview-to-schedule">
    <label class="plain-label normal" for="furtherDetails">Interview Instructions  (Applicant)</label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.candidateInstructions'/>"></span>
    <div class="field"> <#if assignOnly?? && assignOnly>
      <textarea id="furtherDetails" readonly disabled="disabled" name="furtherDetails" class="max" rows="6" cols="80" maxlength='2000'>${interview.furtherDetails!}</textarea>
      <#else>
      <textarea id="furtherDetails" name="furtherDetails" class="max" rows="6" cols="80" maxlength='2000'>${interview.furtherDetails!}</textarea>
      </#if>
      <@spring.bind "interview.furtherDetails" />
      <#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i>
          ${error}
        </div>
      </#list>
    </div>
  </div>
  
  <div class="row interview-scheduled interview-to-schedule">
    <label class="plain-label normal" for="interviewLocation">Interview Location</label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.location'/>"></span>
    <div class="field"> 
    <#if assignOnly?? && assignOnly>
      <input type="text" id="interviewLocation" name="interviewLocation" class="input-xxlarge" placeholder="e.g. http://www.ucl.ac.uk/locations/ucl-maps/" value="${(interview.locationURL?html)!}">
      <#else>
      <input type="text" id="interviewLocation" name="interviewLocation" class="input-xxlarge" placeholder="e.g. http://www.ucl.ac.uk/locations/ucl-maps/" value="${(interview.locationURL?html)!}">
      </#if>
      
      <@spring.bind "interview.locationURL" />
      <#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i>
          ${error}
        </div>
      </#list>
    </div>
  </div>
  
</div>
<div id="section_4">
	
</div>