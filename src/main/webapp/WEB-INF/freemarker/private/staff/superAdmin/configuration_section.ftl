<#import "/spring.ftl" as spring />
<section class="form-rows">
	<h2>Manage Service Levels</h2>
	
	<div>
		<form id="configForm">

			<div class="alert alert-info">
				<i class="icon-info-sign"></i> Edit the system configuration. <strong>Be aware that this will change the system behaviour for all programmes.</strong>
			</div>
		
			<div class="row-group" id="section-stages">
				<h3>Service Level Commitments</h3>
				
				<select id="stages" style="display: none;">
					<#list states as state>
					<option value="${stage.id}"></option>
					</#list>
				</select>

				<#list states as state>
				<div class="row"> 
					<label for="${state.id}_duration" id="${stage.displayValue()}-lbl" class="plain-label">${stage.displayValue()} Stage Duration<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'configuration.validationDuration'/> ${stage.displayValue()} stage."></span>
					<div class="field">	
						<input type="hidden" id="stage" name="stage" value="${state.id}" />
						<#if states[state]?? && states[state].duration??>  				
						<input class="numeric input-small" type="text" size="4" id="${state.id}_duration" name="${state.id}_duration" value="${state.duration?string("######")}" />
						<#else>
						<input class="numeric" type="text" size="4" id="${state.id}_duration" name="${state.id}_duration"  />
						</#if>
						<select name="${state.id}_unit" id="${state.id}_unit" class="input-small">
							<option value="">Select...</option>
							<#list units as unit>
                                <option value="${unit}" <#if  stageDurations[stage]?? && stageDurations[stage].unit?? && stageDurations[stage].unit == unit> selected="selected"</#if>>${unit.displayValue()}</option>
							</#list>
						</select>
						
            <div class="alert alert-error" id="${stage}_invalidDuration"  style="display:none;">
                   <i class="icon-warning-sign"></i> <span></span>	
            </div>
            <div class="alert alert-error" id="${stage}_invalidUnit"  style="display:none;">
                   <i class="icon-warning-sign"></i> <span></span>	
            </div>

					</div>
				</div>
				</#list>
				<input type="hidden" name="stagesDuration" id= "stagesDuration" />

			</div><!-- .row-group -->

		<!-- Configure Reminder Interval -->
			
			<div class="row-group" id="section-reminders">
				<h3>Email Notifications</h3>
				
        <select id="availableReminderIntervals" style="display: none;">
          <#list reminderIntervals as reminderInterval>
          <option value="${reminderInterval.reminderType}"></option>
          </#list>
        </select>

        <#list reminderIntervals as reminderInterval>
  				<div class="row">
  					<label for="reminderIntervalDuration" id="reminder-lbl" class="plain-label">${reminderInterval.reminderType.displayValue()} Reminder Frequency<em>*</em></label>
  				  <#assign args = [reminderInterval.reminderType.displayValue()]>
  					<span class="hint" data-desc="<@spring.messageArgs 'configuration.reminderFrequency' args />"></span>
  					<div class="field">	
  						<input class="numeric input-small" type="text" size="4" id="reminderIntervalDuration_${reminderInterval.reminderType}" name="reminderIntervalDuration" value="${(reminderInterval.duration?string("######"))!}" />
  						<select name="reminderIntervalUnit" id="reminderIntervalUnit_${reminderInterval.reminderType}" class="input-small">
  							<option value="">Select...</option>
    						<#list units as unit>
    							<option value="${unit}" <#if reminderInterval.unit == unit> selected="selected" </#if>> ${unit.displayValue()}</option>               
    						</#list>
  						</select>
  						<div class="alert alert-error" id="invalidDurationInterval_${reminderInterval.reminderType}"  style="display:none;">
                 <i class="icon-warning-sign"></i> <span></span>	
              </div>
              <div class="alert alert-error" id="invalidUnitInterval_${reminderInterval.reminderType}"  style="display:none;">
                 <i class="icon-warning-sign"></i> <span></span>	
              </div>	
  					</div>
  				</div><!-- .row -->
				</#list>
				
				<input type="hidden" name="reminderIntervals" id= "reminderIntervals" />

        <div class="row">
          <label for="notificationsDurationInput" id="notificationsDuration-lbl" class="plain-label">Expiry of Notifications<em>*</em></label>
          <span class="hint" data-desc="<@spring.message 'configuration.notificationsDuration' />"></span>
          <div class="field"> 
            <input class="numeric input-small" type="text" size="4" id="notificationsDurationInput" name="notificationsDurationInput" value="${notificationsDuration.duration}" />
            <select name="notificationsDurationUnit" id="notificationsDurationUnit" class="input-small">
              <option value="">Select...</option>
              <#list units as unit>
                <option value="${unit}" <#if notificationsDuration.unit == unit> selected="selected" </#if>> ${unit.displayValue()}</option>               
              </#list>
            </select>
            <div class="alert alert-error" id="invalidNotificationsDuration"  style="display:none;">
               <i class="icon-warning-sign"></i> <span></span>  
            </div>
            <div class="alert alert-error" id="invalidNotificationsDurationUnit"  style="display:none;">
               <i class="icon-warning-sign"></i> <span></span>  
            </div>  
          </div>
        </div><!-- .row -->
				
				<input type="hidden" name="notificationsDuration" id= "notificationsDuration" />
				
			</div><!-- .row-group -->
			
			<div class="buttons">						        		
					<button class="btn btn-primary" id="submitRUBtn" type="button" value="Submit">Submit</button>						        
			</div>
			
		</form>
	</div>
</section>
<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/configuration_section.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript">
    $(document).ready(function() {
        autosuggest($("#reg-firstname"), $("#reg-lastname"), $("#reg-email"));
    });
</script>
 