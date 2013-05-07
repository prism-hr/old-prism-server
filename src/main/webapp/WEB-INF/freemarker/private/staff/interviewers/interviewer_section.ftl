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
  	<label class="plain-label normal">Interview Status</label>
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
  	</div>
  </div>
  <div class="row interview-happened interview-scheduled interview-to-schedule">
  	<label class="plain-label normal" for="timezone">What time zone will the interview take place in?<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'interviewArrangements.timezone'/>"></span>
    <div class="field">
	  	<select id="timezone" name="timezone" class="very-large">
			<option timeZoneId="1" gmtAdjustment="GMT-12:00" useDaylightTime="0" value="-12">(GMT-12:00) International Date Line West</option>
			<option timeZoneId="2" gmtAdjustment="GMT-11:00" useDaylightTime="0" value="-11">(GMT-11:00) Midway Island, Samoa</option>
			<option timeZoneId="3" gmtAdjustment="GMT-10:00" useDaylightTime="0" value="-10">(GMT-10:00) Hawaii</option>
			<option timeZoneId="4" gmtAdjustment="GMT-09:00" useDaylightTime="1" value="-9">(GMT-09:00) Alaska</option>
			<option timeZoneId="5" gmtAdjustment="GMT-08:00" useDaylightTime="1" value="-8">(GMT-08:00) Pacific Time (US & Canada)</option>
			<option timeZoneId="6" gmtAdjustment="GMT-08:00" useDaylightTime="1" value="-8">(GMT-08:00) Tijuana, Baja California</option>
			<option timeZoneId="7" gmtAdjustment="GMT-07:00" useDaylightTime="0" value="-7">(GMT-07:00) Arizona</option>
			<option timeZoneId="8" gmtAdjustment="GMT-07:00" useDaylightTime="1" value="-7">(GMT-07:00) Chihuahua, La Paz, Mazatlan</option>
			<option timeZoneId="9" gmtAdjustment="GMT-07:00" useDaylightTime="1" value="-7">(GMT-07:00) Mountain Time (US & Canada)</option>
			<option timeZoneId="10" gmtAdjustment="GMT-06:00" useDaylightTime="0" value="-6">(GMT-06:00) Central America</option>
			<option timeZoneId="11" gmtAdjustment="GMT-06:00" useDaylightTime="1" value="-6">(GMT-06:00) Central Time (US & Canada)</option>
			<option timeZoneId="12" gmtAdjustment="GMT-06:00" useDaylightTime="1" value="-6">(GMT-06:00) Guadalajara, Mexico City, Monterrey</option>
			<option timeZoneId="13" gmtAdjustment="GMT-06:00" useDaylightTime="0" value="-6">(GMT-06:00) Saskatchewan</option>
			<option timeZoneId="14" gmtAdjustment="GMT-05:00" useDaylightTime="0" value="-5">(GMT-05:00) Bogota, Lima, Quito, Rio Branco</option>
			<option timeZoneId="15" gmtAdjustment="GMT-05:00" useDaylightTime="1" value="-5">(GMT-05:00) Eastern Time (US & Canada)</option>
			<option timeZoneId="16" gmtAdjustment="GMT-05:00" useDaylightTime="1" value="-5">(GMT-05:00) Indiana (East)</option>
			<option timeZoneId="17" gmtAdjustment="GMT-04:00" useDaylightTime="1" value="-4">(GMT-04:00) Atlantic Time (Canada)</option>
			<option timeZoneId="18" gmtAdjustment="GMT-04:00" useDaylightTime="0" value="-4">(GMT-04:00) Caracas, La Paz</option>
			<option timeZoneId="19" gmtAdjustment="GMT-04:00" useDaylightTime="0" value="-4">(GMT-04:00) Manaus</option>
			<option timeZoneId="20" gmtAdjustment="GMT-04:00" useDaylightTime="1" value="-4">(GMT-04:00) Santiago</option>
			<option timeZoneId="21" gmtAdjustment="GMT-03:30" useDaylightTime="1" value="-3.5">(GMT-03:30) Newfoundland</option>
			<option timeZoneId="22" gmtAdjustment="GMT-03:00" useDaylightTime="1" value="-3">(GMT-03:00) Brasilia</option>
			<option timeZoneId="23" gmtAdjustment="GMT-03:00" useDaylightTime="0" value="-3">(GMT-03:00) Buenos Aires, Georgetown</option>
			<option timeZoneId="24" gmtAdjustment="GMT-03:00" useDaylightTime="1" value="-3">(GMT-03:00) Greenland</option>
			<option timeZoneId="25" gmtAdjustment="GMT-03:00" useDaylightTime="1" value="-3">(GMT-03:00) Montevideo</option>
			<option timeZoneId="26" gmtAdjustment="GMT-02:00" useDaylightTime="1" value="-2">(GMT-02:00) Mid-Atlantic</option>
			<option timeZoneId="27" gmtAdjustment="GMT-01:00" useDaylightTime="0" value="-1">(GMT-01:00) Cape Verde Is.</option>
			<option timeZoneId="28" gmtAdjustment="GMT-01:00" useDaylightTime="1" value="-1">(GMT-01:00) Azores</option>
			<option timeZoneId="29" gmtAdjustment="GMT+00:00" useDaylightTime="0" value="0">(GMT+00:00) Casablanca, Monrovia, Reykjavik</option>
			<option timeZoneId="30" gmtAdjustment="GMT+00:00" useDaylightTime="1" value="0" selected>(GMT+00:00) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London</option>
			<option timeZoneId="31" gmtAdjustment="GMT+01:00" useDaylightTime="1" value="1">(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna</option>
			<option timeZoneId="32" gmtAdjustment="GMT+01:00" useDaylightTime="1" value="1">(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague</option>
			<option timeZoneId="33" gmtAdjustment="GMT+01:00" useDaylightTime="1" value="1">(GMT+01:00) Brussels, Copenhagen, Madrid, Paris</option>
			<option timeZoneId="34" gmtAdjustment="GMT+01:00" useDaylightTime="1" value="1">(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb</option>
			<option timeZoneId="35" gmtAdjustment="GMT+01:00" useDaylightTime="1" value="1">(GMT+01:00) West Central Africa</option>
			<option timeZoneId="36" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Amman</option>
			<option timeZoneId="37" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Athens, Bucharest, Istanbul</option>
			<option timeZoneId="38" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Beirut</option>
			<option timeZoneId="39" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Cairo</option>
			<option timeZoneId="40" gmtAdjustment="GMT+02:00" useDaylightTime="0" value="2">(GMT+02:00) Harare, Pretoria</option>
			<option timeZoneId="41" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius</option>
			<option timeZoneId="42" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Jerusalem</option>
			<option timeZoneId="43" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Minsk</option>
			<option timeZoneId="44" gmtAdjustment="GMT+02:00" useDaylightTime="1" value="2">(GMT+02:00) Windhoek</option>
			<option timeZoneId="45" gmtAdjustment="GMT+03:00" useDaylightTime="0" value="3">(GMT+03:00) Kuwait, Riyadh, Baghdad</option>
			<option timeZoneId="46" gmtAdjustment="GMT+03:00" useDaylightTime="1" value="3">(GMT+03:00) Moscow, St. Petersburg, Volgograd</option>
			<option timeZoneId="47" gmtAdjustment="GMT+03:00" useDaylightTime="0" value="3">(GMT+03:00) Nairobi</option>
			<option timeZoneId="48" gmtAdjustment="GMT+03:00" useDaylightTime="0" value="3">(GMT+03:00) Tbilisi</option>
			<option timeZoneId="49" gmtAdjustment="GMT+03:30" useDaylightTime="1" value="3.5">(GMT+03:30) Tehran</option>
			<option timeZoneId="50" gmtAdjustment="GMT+04:00" useDaylightTime="0" value="4">(GMT+04:00) Abu Dhabi, Muscat</option>
			<option timeZoneId="51" gmtAdjustment="GMT+04:00" useDaylightTime="1" value="4">(GMT+04:00) Baku</option>
			<option timeZoneId="52" gmtAdjustment="GMT+04:00" useDaylightTime="1" value="4">(GMT+04:00) Yerevan</option>
			<option timeZoneId="53" gmtAdjustment="GMT+04:30" useDaylightTime="0" value="4.5">(GMT+04:30) Kabul</option>
			<option timeZoneId="54" gmtAdjustment="GMT+05:00" useDaylightTime="1" value="5">(GMT+05:00) Yekaterinburg</option>
			<option timeZoneId="55" gmtAdjustment="GMT+05:00" useDaylightTime="0" value="5">(GMT+05:00) Islamabad, Karachi, Tashkent</option>
			<option timeZoneId="56" gmtAdjustment="GMT+05:30" useDaylightTime="0" value="5.5">(GMT+05:30) Sri Jayawardenapura</option>
			<option timeZoneId="57" gmtAdjustment="GMT+05:30" useDaylightTime="0" value="5.5">(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi</option>
			<option timeZoneId="58" gmtAdjustment="GMT+05:45" useDaylightTime="0" value="5.75">(GMT+05:45) Kathmandu</option>
			<option timeZoneId="59" gmtAdjustment="GMT+06:00" useDaylightTime="1" value="6">(GMT+06:00) Almaty, Novosibirsk</option>
			<option timeZoneId="60" gmtAdjustment="GMT+06:00" useDaylightTime="0" value="6">(GMT+06:00) Astana, Dhaka</option>
			<option timeZoneId="61" gmtAdjustment="GMT+06:30" useDaylightTime="0" value="6.5">(GMT+06:30) Yangon (Rangoon)</option>
			<option timeZoneId="62" gmtAdjustment="GMT+07:00" useDaylightTime="0" value="7">(GMT+07:00) Bangkok, Hanoi, Jakarta</option>
			<option timeZoneId="63" gmtAdjustment="GMT+07:00" useDaylightTime="1" value="7">(GMT+07:00) Krasnoyarsk</option>
			<option timeZoneId="64" gmtAdjustment="GMT+08:00" useDaylightTime="0" value="8">(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi</option>
			<option timeZoneId="65" gmtAdjustment="GMT+08:00" useDaylightTime="0" value="8">(GMT+08:00) Kuala Lumpur, Singapore</option>
			<option timeZoneId="66" gmtAdjustment="GMT+08:00" useDaylightTime="0" value="8">(GMT+08:00) Irkutsk, Ulaan Bataar</option>
			<option timeZoneId="67" gmtAdjustment="GMT+08:00" useDaylightTime="0" value="8">(GMT+08:00) Perth</option>
			<option timeZoneId="68" gmtAdjustment="GMT+08:00" useDaylightTime="0" value="8">(GMT+08:00) Taipei</option>
			<option timeZoneId="69" gmtAdjustment="GMT+09:00" useDaylightTime="0" value="9">(GMT+09:00) Osaka, Sapporo, Tokyo</option>
			<option timeZoneId="70" gmtAdjustment="GMT+09:00" useDaylightTime="0" value="9">(GMT+09:00) Seoul</option>
			<option timeZoneId="71" gmtAdjustment="GMT+09:00" useDaylightTime="1" value="9">(GMT+09:00) Yakutsk</option>
			<option timeZoneId="72" gmtAdjustment="GMT+09:30" useDaylightTime="0" value="9.5">(GMT+09:30) Adelaide</option>
			<option timeZoneId="73" gmtAdjustment="GMT+09:30" useDaylightTime="0" value="9.5">(GMT+09:30) Darwin</option>
			<option timeZoneId="74" gmtAdjustment="GMT+10:00" useDaylightTime="0" value="10">(GMT+10:00) Brisbane</option>
			<option timeZoneId="75" gmtAdjustment="GMT+10:00" useDaylightTime="1" value="10">(GMT+10:00) Canberra, Melbourne, Sydney</option>
			<option timeZoneId="76" gmtAdjustment="GMT+10:00" useDaylightTime="1" value="10">(GMT+10:00) Hobart</option>
			<option timeZoneId="77" gmtAdjustment="GMT+10:00" useDaylightTime="0" value="10">(GMT+10:00) Guam, Port Moresby</option>
			<option timeZoneId="78" gmtAdjustment="GMT+10:00" useDaylightTime="1" value="10">(GMT+10:00) Vladivostok</option>
			<option timeZoneId="79" gmtAdjustment="GMT+11:00" useDaylightTime="1" value="11">(GMT+11:00) Magadan, Solomon Is., New Caledonia</option>
			<option timeZoneId="80" gmtAdjustment="GMT+12:00" useDaylightTime="1" value="12">(GMT+12:00) Auckland, Wellington</option>
			<option timeZoneId="81" gmtAdjustment="GMT+12:00" useDaylightTime="0" value="12">(GMT+12:00) Fiji, Kamchatka, Marshall Is.</option>
			<option timeZoneId="82" gmtAdjustment="GMT+13:00" useDaylightTime="0" value="13">(GMT+13:00) Nuku'alofa</option>
		</select>
		<input type="hidden" id="submittedTimezone" value="${interview.timezone!}">
		<@spring.bind "interview.interviewDueDate" />
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
  	<label class="plain-label normal" for="availableDates">Select Available Dates<em>*</em></label>
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
    	<@spring.bind "interview.timeslots" />
    	<#list spring.status.errorMessages as error>
    		<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
    	</#list>	
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
  			<td class="remove-column"></td>
  		</tfoot>
  	</table>
  </div>
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
    <label class="plain-label normal">Interview Time (GMT/BST)<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewTime'/>"></span>
    <div class="field"> <#include "/private/staff/interviewers/time_dropdown.ftl"/> </div>
  </div>
  
  <div class="row interview-scheduled interview-to-schedule">
    <label class="plain-label normal" for="instructionsForInterviewer">Interview Instructions for Interviewer</label>
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
    <label class="plain-label normal" for="furtherDetails">Interview Instructions for Candidate</label>
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